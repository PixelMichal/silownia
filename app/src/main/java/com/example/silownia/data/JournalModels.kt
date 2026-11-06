package com.example.silownia.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

data class Recipe(
    val id: Long? = null,
    val cat: String? = null,
    val n: String,
    val k: Int,
    val p: Int,
    val c: Int,
    val f: Int,
    val i: String,
    val s: String
)

data class PlanExercise(
    val name: String,
    val setsReps: String,
    val id: Long? = null
)

data class PlanDay(
    val dayName: String,
    val exercises: List<PlanExercise>
)

data class MealEntry(
    val id: Long,
    val d: String,
    val t: String? = null,
    val n: String,
    val k: Double,
    val p: Double,
    val c: Double,
    val f: Double
)

data class WorkoutSetEntry(
    val id: Long,
    val d: String,
    val e: String,
    val kg: Double,
    val r: Int,
    val s: Int
)

data class WeightEntry(
    val d: String,
    val kg: Double
)

data class BioData(
    val w: Double,
    val h: Double,
    val a: Int,
    val x: String, // 'm' | 'k'
    val v: String,
    val g: String // 'red' | 'utr' | 'masa'
)

data class SuppItem(
    val id: Long,
    val n: String,
    val dose: String
)

data class SleepEntry(
    val d: String,
    val b: String,
    val w: String,
    val q: Int,
    val dp: Int? = null,
    val lt: Int? = null
)

data class CustomExercise(
    val id: Long,
    val n: String,
    val sr: String,
    val day: String
)

data class StepGoalConfig(
    val v: Int,
    val g: String? = null
)

data class Goals(
    val k: Int = 2200,
    val p: Int = 150
)

data class JournalState(
    val goals: Goals = Goals(),
    val meals: List<MealEntry> = emptyList(),
    val sets: List<WorkoutSetEntry> = emptyList(),
    val weights: List<WeightEntry> = emptyList(),
    val bio: BioData? = null,
    val supps: List<SuppItem> = emptyList(),
    val sl: Map<String, List<Long>> = emptyMap(),
    val sleep: List<SleepEntry> = emptyList(),
    val sg: Double? = 8.0,
    val myrec: List<Recipe> = emptyList(),
    val myex: List<CustomExercise> = emptyList(),
    val steps: Map<String, Int> = emptyMap(),
    val stg: StepGoalConfig? = null
)

object DateUtils {
    private val ymdFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val plLocale = Locale("pl", "PL")
    private val dlabelFormat = SimpleDateFormat("d MMM", plLocale)
    private val weekdayFormat = SimpleDateFormat("EEEE", plLocale)
    private val longDateFormat = SimpleDateFormat("d MMMM yyyy", plLocale)

    fun ymd(d: Date = Date()): String = ymdFormat.format(d)

    fun dlabel(d: String): String {
        return try {
            val date = ymdFormat.parse(d) ?: return d
            dlabelFormat.format(date)
        } catch (_: Exception) {
            d
        }
    }

    fun weekday(d: String): String {
        return try {
            val date = ymdFormat.parse(d) ?: return d
            weekdayFormat.format(date).replaceFirstChar { if (it.isLowerCase()) it.titlecase(plLocale) else it.toString() }
        } catch (_: Exception) {
            d
        }
    }

    fun longDate(d: String): String {
        return try {
            val date = ymdFormat.parse(d) ?: return d
            longDateFormat.format(date)
        } catch (_: Exception) {
            d
        }
    }

    fun shiftDays(d: String, delta: Int): String {
        return try {
            val cal = Calendar.getInstance()
            cal.time = ymdFormat.parse(d) ?: Date()
            cal.add(Calendar.DAY_OF_MONTH, delta)
            ymdFormat.format(cal.time)
        } catch (_: Exception) {
            d
        }
    }
}

fun num(v: String?): Double {
    if (v.isNullOrBlank()) return 0.0
    return v.replace(',', '.').toDoubleOrNull() ?: 0.0
}

fun fmtN(n: Number): String {
    return String.format(Locale("pl", "PL"), "%,d", n.toLong()).replace('\u00A0', ' ')
}

fun dur(b: String, w: String): Int {
    fun toMin(t: String): Int {
        val parts = t.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h * 60 + m
    }
    var d = toMin(w) - toMin(b)
    if (d <= 0) d += 1440
    return d
}

fun hm(m: Int): String {
    val hours = m / 60
    val mins = m % 60
    return "$hours h ${String.format(Locale.US, "%02d", mins)} min"
}

fun calcBioTargets(b: BioData?): Goals? {
    if (b == null || b.w <= 0 || b.h <= 0 || b.a <= 0) return null
    val bmr = 10 * b.w + 6.25 * b.h - 5 * b.a + (if (b.x == "k") -161 else 5)
    val act = b.v.toDoubleOrNull() ?: 1.55
    val delta = when (b.g) {
        "red" -> -400.0
        "masa" -> 300.0
        else -> 0.0
    }
    val t = bmr * act + delta
    val k = (max(t, bmr) / 10).roundToInt() * 10
    val p = (2 * b.w).roundToInt()
    return Goals(k = k, p = p)
}
