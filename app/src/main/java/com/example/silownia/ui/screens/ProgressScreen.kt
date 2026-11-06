package com.example.silownia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silownia.data.DateUtils
import com.example.silownia.data.WeightEntry
import com.example.silownia.data.num
import com.example.silownia.ui.components.AppInput
import com.example.silownia.ui.components.Card
import com.example.silownia.ui.components.ChartCanvas
import com.example.silownia.ui.components.ChartPoint
import com.example.silownia.ui.components.DatePickerField
import com.example.silownia.ui.components.DayNav
import com.example.silownia.ui.components.Field
import com.example.silownia.ui.components.GoButton
import com.example.silownia.ui.components.ScreenScroll
import com.example.silownia.ui.components.SectionH2
import com.example.silownia.ui.components.SelectField
import com.example.silownia.ui.components.SelectOption
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.JournalViewModel
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(viewModel: JournalViewModel) {
    val state by viewModel.state.collectAsState()

    var weightKg by remember { mutableStateOf("") }
    var weightDate by remember { mutableStateOf(viewModel.today) }
    var selectedEx by remember { mutableStateOf("") }

    val sortedWeights = remember(state.weights) {
        state.weights.sortedBy { it.d }
    }

    // Last 7 days stats
    val (avgKcal, trainDays) = remember(state.meals, state.sets, viewModel.today) {
        val week = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until 7) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_MONTH, -i)
            week.add(DateUtils.ymd(c.time))
        }
        val daysWithMeals = week.filter { d -> state.meals.any { it.d == d } }
        val kc = if (daysWithMeals.isNotEmpty()) {
            (state.meals.filter { week.contains(it.d) }.sumOf { it.k } / daysWithMeals.size).roundToInt()
        } else 0
        val tr = week.count { d -> state.sets.any { it.d == d } }
        Pair(kc, tr)
    }

    val handleAddWeight = {
        if (weightKg.isNotBlank()) {
            val d = if (Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(weightDate.trim())) {
                weightDate.trim()
            } else viewModel.today
            val valKg = num(weightKg)
            if (valKg > 0) {
                viewModel.updateState { prev ->
                    val filtered = prev.weights.filter { it.d != d }
                    prev.copy(weights = filtered + WeightEntry(d, valKg))
                }
                weightKg = ""
            }
        }
    }

    val loggedExercises = remember(state.sets) {
        state.sets.map { it.e }.distinct().sorted()
    }

    val activeEx = if (loggedExercises.contains(selectedEx)) {
        selectedEx
    } else {
        loggedExercises.firstOrNull() ?: ""
    }

    val strengthPts = remember(activeEx, state.sets) {
        if (activeEx.isEmpty()) emptyList()
        else {
            val byDate = mutableMapOf<String, Double>()
            state.sets.filter { it.e == activeEx }.forEach { s ->
                val prev = byDate[s.d] ?: 0.0
                if (s.kg > prev) byDate[s.d] = s.kg
            }
            byDate.keys.sorted().map { ChartPoint(it, byDate[it] ?: 0.0) }
        }
    }

    val maxStrengthRecord = strengthPts.maxOfOrNull { it.y } ?: 0.0

    val weightDiff = if (sortedWeights.size > 1) {
        sortedWeights.last().kg - sortedWeights.first().kg
    } else 0.0

    ScreenScroll {
        DayNav(title = "Postępy")

        // 7 dni podsumowanie
        Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (avgKcal > 0) "$avgKcal" else "–",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "średnie kcal / dzień (7 dni)",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$trainDays",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "dni z treningiem (7 dni)",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
            }
        }

        // Waga ciała
        SectionH2(text = "Waga ciała")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Field(label = "Waga kg", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = weightKg,
                        onValueChange = { weightKg = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                DatePickerField(
                    label = "Data",
                    value = weightDate,
                    onValueChange = { weightDate = it },
                    modifier = Modifier.weight(1f)
                )
                GoButton(label = "Zapisz", onClick = handleAddWeight)
            }
        }

        Card {
            if (sortedWeights.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${sortedWeights.last().kg} kg",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "ostatni pomiar",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (sortedWeights.size > 1) {
                                (if (weightDiff > 0) "+" else "") + String.format(java.util.Locale.US, "%.1f", weightDiff) + " kg"
                            } else "–",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "zmiana od początku",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            ChartCanvas(pts = sortedWeights.map { ChartPoint(it.d, it.kg) })
        }

        // Siła
        SectionH2(text = "Siła")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            if (loggedExercises.isEmpty()) {
                Text(
                    text = "Zapisz pierwsze serie w zakładce Trening, a tu pojawi się wykres.",
                    fontSize = 15.sp,
                    color = AppTheme.colors.mut
                )
            } else {
                SelectField(
                    label = "Ćwiczenie",
                    value = activeEx,
                    options = loggedExercises.map { SelectOption(it, it) },
                    onSelect = { selectedEx = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Najcięższa seria w danym dniu (kg)",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut
                )
                ChartCanvas(pts = strengthPts)
                if (strengthPts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rekord: ${maxStrengthRecord} kg",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.ink
                    )
                }
            }
        }
    }
}
