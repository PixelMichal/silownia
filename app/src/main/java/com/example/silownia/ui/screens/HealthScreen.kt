package com.example.silownia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silownia.data.DateUtils
import com.example.silownia.data.JournalData
import com.example.silownia.data.SleepEntry
import com.example.silownia.data.StepGoalConfig
import com.example.silownia.data.dur
import com.example.silownia.data.fmtN
import com.example.silownia.data.hm
import com.example.silownia.data.num
import com.example.silownia.ui.components.AppInput
import com.example.silownia.ui.components.Bar
import com.example.silownia.ui.components.Card
import com.example.silownia.ui.components.ChartCanvas
import com.example.silownia.ui.components.ChartPoint
import com.example.silownia.ui.components.ChipButton
import com.example.silownia.ui.components.DayNav
import com.example.silownia.ui.components.Field
import com.example.silownia.ui.components.GoButton
import com.example.silownia.ui.components.MiniButton
import com.example.silownia.ui.components.MultiBar
import com.example.silownia.ui.components.ScreenScroll
import com.example.silownia.ui.components.SectionH2
import com.example.silownia.ui.components.SelectField
import com.example.silownia.ui.components.SelectOption
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.JournalViewModel
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HealthScreen(viewModel: JournalViewModel) {
    val state by viewModel.state.collectAsState()
    val cur by viewModel.cur.collectAsState()

    var exactSteps by remember { mutableStateOf("") }
    val goal = viewModel.stGoal()
    var customGoalInput by remember(goal) { mutableStateOf(goal.toString()) }

    var rstA by remember { mutableStateOf("") } // 'd' | 'a' | ""

    LaunchedEffect(rstA) {
        if (rstA.isNotEmpty()) {
            delay(4000)
            rstA = ""
        }
    }

    var showImportDetails by remember { mutableStateOf(false) }

    // Sleep state
    val sleepList = state.sleep
    val curSleep = sleepList.find { it.d == cur }
    val sleepGoal = state.sg ?: 8.0

    var bedTime by remember(cur, curSleep) { mutableStateOf(curSleep?.b ?: "23:00") }
    var wakeTime by remember(cur, curSleep) { mutableStateOf(curSleep?.w ?: "07:00") }
    var sleepQuality by remember(cur, curSleep) { mutableStateOf(curSleep?.q?.toString() ?: "3") }
    var deepSleep by remember(cur, curSleep) { mutableStateOf(curSleep?.dp?.toString() ?: "") }
    var lightSleep by remember(cur, curSleep) { mutableStateOf(curSleep?.lt?.toString() ?: "") }
    var sleepErr by remember(cur) { mutableStateOf("") }

    var sgInput by remember(sleepGoal) { mutableStateOf(sleepGoal.toString().removeSuffix(".0")) }

    // Steps calculations
    val bioG = state.bio?.g ?: "utr"
    val rPreset = JournalData.STEP_GOALS[bioG] ?: JournalData.STEP_GOALS["utr"]!!
    val n = viewModel.stDay(cur)
    val b = state.bio
    val weightKg = b?.w ?: 70.0
    val stride = if (b?.h != null && b.h > 0) (b.h * 0.415) / 100.0 else 0.7
    val km = (n * stride) / 1000.0
    val kc = n * 0.0005 * weightKg

    val addSteps = { delta: Int ->
        viewModel.updateState { prev ->
            val curVal = prev.steps[cur] ?: 0
            prev.copy(steps = prev.steps + (cur to max(0, curVal + delta)))
        }
    }

    val handleSetExactSteps = {
        if (exactSteps.isNotBlank()) {
            val valSteps = max(0, num(exactSteps).roundToInt())
            viewModel.updateState { it.copy(steps = it.steps + (cur to valSteps)) }
            exactSteps = ""
        }
    }

    val handleResetDaySteps = {
        if (rstA != "d") {
            rstA = "d"
        } else {
            viewModel.updateState { it.copy(steps = it.steps - cur) }
            rstA = ""
        }
    }

    val handleResetAllSteps = {
        if (rstA != "a") {
            rstA = "a"
        } else {
            viewModel.updateState { it.copy(steps = emptyMap()) }
            rstA = ""
        }
    }

    val handleSetGoalPreset = { valTarget: Int ->
        viewModel.updateState { it.copy(stg = StepGoalConfig(v = valTarget, g = bioG)) }
    }

    val handleSetGoalForm = {
        val v = num(customGoalInput).roundToInt()
        if (v in 1000..50000) {
            viewModel.updateState { it.copy(stg = StepGoalConfig(v = v)) }
        }
    }

    val handleResetGoalDefault = {
        viewModel.updateState { it.copy(stg = null) }
    }

    // Last 7 days stats
    val last7Dates = remember(viewModel.today) {
        val list = mutableListOf<String>()
        val cal = Calendar.getInstance()
        for (i in 0 until 7) {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_MONTH, -i)
            list.add(DateUtils.ymd(c.time))
        }
        list
    }
    val vals7 = last7Dates.map { viewModel.stDay(it) }.filter { it > 0 }
    val avgSteps = if (vals7.isNotEmpty()) vals7.average() else 0.0
    val hitSteps = last7Dates.count { viewModel.stDay(it) >= goal }

    val stepChartKeys = remember(state.steps) {
        state.steps.keys.filter { (state.steps[it] ?: 0) > 0 }.sorted().takeLast(14)
    }

    // Sleep handlers
    val handleSaveSleep = {
        val dpVal = if (deepSleep.trim().isEmpty()) null else max(0, num(deepSleep).roundToInt())
        val ltVal = if (lightSleep.trim().isEmpty()) null else max(0, num(lightSleep).roundToInt())
        val tot = dur(bedTime, wakeTime)
        if ((dpVal ?: 0) + (ltVal ?: 0) > tot) {
            sleepErr = "Głęboki i płytki sen razem nie mogą być dłuższe niż cała noc (${hm(tot)})."
        } else {
            sleepErr = ""
            val entry = SleepEntry(
                d = cur,
                b = bedTime.trim().ifEmpty { "23:00" },
                w = wakeTime.trim().ifEmpty { "07:00" },
                q = sleepQuality.toIntOrNull() ?: 3,
                dp = dpVal,
                lt = ltVal
            )
            viewModel.updateState { prev ->
                prev.copy(sleep = prev.sleep.filter { it.d != cur } + entry)
            }
        }
    }

    val handleDelSleep = {
        viewModel.updateState { prev ->
            prev.copy(sleep = prev.sleep.filter { it.d != cur })
        }
    }

    val handleSaveSleepGoal = {
        val targetH = num(sgInput).coerceIn(4.0, 14.0)
        viewModel.updateState { it.copy(sg = targetH) }
    }

    val last14Sleep = remember(state.sleep) {
        state.sleep.sortedBy { it.d }.takeLast(14)
    }
    val last7Sleep = remember(last14Sleep) {
        last14Sleep.takeLast(7)
    }
    val deepList = remember(last7Sleep) { last7Sleep.filter { it.dp != null } }
    val lightList = remember(last7Sleep) { last7Sleep.filter { it.lt != null } }

    val qualityOptions = (1..5).map { SelectOption(it.toString(), "$it z 5") }

    ScreenScroll {
        DayNav(
            cur = cur,
            today = viewModel.today,
            onShift = { viewModel.shift(it) }
        )

        // Kroki
        SectionH2(text = "Kroki")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = fmtN(n),
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.ink,
                    lineHeight = 58.sp
                )
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "cel ${fmtN(goal)} ",
                            fontSize = 15.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                    Text(
                        text = if (n >= goal) "cel osiągnięty" else "brakuje ${fmtN(goal - n)}",
                        fontSize = 15.sp,
                        color = AppTheme.colors.mut,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Bar(
                pct = if (goal > 0) (n.toDouble() / goal) * 100.0 else 0.0,
                over = n >= goal
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "≈ ${String.format(java.util.Locale.US, "%.1f", km)} km · ok. ${kc.roundToInt()} kcal (orientacyjnie)",
                fontSize = 14.sp,
                color = AppTheme.colors.mut
            )

            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(500, 1000, 2000, 5000).forEach { x ->
                    ChipButton(
                        label = "+ ${fmtN(x)}",
                        onClick = { addSteps(x) }
                    )
                }
                ChipButton(
                    label = "− 1 000",
                    onClick = { addSteps(-1000) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Field(label = "Ustaw dokładną liczbę", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = exactSteps,
                        onValueChange = { exactSteps = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = "np. 8500"
                    )
                }
                GoButton(label = "Zapisz", onClick = handleSetExactSteps)
            }

            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChipButton(
                    label = if (rstA == "d") "Na pewno? Dotknij ponownie" else "Wyzeruj ten dzień",
                    active = rstA == "d",
                    onClick = handleResetDaySteps
                )
                ChipButton(
                    label = if (rstA == "a") "Na pewno? Dotknij ponownie" else "Usuń historię kroków",
                    active = rstA == "a",
                    onClick = handleResetAllSteps
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showImportDetails = !showImportDetails }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showImportDetails) "▾ Automatyczny import z iPhone’a" else "▸ Automatyczny import z iPhone’a",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.ink
                )
            }
            if (showImportDetails) {
                Text(
                    text = "W aplikacji Skróty utwórz automatyzację o stałej porze (np. 22:00): pobierz kroki z dzisiaj, a potem otwórz URL z parametrem kroki=.",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut,
                    lineHeight = 20.sp
                )
            }
        }

        // Statystyki 7 dni
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
                        text = if (avgSteps > 0) fmtN(avgSteps) else "–",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "średnio dziennie (7 dni)",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$hitSteps / 7",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "dni z osiągniętym celem",
                        fontSize = 13.sp,
                        color = AppTheme.colors.mut
                    )
                }
            }
        }

        if (stepChartKeys.size > 1) {
            Card {
                ChartCanvas(
                    pts = stepChartKeys.map { d ->
                        ChartPoint(d, (state.steps[d] ?: 0).toDouble())
                    }
                )
            }
        }

        // Ile kroków dziennie?
        Card {
            Text(
                text = "Ile kroków dziennie?",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.ink
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Twój cel w profilu: ${rPreset.label}${if (state.bio?.g != null) "" else " (ustawisz go w zakładce Profil)"}.",
                fontSize = 14.sp,
                color = AppTheme.colors.mut
            )

            listOf("red", "utr", "masa").forEach { k ->
                val x = JournalData.STEP_GOALS[k]!!
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${x.label}${if (k == bioG) " · twój cel" else ""}",
                            fontSize = 16.sp,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "${fmtN(x.min)}–${fmtN(x.max)} kroków, cel ${fmtN(x.target)}",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                    MiniButton(
                        label = "Ustaw cel",
                        onClick = { handleSetGoalPreset(x.target) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Field(label = "Własny cel kroków dziennie", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = customGoalInput,
                        onValueChange = { customGoalInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = "np. 9000"
                    )
                }
                GoButton(label = "Zapisz cel", onClick = handleSetGoalForm)
            }

            if (state.stg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Przywróć cel domyślny (${fmtN(rPreset.target)})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.acc,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { handleResetGoalDefault() }
                )
            }
        }

        // Sen
        SectionH2(text = "Sen")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "Poszedłem spać", modifier = Modifier.weight(1f)) {
                    AppInput(value = bedTime, onValueChange = { bedTime = it }, placeholder = "23:00")
                }
                Field(label = "Wstałem", modifier = Modifier.weight(1f)) {
                    AppInput(value = wakeTime, onValueChange = { wakeTime = it }, placeholder = "07:00")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            SelectField(
                label = "Jakość snu",
                value = sleepQuality,
                options = qualityOptions,
                onSelect = { sleepQuality = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "Głęboki sen (min)", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = deepSleep,
                        onValueChange = { deepSleep = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = "opcjonalnie"
                    )
                }
                Field(label = "Płytki sen (min)", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = lightSleep,
                        onValueChange = { lightSleep = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = "opcjonalnie"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = if (curSleep != null) "Zaktualizuj sen" else "Zapisz sen",
                onClick = handleSaveSleep,
                modifier = Modifier.fillMaxWidth()
            )

            if (sleepErr.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = sleepErr, fontSize = 14.sp, color = AppTheme.colors.acc)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Dzień oznacza poranek, w którym wstałeś.",
                fontSize = 14.sp,
                color = AppTheme.colors.mut
            )
        }

        // Podsumowanie snu z dzisiaj
        if (curSleep != null) {
            val mi = dur(curSleep.b, curSleep.w)
            val goalMi = (sleepGoal * 60).roundToInt()
            val over = mi >= goalMi
            val hasStages = curSleep.dp != null || curSleep.lt != null
            val dp = curSleep.dp ?: 0
            val lt = curSleep.lt ?: 0
            val rest = max(0, mi - dp - lt)
            val pc = { x: Int -> if (mi > 0) (x * 100) / mi else 0 }

            Card {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = hm(mi),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Text(
                        text = "cel ${String.format(java.util.Locale.US, "%.0f", sleepGoal)} h\n" +
                                if (over) "cel osiągnięty" else "brakuje ${hm(max(0, goalMi - mi))}",
                        fontSize = 15.sp,
                        color = AppTheme.colors.mut,
                        textAlign = TextAlign.End,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Bar(
                    pct = if (goalMi > 0) (mi.toDouble() / goalMi) * 100.0 else 0.0,
                    over = over
                )

                if (hasStages) {
                    Spacer(modifier = Modifier.height(14.dp))
                    MultiBar(total = mi, dp = dp, lt = lt, rest = rest)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hm(dp),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.acc
                            )
                            Text(
                                text = "głęboki (${pc(dp)}%)",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hm(lt),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.ok
                            )
                            Text(
                                text = "płytki (${pc(lt)}%)",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = hm(rest),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.ink
                            )
                            Text(
                                text = "pozostałe (${pc(rest)}%)",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Usuń wpis",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.acc,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { handleDelSleep() }
                )
            }
        }

        // Ostatnie noce
        SectionH2(text = "Ostatnie noce")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            if (last7Sleep.isNotEmpty()) {
                val avgSleepMi = last7Sleep.map { dur(it.b, it.w) }.average().roundToInt()
                val avgQuality = last7Sleep.map { it.q }.average()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = hm(avgSleepMi),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "średnio (${last7Sleep.size} nocy)",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${String.format(java.util.Locale.US, "%.1f", avgQuality)} / 5",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.ink
                        )
                        Text(
                            text = "średnia jakość",
                            fontSize = 13.sp,
                            color = AppTheme.colors.mut
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Godziny snu w kolejnych dniach",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut
                )
            }

            ChartCanvas(
                pts = last14Sleep.map {
                    ChartPoint(it.d, (dur(it.b, it.w) / 60.0))
                }
            )

            if (deepList.isNotEmpty() || lightList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (deepList.isNotEmpty()) {
                        val avgDeep = deepList.mapNotNull { it.dp }.average().roundToInt()
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = hm(avgDeep),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.ink
                            )
                            Text(
                                text = "średnio głęboki sen",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                    }
                    if (lightList.isNotEmpty()) {
                        val avgLight = lightList.mapNotNull { it.lt }.average().roundToInt()
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = hm(avgLight),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppTheme.colors.ink
                            )
                            Text(
                                text = "średnio płytki sen",
                                fontSize = 13.sp,
                                color = AppTheme.colors.mut
                            )
                        }
                    }
                }
            }
        }

        Card {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Field(label = "Cel snu w godzinach", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = sgInput,
                        onValueChange = { sgInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                GoButton(label = "Zapisz", onClick = handleSaveSleepGoal)
            }
        }

        Card {
            Text(
                text = "Sen a regeneracja",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.ink
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Do regeneracji po treningu celuj w 7–9 godzin. Pomaga stała pora wstawania, mniej ekranu przed snem i kofeina najpóźniej we wczesnych godzinach popołudnia.",
                fontSize = 14.sp,
                color = AppTheme.colors.mut,
                lineHeight = 20.sp
            )
        }
    }
}
