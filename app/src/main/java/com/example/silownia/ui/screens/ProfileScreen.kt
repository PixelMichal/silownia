package com.example.silownia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.silownia.data.BioData
import com.example.silownia.data.WeightEntry
import com.example.silownia.data.calcBioTargets
import com.example.silownia.data.num
import com.example.silownia.ui.components.AppInput
import com.example.silownia.ui.components.Card
import com.example.silownia.ui.components.DayNav
import com.example.silownia.ui.components.Field
import com.example.silownia.ui.components.GoButton
import com.example.silownia.ui.components.ScreenScroll
import com.example.silownia.ui.components.SelectField
import com.example.silownia.ui.components.SelectOption
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.AppTab
import com.example.silownia.viewmodel.JournalViewModel
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(viewModel: JournalViewModel) {
    val state by viewModel.state.collectAsState()
    val b = state.bio

    var w by remember(b) { mutableStateOf(b?.w?.toString() ?: "") }
    var h by remember(b) { mutableStateOf(b?.h?.toString() ?: "") }
    var a by remember(b) { mutableStateOf(if (b?.a != null && b.a > 0) b.a.toString() else "") }
    var x by remember(b) { mutableStateOf(b?.x ?: "m") }
    var v by remember(b) { mutableStateOf(b?.v ?: "1.55") }
    var g by remember(b) { mutableStateOf(b?.g ?: "utr") }

    val sexOptions = listOf(
        SelectOption("m", "Mężczyzna"),
        SelectOption("k", "Kobieta")
    )

    val activityOptions = listOf(
        SelectOption("1.375", "Mała (do 3 treningów tygodniowo)"),
        SelectOption("1.55", "Umiarkowana (3–5 treningów)"),
        SelectOption("1.725", "Duża (6–7 treningów)")
    )

    val goalOptions = listOf(
        SelectOption("red", "Redukcja masy"),
        SelectOption("utr", "Utrzymanie"),
        SelectOption("masa", "Zwiększenie masy")
    )

    val handleSaveBio = {
        val wNum = num(w)
        val hNum = num(h)
        if (wNum > 0 && hNum > 0) {
            val nextBio = BioData(
                w = wNum,
                h = hNum,
                a = num(a).roundToInt(),
                x = x,
                v = v,
                g = g
            )
            viewModel.updateState { prev ->
                val filteredWeights = prev.weights.filter { it.d != viewModel.today }
                prev.copy(
                    bio = nextBio,
                    weights = filteredWeights + WeightEntry(viewModel.today, wNum)
                )
            }
        }
    }

    val targets = remember(b) { calcBioTargets(b) }

    val handleApplyGoals = {
        if (targets != null) {
            viewModel.updateState { it.copy(goals = targets) }
            viewModel.selectTab(AppTab.DIET)
        }
    }

    val bmi = if (b?.w != null && b.w > 0 && b.h > 0) {
        b.w / (b.h / 100.0).pow(2.0)
    } else null

    ScreenScroll {
        DayNav(title = "Profil")

        Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "Waga kg", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = w,
                        onValueChange = { w = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Wzrost cm", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = h,
                        onValueChange = { h = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "Wiek", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = a,
                        onValueChange = { a = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                SelectField(
                    label = "Płeć",
                    value = x,
                    options = sexOptions,
                    onSelect = { x = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            SelectField(
                label = "Aktywność",
                value = v,
                options = activityOptions,
                onSelect = { v = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            SelectField(
                label = "Cel",
                value = g,
                options = goalOptions,
                onSelect = { g = it }
            )

            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = "Zapisz profil",
                onClick = handleSaveBio,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Zapis dodaje też dzisiejszą wagę do wykresu w Postępach.",
                fontSize = 14.sp,
                color = AppTheme.colors.mut
            )
        }

        if (b != null && b.w > 0 && b.h > 0 && bmi != null) {
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
                            text = "${b.w} kg · ${b.h.roundToInt()} cm",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "aktualne dane",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = String.format(java.util.Locale.US, "%.1f", bmi),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "BMI (nie rozróżnia mięśni od tłuszczu)",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                }
            }

            if (targets != null) {
                Card {
                    Text(
                        text = "Orientacyjne cele dzienne",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${targets.k} kcal i ${targets.p} g białka (2 g na kg masy ciała). To szacunek: po 2–3 tygodniach skoryguj kalorie według zmiany wagi.",
                        fontSize = 14.sp,
                        color = AppTheme.colors.mut,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GoButton(
                        label = "Ustaw jako cele dzienne",
                        onClick = handleApplyGoals,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Podaj wiek, a wyliczę orientacyjne kalorie i białko.",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut
                )
            }
        }
    }
}
