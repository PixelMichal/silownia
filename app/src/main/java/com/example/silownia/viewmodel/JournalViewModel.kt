package com.example.silownia.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.silownia.data.DateUtils
import com.example.silownia.data.JournalData
import com.example.silownia.data.JournalRepository
import com.example.silownia.data.JournalState
import com.example.silownia.data.WorkoutSetEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

enum class AppTab(val label: String) {
    DIET("Dieta"),
    RECIPES("Przepisy"),
    TRAINING("Trening"),
    HEALTH("Zdrowie"),
    PROGRESS("Postępy"),
    PROFILE("Profil")
}

class JournalViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val repo = JournalRepository(application)

    private val _state = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    val today: String = DateUtils.ymd()

    private val _cur = MutableStateFlow(today)
    val cur: StateFlow<String> = _cur.asStateFlow()

    private val _mt = MutableStateFlow(JournalData.defMt())
    val mt: StateFlow<String> = _mt.asStateFlow()

    private val _pe = MutableStateFlow("")
    val pe: StateFlow<String> = _pe.asStateFlow()

    private val _selectedTab = MutableStateFlow(AppTab.DIET)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    // Rest timer state (seconds remaining, 0 = inactive)
    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds.asStateFlow()

    private val _restTimerTotal = MutableStateFlow(120)
    val restTimerTotal: StateFlow<Int> = _restTimerTotal.asStateFlow()

    private var timerJob: Job? = null

    // Hardware / fallback step sensor
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private var usingHardwareStepSensor = false
    private var lastCounterTotal: Int? = null
    private var stFilter = 9.81
    private var stUp = false
    private var stLast = 0L

    init {
        _state.value = repo.loadState()
        startStepSensor()
    }

    fun startStepSensor() {
        sensorManager?.unregisterListener(this)
        val stepCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val stepDetector = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (stepCounter != null) {
            val ok = sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL)
            if (ok) {
                usingHardwareStepSensor = true
                return
            }
        }
        if (stepDetector != null) {
            val ok = sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL)
            if (ok) {
                usingHardwareStepSensor = true
                return
            }
        }

        // Fallback to accelerometer if hardware step sensor is unavailable or permission not granted yet
        usingHardwareStepSensor = false
        val accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accel != null) {
            stFilter = 9.81
            stUp = false
            stLast = System.currentTimeMillis()
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun setCur(d: String) {
        _cur.value = d
    }

    fun shift(days: Int) {
        _cur.value = DateUtils.shiftDays(_cur.value, days)
    }

    fun setMt(type: String) {
        _mt.value = type
    }

    fun setPe(exercise: String) {
        _pe.value = exercise
    }

    fun updateState(updater: (JournalState) -> JournalState) {
        val updated = updater(_state.value)
        _state.value = updated
        repo.saveState(updated)
    }

    fun getExerciseNames(): List<String> {
        val seen = mutableSetOf<String>()
        _state.value.sets.forEach { seen.add(it.e) }
        JournalData.BASE_EXERCISES.forEach { seen.add(it) }
        _state.value.myex.forEach { seen.add(it.n) }
        JournalData.EXERCISE_LIBRARY.values.forEach { list -> list.forEach { seen.add(it) } }
        return seen.toList().sorted()
    }

    fun lastOf(exerciseName: String): WorkoutSetEntry? {
        val target = exerciseName.trim().lowercase()
        if (target.isEmpty()) return null
        val currentDate = _cur.value
        return _state.value.sets
            .filter { it.e.lowercase() == target && it.d < currentDate }
            .sortedWith(compareByDescending<WorkoutSetEntry> { it.d }.thenByDescending { it.id })
            .firstOrNull()
    }

    fun stGoal(): Int {
        val stg = _state.value.stg
        if (stg != null && stg.v > 0) return stg.v
        val g = _state.value.bio?.g ?: "utr"
        return JournalData.STEP_GOALS[g]?.target ?: 8000
    }

    fun stDay(d: String): Int {
        return _state.value.steps[d] ?: 0
    }

    fun waterDefaultGoal(): Int {
        val w = _state.value.bio?.w
        return if (w != null && w > 0) {
            (((w * 35.0) / 50.0).toInt() * 50).coerceIn(1500, 6000)
        } else {
            2500
        }
    }

    fun waterGoal(): Int {
        val wg = _state.value.wg
        if (wg != null && wg > 0) return wg
        return waterDefaultGoal()
    }

    fun waterDay(d: String): Int {
        return _state.value.water[d] ?: 0
    }

    // Rest timer methods
    fun startRestTimer(seconds: Int = 120) {
        timerJob?.cancel()
        _restTimerTotal.value = seconds
        _restTimerSeconds.value = seconds
        timerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1000)
                _restTimerSeconds.value -= 1
            }
            notifyRestFinished()
        }
    }

    fun addRestTime(deltaSeconds: Int) {
        val next = (_restTimerSeconds.value + deltaSeconds).coerceAtLeast(0)
        if (next == 0) {
            stopRestTimer()
        } else {
            if (next > _restTimerTotal.value) {
                _restTimerTotal.value = next
            }
            _restTimerSeconds.value = next
        }
    }

    fun stopRestTimer() {
        timerJob?.cancel()
        _restTimerSeconds.value = 0
    }

    private fun notifyRestFinished() {
        try {
            val app = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        } catch (_: Exception) {
        }
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 400)
        } catch (_: Exception) {
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSinceBoot = event.values[0].toInt()
                val prevTotal = lastCounterTotal
                lastCounterTotal = totalSinceBoot
                if (prevTotal != null) {
                    val delta = totalSinceBoot - prevTotal
                    if (delta in 1..500) {
                        val currentDay = DateUtils.ymd()
                        updateState { prev ->
                            val prevSteps = prev.steps[currentDay] ?: 0
                            prev.copy(steps = prev.steps + (currentDay to (prevSteps + delta)))
                        }
                    }
                }
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                val stepsDetected = event.values.size.coerceAtLeast(1)
                val currentDay = DateUtils.ymd()
                updateState { prev ->
                    val prevSteps = prev.steps[currentDay] ?: 0
                    prev.copy(steps = prev.steps + (currentDay to (prevSteps + stepsDetected)))
                }
            }
            Sensor.TYPE_ACCELEROMETER -> {
                if (usingHardwareStepSensor) return
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]
                val m = sqrt((ax * ax + ay * ay + az * az).toDouble())
                val t = System.currentTimeMillis()
                stFilter = stFilter * 0.6 + m * 0.4
                val d = stFilter - 9.81

                if (d > 0.7 && !stUp && (t - stLast > 320)) {
                    stUp = true
                    stLast = t
                    val currentDay = DateUtils.ymd()
                    updateState { prev ->
                        val prevSteps = prev.steps[currentDay] ?: 0
                        prev.copy(steps = prev.steps + (currentDay to (prevSteps + 1)))
                    }
                } else if (d < 0.15) {
                    stUp = false
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        sensorManager?.unregisterListener(this)
    }
}
