package com.example.silownia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silownia.data.CustomExercise
import com.example.silownia.data.DateUtils
import com.example.silownia.data.JournalData
import com.example.silownia.data.PlanDay
import com.example.silownia.data.PlanExercise
import com.example.silownia.data.WorkoutSetEntry
import com.example.silownia.data.fmtN
import com.example.silownia.data.num
import com.example.silownia.ui.components.AppInput
import com.example.silownia.ui.components.Card
import com.example.silownia.ui.components.ChipButton
import com.example.silownia.ui.components.DayNav
import com.example.silownia.ui.components.Field
import com.example.silownia.ui.components.GoButton
import com.example.silownia.ui.components.ScreenScroll
import com.example.silownia.ui.components.SectionH2
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.JournalViewModel
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrainingScreen(viewModel: JournalViewModel) {
    val state by viewModel.state.collectAsState()
    val cur by viewModel.cur.collectAsState()
    val pe by viewModel.pe.collectAsState()

    var kg by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var setsCount by remember { mutableStateOf("1") }
    var showSuggestions by remember { mutableStateOf(false) }

    var plan by remember { mutableStateOf("FBW 3 dni") }
    var eq by remember { mutableStateOf("") }
    var exg by remember { mutableStateOf("Klatka") }
    var exMax by remember { mutableIntStateOf(40) }

    var customExName by remember { mutableStateOf("") }
    var customExSr by remember { mutableStateOf("") }
    var customExDay by remember { mutableStateOf("") }

    val todaySets = state.sets.filter { it.d == cur }
    val lastEntry = remember(pe, cur, state.sets) {
        if (pe.trim().isNotEmpty()) viewModel.lastOf(pe.trim()) else null
    }

    val exNames = remember(state.sets, state.myex) {
        viewModel.getExerciseNames()
    }

    val matchingSuggestions = remember(pe, showSuggestions, exNames) {
        val q = pe.trim().lowercase()
        if (q.isEmpty() || !showSuggestions) emptyList()
        else exNames.filter { it.lowercase().contains(q) && it.lowercase() != q }.take(6)
    }

    val handleFillLast = {
        lastEntry?.let {
            kg = it.kg.toString()
            reps = it.r.toString()
            setsCount = it.s.toString()
        }
    }

    val handleAddSet = {
        val eName = pe.trim()
        if (eName.isNotEmpty() && kg.isNotBlank() && reps.isNotBlank()) {
            val newSet = WorkoutSetEntry(
                id = System.currentTimeMillis(),
                d = cur,
                e = eName,
                kg = num(kg),
                r = num(reps).roundToInt(),
                s = max(1, num(setsCount).roundToInt())
            )
            viewModel.updateState { it.copy(sets = it.sets + newSet) }
            showSuggestions = false
            kg = ""
            reps = ""
            setsCount = "1"
        }
    }

    val handleDelSet = { id: Long ->
        viewModel.updateState { it.copy(sets = it.sets.filter { s -> s.id != id }) }
    }

    val handleUseExercise = { name: String ->
        viewModel.setPe(name)
        showSuggestions = false
    }

    val planKeys = remember {
        JournalData.WORKOUT_PLANS.keys.toList() + "Mój plan"
    }

    val currentPlanDays: List<PlanDay> = remember(plan, state.myex) {
        if (plan != "Mój plan") {
            JournalData.WORKOUT_PLANS[plan] ?: emptyList()
        } else {
            val myExList = state.myex
            if (myExList.isEmpty()) {
                listOf(PlanDay("Nie masz jeszcze własnych ćwiczeń. Dodaj pierwsze poniżej.", emptyList()))
            } else {
                val groups = mutableMapOf<String, MutableList<PlanExercise>>()
                val daysOrder = mutableListOf<String>()
                myExList.forEach { x ->
                    val d = if (x.day.isNotEmpty()) x.day else "Mój plan"
                    if (!groups.containsKey(d)) {
                        groups[d] = mutableListOf()
                        daysOrder.add(d)
                    }
                    groups[d]?.add(PlanExercise(x.n, x.sr, x.id))
                }
                daysOrder.map { PlanDay(it, groups[it] ?: emptyList()) }
            }
        }
    }

    val filteredLibrary = remember(eq, exg) {
        val q = eq.trim().lowercase()
        val list = mutableListOf<String>()
        JournalData.EXERCISE_LIBRARY.forEach { (k, items) ->
            if (q.isNotEmpty() || k == exg) {
                items.forEach { n ->
                    if (q.isEmpty() || n.lowercase().contains(q)) {
                        list.add(n)
                    }
                }
            }
        }
        list
    }

    val handleAddCustomEx = {
        val n = customExName.trim()
        if (n.isNotEmpty()) {
            val newEx = CustomExercise(
                id = System.currentTimeMillis(),
                n = n,
                sr = customExSr.trim(),
                day = customExDay.trim()
            )
            viewModel.updateState { it.copy(myex = it.myex + newEx) }
            plan = "Mój plan"
            customExName = ""
            customExSr = ""
            customExDay = ""
        }
    }

    val handleDelCustomEx = { id: Long ->
        viewModel.updateState { it.copy(myex = it.myex.filter { it.id != id }) }
    }

    val totalVolume = todaySets.sumOf { (it.kg * it.r * it.s).toLong() }

    ScreenScroll {
        DayNav(
            cur = cur,
            today = viewModel.today,
            onShift = { viewModel.shift(it) }
        )

        // Dodaj serię
        SectionH2(text = "Dodaj serię")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Field(label = "Ćwiczenie") {
                AppInput(
                    value = pe,
                    onValueChange = {
                        viewModel.setPe(it)
                        showSuggestions = true
                    },
                    placeholder = "np. Przysiad ze sztangą"
                )
            }

            if (matchingSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.colors.bg)
                        .border(1.dp, AppTheme.colors.line, RoundedCornerShape(10.dp))
                ) {
                    matchingSuggestions.forEachIndexed { idx, s ->
                        if (idx > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(AppTheme.colors.line)
                            )
                        }
                        Text(
                            text = s,
                            fontSize = 15.sp,
                            color = AppTheme.colors.ink,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setPe(s)
                                    showSuggestions = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            if (lastEntry != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ostatnio (${DateUtils.dlabel(lastEntry.d)}): ${lastEntry.kg} kg × ${lastEntry.r} × ${lastEntry.s} ",
                        fontSize = 14.sp,
                        color = AppTheme.colors.mut
                    )
                    Text(
                        text = "użyj",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppTheme.colors.acc,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { handleFillLast() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "Ciężar kg", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = kg,
                        onValueChange = { kg = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Powtórzenia", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = reps,
                        onValueChange = { reps = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Field(label = "Serie", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = setsCount,
                        onValueChange = { setsCount = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = "Zapisz serię",
                onClick = handleAddSet,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Dzisiejszy trening
        SectionH2(text = "Dzisiejszy trening")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            if (todaySets.isEmpty()) {
                Text(
                    text = "Nic jeszcze nie zapisano.",
                    fontSize = 15.sp,
                    color = AppTheme.colors.mut
                )
            } else {
                todaySets.forEachIndexed { index, s ->
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(AppTheme.colors.line)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.e,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = AppTheme.colors.ink
                            )
                            Text(
                                text = "${s.kg} kg × ${s.r} powt. × ${s.s} ${if (s.s == 1) "seria" else "serie"}",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { handleDelSet(s.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "×",
                                fontSize = 22.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Objętość: ${fmtN(totalVolume)} kg",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut
                )
            }
        }

        // Przykładowe plany
        SectionH2(text = "Przykładowe plany")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            planKeys.forEach { k ->
                ChipButton(
                    label = k,
                    active = k == plan,
                    onClick = { plan = k }
                )
            }
        }

        currentPlanDays.forEach { day ->
            Card {
                Text(
                    text = day.dayName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.ink
                )
                day.exercises.forEach { ex ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppTheme.colors.bg)
                            .border(1.dp, AppTheme.colors.line, RoundedCornerShape(10.dp))
                            .clickable { handleUseExercise(ex.name) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ex.name,
                                fontSize = 16.sp,
                                color = AppTheme.colors.ink,
                                modifier = Modifier.weight(1f)
                            )
                            if (ex.setsReps.isNotEmpty()) {
                                Text(
                                    text = ex.setsReps,
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.mut
                                )
                            }
                        }
                    }
                    if (ex.id != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "usuń ćwiczenie",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppTheme.colors.acc,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { handleDelCustomEx(ex.id) }
                        )
                    }
                }
            }
        }

        Card {
            Text(
                text = "Jak robić progres",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.ink
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Zostań przy tym samym ciężarze, aż we wszystkich seriach zrobisz górną liczbę powtórzeń. Wtedy dodaj 2,5 kg i wróć do dolnego zakresu. Między seriami w ćwiczeniach wielostawowych odpoczywaj 2–3 minuty. Dotknij ćwiczenia, żeby wpisać je do formularza na górze.",
                fontSize = 14.sp,
                color = AppTheme.colors.mut,
                lineHeight = 20.sp
            )
        }

        // Biblioteka ćwiczeń
        SectionH2(text = "Biblioteka ćwiczeń (${JournalData.TOTAL_LIBRARY_EXERCISES})")
        AppInput(
            value = eq,
            onValueChange = {
                eq = it
                exMax = 40
            },
            placeholder = "Szukaj ćwiczenia"
        )

        if (eq.isEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                JournalData.EXERCISE_LIBRARY.keys.forEach { k ->
                    ChipButton(
                        label = k,
                        active = k == exg,
                        onClick = {
                            exg = k
                            exMax = 40
                        }
                    )
                }
            }
        }

        Card {
            if (filteredLibrary.isEmpty()) {
                Text(
                    text = "Nic nie znaleziono.",
                    fontSize = 15.sp,
                    color = AppTheme.colors.mut
                )
            } else {
                val toShow = filteredLibrary.take(exMax)
                toShow.forEachIndexed { index, n ->
                    if (index > 0) Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppTheme.colors.bg)
                            .border(1.dp, AppTheme.colors.line, RoundedCornerShape(10.dp))
                            .clickable { handleUseExercise(n) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = n,
                            fontSize = 16.sp,
                            color = AppTheme.colors.ink
                        )
                    }
                }

                if (filteredLibrary.size > exMax) {
                    Spacer(modifier = Modifier.height(10.dp))
                    GoButton(
                        label = "Pokaż więcej (${filteredLibrary.size - exMax})",
                        onClick = { exMax += 40 },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Dotknij ćwiczenia, żeby wpisać je do formularza serii na górze.",
            fontSize = 14.sp,
            color = AppTheme.colors.mut
        )

        // Dodaj własne ćwiczenie
        SectionH2(text = "Dodaj własne ćwiczenie")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Field(label = "Nazwa ćwiczenia") {
                AppInput(
                    value = customExName,
                    onValueChange = { customExName = it },
                    placeholder = "np. Face pull"
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "Serie × powtórzenia", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = customExSr,
                        onValueChange = { customExSr = it },
                        placeholder = "3×12"
                    )
                }
                Field(label = "Dzień planu", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = customExDay,
                        onValueChange = { customExDay = it },
                        placeholder = "np. Dzień A"
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = "Dodaj do Mojego planu",
                onClick = handleAddCustomEx,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
