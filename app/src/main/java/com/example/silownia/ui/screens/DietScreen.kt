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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silownia.data.JournalData
import com.example.silownia.data.MealEntry
import com.example.silownia.data.num
import com.example.silownia.ui.components.AppInput
import com.example.silownia.ui.components.Bar
import com.example.silownia.ui.components.Card
import com.example.silownia.ui.components.ChipButton
import com.example.silownia.ui.components.DayNav
import com.example.silownia.ui.components.Field
import com.example.silownia.ui.components.GoButton
import com.example.silownia.ui.components.MiniButton
import com.example.silownia.ui.components.ScreenScroll
import com.example.silownia.ui.components.SectionH2
import com.example.silownia.ui.components.SelectField
import com.example.silownia.ui.components.SelectOption
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.JournalViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DietScreen(viewModel: JournalViewModel) {
    val state by viewModel.state.collectAsState()
    val cur by viewModel.cur.collectAsState()
    val mt by viewModel.mt.collectAsState()

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val nameFocusRequester = remember { FocusRequester() }

    var mealName by remember { mutableStateOf("") }
    var mealK by remember { mutableStateOf("") }
    var mealP by remember { mutableStateOf("") }
    var mealC by remember { mutableStateOf("") }
    var mealF by remember { mutableStateOf("") }

    var goalK by remember(state.goals.k) { mutableStateOf(state.goals.k.toString()) }
    var goalP by remember(state.goals.p) { mutableStateOf(state.goals.p.toString()) }

    var suppName by remember { mutableStateOf("") }
    var suppDose by remember { mutableStateOf("") }

    val dayMeals = state.meals.filter { it.d == cur }
    val kTotal = dayMeals.sumOf { it.k }
    val pTotal = dayMeals.sumOf { it.p }
    val cTotal = dayMeals.sumOf { it.c }
    val fTotal = dayMeals.sumOf { it.f }

    val goals = state.goals
    val pct = if (goals.k > 0) (kTotal / goals.k.toDouble()) * 100.0 else 0.0
    val left = goals.k - kTotal

    val handleAddMeal = {
        val trimmed = mealName.trim()
        if (trimmed.isNotEmpty() && mealK.isNotBlank()) {
            val newMeal = MealEntry(
                id = System.currentTimeMillis(),
                d = cur,
                t = mt,
                n = trimmed,
                k = num(mealK),
                p = num(mealP),
                c = num(mealC),
                f = num(mealF)
            )
            viewModel.updateState { it.copy(meals = it.meals + newMeal) }
            mealName = ""
            mealK = ""
            mealP = ""
            mealC = ""
            mealF = ""
        }
    }

    val handleReMeal = { id: Long ->
        val found = state.meals.find { it.id == id }
        if (found != null) {
            val reMeal = found.copy(
                id = System.currentTimeMillis(),
                d = cur,
                t = found.t ?: mt
            )
            viewModel.updateState { it.copy(meals = it.meals + reMeal) }
        }
    }

    val handleDelMeal = { id: Long ->
        viewModel.updateState { it.copy(meals = it.meals.filter { m -> m.id != id }) }
    }

    val handleSetGoals = {
        val newK = num(goalK).roundToInt().coerceAtLeast(500)
        val newP = num(goalP).roundToInt().coerceAtLeast(20)
        viewModel.updateState { it.copy(goals = it.goals.copy(k = newK, p = newP)) }
    }

    // Recent meals
    val recentMeals = remember(state.meals) {
        val list = mutableListOf<MealEntry>()
        val seen = mutableSetOf<String>()
        state.meals.reversed().forEach { m ->
            if (seen.add(m.n) && list.size < 6) {
                list.add(m)
            }
        }
        list
    }

    // Supplements
    val suppsList = state.supps
    val doneSupps = state.sl[cur] ?: emptyList()
    val doneCount = suppsList.count { doneSupps.contains(it.id) }
    val availablePresets = JournalData.PRESET_SUPPS.filter { p -> suppsList.none { it.n == p.first } }

    val addSupp = { n: String, dose: String ->
        if (n.isNotBlank()) {
            val item = com.example.silownia.data.SuppItem(
                id = System.currentTimeMillis(),
                n = n.trim(),
                dose = dose.trim()
            )
            viewModel.updateState { it.copy(supps = it.supps + item) }
        }
    }

    val toggleSupp = { id: Long ->
        viewModel.updateState { prev ->
            val sl = prev.sl.toMutableMap()
            val list = (sl[cur] ?: emptyList()).toMutableList()
            if (list.contains(id)) list.remove(id) else list.add(id)
            sl[cur] = list
            prev.copy(sl = sl)
        }
    }

    val delSupp = { id: Long ->
        viewModel.updateState { prev ->
            prev.copy(supps = prev.supps.filter { it.id != id })
        }
    }

    val mealSelectOptions = JournalData.MEAL_TYPES.filter { it.first.isNotEmpty() }.map {
        SelectOption(it.first, it.second)
    }

    ScreenScroll(scrollState = scrollState) {
        DayNav(
            cur = cur,
            today = viewModel.today,
            onShift = { viewModel.shift(it) },
            onDateSelected = { viewModel.setCur(it) }
        )

        // Hero Card
        Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${kTotal.roundToInt()}",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.ink,
                    lineHeight = 58.sp
                )
                Text(
                    text = "z ${goals.k} kcal\n" + if (left >= 0) "zostało ${left.roundToInt()}" else "ponad o ${(-left).roundToInt()}",
                    fontSize = 15.sp,
                    color = AppTheme.colors.mut,
                    textAlign = TextAlign.End,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Bar(pct = pct)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${pTotal.roundToInt()} g",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "białko (cel ${goals.p})",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${cTotal.roundToInt()} g",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "węglowodany",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${fTotal.roundToInt()} g",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "tłuszcze",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
            }
        }

        // Dodaj posiłek
        SectionH2(text = "Dodaj posiłek")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Field(label = "Nazwa") {
                AppInput(
                    value = mealName,
                    onValueChange = { mealName = it },
                    placeholder = "np. owsianka z bananem",
                    modifier = Modifier.focusRequester(nameFocusRequester)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            SelectField(
                label = "Posiłek",
                value = mt,
                options = mealSelectOptions,
                onSelect = { viewModel.setMt(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "kcal", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = mealK,
                        onValueChange = { mealK = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Białko g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = mealP,
                        onValueChange = { mealP = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Węgl. g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = mealC,
                        onValueChange = { mealC = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Tłuszcz g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = mealF,
                        onValueChange = { mealF = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = "Dodaj posiłek",
                onClick = handleAddMeal,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Posiłki
        SectionH2(text = "Posiłki")
        JournalData.MEAL_TYPES.forEach { t ->
            val list = dayMeals.filter { (it.t ?: "") == t.first }
            if (t.first.isNotEmpty() || list.isNotEmpty()) {
                Card {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = t.second,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (list.isNotEmpty()) {
                                Text(
                                    text = "${list.sumOf { it.k }.roundToInt()} kcal",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.mut,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            if (t.first.isNotEmpty()) {
                                MiniButton(
                                    label = "+ Dodaj",
                                    onClick = {
                                        viewModel.setMt(t.first)
                                        scope.launch {
                                            scrollState.animateScrollTo(0)
                                            nameFocusRequester.requestFocus()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    list.forEachIndexed { index, m ->
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
                                    text = m.n,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = AppTheme.colors.ink
                                )
                                Text(
                                    text = "${m.k.roundToInt()} kcal · B ${m.p.roundToInt()} · W ${m.c.roundToInt()} · T ${m.f.roundToInt()}",
                                    fontSize = 13.sp,
                                    color = AppTheme.colors.mut
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable { handleDelMeal(m.id) },
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
                }
            }
        }

        // Szybko dodaj ponownie
        if (recentMeals.isNotEmpty()) {
            SectionH2(text = "Szybko dodaj ponownie")
            Card(modifier = Modifier.padding(top = 0.dp)) {
                recentMeals.forEachIndexed { index, m ->
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
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = m.n,
                                fontSize = 16.sp,
                                color = AppTheme.colors.ink
                            )
                            Text(
                                text = "${m.k.roundToInt()} kcal · B ${m.p.roundToInt()}",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                        GoButton(
                            label = "Dodaj",
                            onClick = { handleReMeal(m.id) }
                        )
                    }
                }
            }
        }

        // Cele dzienne
        SectionH2(text = "Cele dzienne")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Field(label = "kcal", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = goalK,
                        onValueChange = { goalK = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                Field(label = "Białko g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = goalP,
                        onValueChange = { goalP = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                GoButton(label = "Zapisz", onClick = handleSetGoals)
            }
        }

        // Suplementy
        SectionH2(text = "Suplementy")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            if (suppsList.isEmpty()) {
                Text(
                    text = "Dodaj suplementy, które bierzesz, a każdego dnia odhaczysz je tutaj.",
                    fontSize = 15.sp,
                    color = AppTheme.colors.mut
                )
            } else {
                Text(
                    text = "Wzięte tego dnia: $doneCount z ${suppsList.size}",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut
                )
                Spacer(modifier = Modifier.height(8.dp))
                suppsList.forEachIndexed { index, s ->
                    val isDone = doneSupps.contains(s.id)
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
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { toggleSupp(s.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDone) AppTheme.colors.ok else Color.Transparent)
                                    .border(2.dp, if (isDone) AppTheme.colors.ok else AppTheme.colors.line, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = s.n,
                                    fontSize = 16.sp,
                                    color = AppTheme.colors.ink
                                )
                                if (s.dose.isNotEmpty()) {
                                    Text(
                                        text = s.dose,
                                        fontSize = 13.sp,
                                        color = AppTheme.colors.mut
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable { delSupp(s.id) },
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
            }
        }

        if (availablePresets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availablePresets.forEach { p ->
                    ChipButton(
                        label = "+ ${p.first}",
                        onClick = { addSupp(p.first, p.second) }
                    )
                }
            }
        }

        Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Field(label = "Własny suplement", modifier = Modifier.weight(1f)) {
                    AppInput(value = suppName, onValueChange = { suppName = it })
                }
                Field(label = "Dawka", modifier = Modifier.weight(1f)) {
                    AppInput(value = suppDose, onValueChange = { suppDose = it })
                }
                GoButton(
                    label = "Dodaj",
                    onClick = {
                        addSupp(suppName, suppDose)
                        suppName = ""
                        suppDose = ""
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Dawkę dobierz według opakowania. Przy chorobach lub lekach zapytaj lekarza albo farmaceutę.",
            fontSize = 14.sp,
            color = AppTheme.colors.mut
        )
    }
}
