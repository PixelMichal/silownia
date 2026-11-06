package com.example.silownia.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import com.example.silownia.data.DateUtils
import com.example.silownia.data.JournalData
import com.example.silownia.data.JournalRepository
import com.example.silownia.data.JournalState
import com.example.silownia.data.WorkoutSetEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    // Automatic sensor step counter
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private var stFilter = 9.81
    private var stUp = false
    private var stLast = 0L

    init {
        _state.value = repo.loadState()
        startStepSensor()
    }

    private fun startStepSensor() {
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensor != null) {
            stFilter = 9.81
            stUp = false
            stLast = System.currentTimeMillis()
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
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
            updateState { prev ->
                val prevSteps = prev.steps[today] ?: 0
                prev.copy(steps = prev.steps + (today to (prevSteps + 1)))
            }
        } else if (d < 0.15) {
            stUp = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(this)
    }
}
