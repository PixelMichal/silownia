package com.example.silownia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silownia.data.DateUtils
import com.example.silownia.data.JournalData
import com.example.silownia.data.MealEntry
import com.example.silownia.data.Recipe
import com.example.silownia.data.num
import com.example.silownia.ui.components.AppInput
import com.example.silownia.ui.components.Card
import com.example.silownia.ui.components.ChipButton
import com.example.silownia.ui.components.DayNav
import com.example.silownia.ui.components.Field
import com.example.silownia.ui.components.GoButton
import com.example.silownia.ui.components.ScreenScroll
import com.example.silownia.ui.components.SectionH2
import com.example.silownia.ui.components.SelectField
import com.example.silownia.ui.components.SelectOption
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.AppTab
import com.example.silownia.viewmodel.JournalViewModel
import kotlin.math.roundToInt

@Composable
fun RecipesScreen(viewModel: JournalViewModel) {
    val state by viewModel.state.collectAsState()
    val cur by viewModel.cur.collectAsState()

    var rcat by remember { mutableStateOf("Redukcja") }
    var rq by remember { mutableStateOf("") }
    var rmt by remember { mutableStateOf("") }
    var rMax by remember { mutableIntStateOf(30) }

    var myName by remember { mutableStateOf("") }
    var myK by remember { mutableStateOf("") }
    var myP by remember { mutableStateOf("") }
    var myC by remember { mutableStateOf("") }
    var myF by remember { mutableStateOf("") }
    var myIng by remember { mutableStateOf("") }
    var mySteps by remember { mutableStateOf("") }

    val categories = listOf("Redukcja", "Masa", "Utrzymanie")

    val allCategoryRecipes = remember(rcat, state.myrec) {
        val base = JournalData.RECIPES_BY_CAT[rcat] ?: emptyList()
        val custom = state.myrec.filter { it.cat == rcat }
        base + custom
    }

    val filteredRecipes = remember(allCategoryRecipes, rq) {
        val q = rq.trim().lowercase()
        if (q.isEmpty()) allCategoryRecipes
        else allCategoryRecipes.filter { it.n.lowercase().contains(q) }
    }

    val visibleRecipes = remember(filteredRecipes, rMax) {
        filteredRecipes.take(rMax)
    }

    val rTot = filteredRecipes.size
    val rShown = visibleRecipes.size

    val handleAddRecipeToDay = { r: Recipe ->
        val chosenType = if (rmt.isNotEmpty()) rmt else JournalData.guessMt(r.n)
        val newMeal = MealEntry(
            id = System.currentTimeMillis(),
            d = cur,
            t = chosenType,
            n = r.n,
            k = r.k.toDouble(),
            p = r.p.toDouble(),
            c = r.c.toDouble(),
            f = r.f.toDouble()
        )
        viewModel.updateState { it.copy(meals = it.meals + newMeal) }
        viewModel.selectTab(AppTab.DIET)
    }

    val handleAddCustomRecipe = {
        val n = myName.trim()
        if (n.isNotEmpty() && myK.isNotBlank()) {
            val newRec = Recipe(
                id = System.currentTimeMillis(),
                cat = rcat,
                n = n,
                k = num(myK).roundToInt(),
                p = num(myP).roundToInt(),
                c = num(myC).roundToInt(),
                f = num(myF).roundToInt(),
                i = myIng.trim(),
                s = mySteps.trim()
            )
            viewModel.updateState { it.copy(myrec = it.myrec + newRec) }
            myName = ""
            myK = ""
            myP = ""
            myC = ""
            myF = ""
            myIng = ""
            mySteps = ""
        }
    }

    val handleDelCustomRecipe = { id: Long ->
        viewModel.updateState { it.copy(myrec = it.myrec.filter { r -> r.id != id }) }
    }

    val rmOptions = listOf(SelectOption("", "Automatycznie")) +
            JournalData.MEAL_TYPES.filter { it.first.isNotEmpty() }.map { SelectOption(it.first, it.second) }

    ScreenScroll {
        DayNav(title = "Przepisy")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { k ->
                ChipButton(
                    label = k,
                    active = k == rcat,
                    onClick = {
                        rcat = k
                        rMax = 30
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        AppInput(
            value = rq,
            onValueChange = {
                rq = it
                rMax = 30
            },
            placeholder = "Szukaj przepisu"
        )

        Spacer(modifier = Modifier.height(10.dp))
        SelectField(
            label = "Dodaj do posiłku",
            value = rmt,
            options = rmOptions,
            onSelect = { rmt = it }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Wartości na 1 porcję, szacunkowe. Przepis dodasz do dnia: ${DateUtils.dlabel(cur)}.",
            fontSize = 14.sp,
            color = AppTheme.colors.mut
        )

        if (visibleRecipes.isEmpty()) {
            Text(
                text = "Brak pasujących przepisów.",
                fontSize = 15.sp,
                color = AppTheme.colors.mut,
                modifier = Modifier.padding(top = 12.dp)
            )
        } else {
            visibleRecipes.forEach { r ->
                Card {
                    Text(
                        text = r.n,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.ink
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${r.k} kcal · B ${r.p} · W ${r.c} · T ${r.f}",
                        fontSize = 14.sp,
                        color = AppTheme.colors.mut,
                        lineHeight = 20.sp
                    )
                    if (r.i.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = r.i,
                            fontSize = 15.sp,
                            color = AppTheme.colors.ink,
                            lineHeight = 22.sp
                        )
                    }
                    if (r.s.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = r.s,
                            fontSize = 14.sp,
                            color = AppTheme.colors.mut,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GoButton(
                        label = "Dodaj do dnia",
                        onClick = { handleAddRecipeToDay(r) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (r.id != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Własny przepis · ",
                                fontSize = 14.sp,
                                color = AppTheme.colors.mut
                            )
                            Text(
                                text = "usuń",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppTheme.colors.acc,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable { handleDelCustomRecipe(r.id) }
                            )
                        }
                    }
                }
            }
        }

        if (rTot > 0) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Pokazano $rShown z $rTot.",
                fontSize = 14.sp,
                color = AppTheme.colors.mut
            )
        }

        if (rTot > rShown) {
            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = "Pokaż więcej (${rTot - rShown})",
                onClick = { rMax += 30 },
                modifier = Modifier.fillMaxWidth()
            )
        }

        SectionH2(text = "Dodaj własny przepis")
        Card(modifier = Modifier.padding(top = 0.dp)) {
            Field(label = "Nazwa") {
                AppInput(value = myName, onValueChange = { myName = it })
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Field(label = "kcal", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = myK,
                        onValueChange = { myK = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Białko g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = myP,
                        onValueChange = { myP = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Węgl. g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = myC,
                        onValueChange = { myC = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Field(label = "Tłuszcz g", modifier = Modifier.weight(1f)) {
                    AppInput(
                        value = myF,
                        onValueChange = { myF = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Field(label = "Składniki") {
                AppInput(
                    value = myIng,
                    onValueChange = { myIng = it },
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.height(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Field(label = "Sposób przygotowania") {
                AppInput(
                    value = mySteps,
                    onValueChange = { mySteps = it },
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.height(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            GoButton(
                label = "Zapisz w kategorii: $rcat",
                onClick = handleAddCustomRecipe,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
