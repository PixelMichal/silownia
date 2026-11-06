package com.example.silownia.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.silownia.data.DateUtils
import com.example.silownia.ui.theme.AppTheme
import com.example.silownia.viewmodel.AppTab
import java.util.Calendar
import java.util.Locale

@Composable
fun ScreenScroll(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.bg)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            content = content
        )
    }
}

@Composable
fun ScreenLazyColumn(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.bg)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            content = content
        )
    }
}

@Composable
fun DayNav(
    title: String? = null,
    cur: String = "",
    today: String = "",
    onShift: (Int) -> Unit = {},
    onDateSelected: ((String) -> Unit)? = null
) {
    val context = LocalContext.current

    if (title != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.ink,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val openCalendar = {
        val parts = cur.split("-")
        val cal = Calendar.getInstance()
        val year = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.YEAR)
        val month = (parts.getOrNull(1)?.toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)) - 1
        val day = parts.getOrNull(2)?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            context,
            { _, y, m, d ->
                val formatted = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                onDateSelected?.invoke(formatted)
            },
            year,
            month,
            day
        ).show()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.card)
                .border(1.dp, AppTheme.colors.line, CircleShape)
                .clickable { onShift(-1) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.ink
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = onDateSelected != null) { openCalendar() }
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (cur == today) "Dziś" else DateUtils.weekday(cur),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.ink,
                textAlign = TextAlign.Center
            )
            Text(
                text = DateUtils.longDate(cur),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.colors.mut,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.card)
                .border(1.dp, AppTheme.colors.line, CircleShape)
                .clickable { onShift(1) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "›",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.ink
            )
        }
    }
}

@Composable
fun SectionH2(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = AppTheme.colors.ink,
        modifier = modifier.padding(top = 22.dp, bottom = 10.dp)
    )
}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.card,
        border = BorderStroke(1.dp, AppTheme.colors.line)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

@Composable
fun Bar(
    pct: Double,
    modifier: Modifier = Modifier,
    over: Boolean = false
) {
    val clamped = pct.coerceIn(0.0, 100.0).toFloat() / 100f
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(AppTheme.colors.line)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(if (over) AppTheme.colors.ok else AppTheme.colors.acc)
        )
    }
}

@Composable
fun MultiBar(
    total: Int,
    dp: Int,
    lt: Int,
    rest: Int,
    modifier: Modifier = Modifier
) {
    if (total <= 0) return
    val dpPct = (dp.toFloat() / total).coerceIn(0f, 1f)
    val ltPct = (lt.toFloat() / total).coerceIn(0f, 1f)
    val restPct = (rest.toFloat() / total).coerceIn(0f, 1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(AppTheme.colors.line)
    ) {
        if (dpPct > 0) {
            Box(
                modifier = Modifier
                    .weight(dpPct)
                    .fillMaxHeight()
                    .background(AppTheme.colors.acc)
            )
        }
        if (ltPct > 0) {
            Box(
                modifier = Modifier
                    .weight(ltPct)
                    .fillMaxHeight()
                    .background(AppTheme.colors.ok)
            )
        }
        if (restPct > 0) {
            Box(
                modifier = Modifier
                    .weight(restPct)
                    .fillMaxHeight()
                    .background(AppTheme.colors.mut)
            )
        }
    }
}

@Composable
fun GoButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.colors.acc)
            .clickable(onClick = onClick)
            .heightIn(min = 46.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.accInk,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MiniButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AppTheme.colors.bg)
            .border(1.dp, AppTheme.colors.line, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .heightIn(min = 40.dp)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.ink,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChipButton(
    label: String,
    active: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(if (active) AppTheme.colors.acc else AppTheme.colors.card)
            .border(
                1.dp,
                if (active) AppTheme.colors.acc else AppTheme.colors.line,
                RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (active) AppTheme.colors.accInk else AppTheme.colors.ink,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Field(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = AppTheme.colors.mut
        )
        content()
    }
}

@Composable
fun AppInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp),
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(text = placeholder, color = AppTheme.colors.mut, fontSize = 15.sp)
            }
        },
        singleLine = singleLine,
        maxLines = maxLines,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AppTheme.colors.ink,
            unfocusedTextColor = AppTheme.colors.ink,
            focusedContainerColor = AppTheme.colors.bg,
            unfocusedContainerColor = AppTheme.colors.bg,
            focusedBorderColor = AppTheme.colors.acc,
            unfocusedBorderColor = AppTheme.colors.line,
            cursorColor = AppTheme.colors.acc
        ),
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun TimePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parts = value.split(":")
    val initHour = parts.getOrNull(0)?.toIntOrNull() ?: 23
    val initMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    Field(label = label, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.colors.bg)
                .border(1.dp, AppTheme.colors.line, RoundedCornerShape(10.dp))
                .clickable {
                    TimePickerDialog(
                        context,
                        { _, h, m ->
                            onValueChange(String.format(Locale.US, "%02d:%02d", h, m))
                        },
                        initHour,
                        initMinute,
                        true
                    ).show()
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = AppTheme.colors.ink
                )
                Text(
                    text = "🕒",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val parts = value.split("-")
    val cal = Calendar.getInstance()
    val initYear = parts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.YEAR)
    val initMonth = (parts.getOrNull(1)?.toIntOrNull() ?: (cal.get(Calendar.MONTH) + 1)) - 1
    val initDay = parts.getOrNull(2)?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)

    Field(label = label, modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.colors.bg)
                .border(1.dp, AppTheme.colors.line, RoundedCornerShape(10.dp))
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            onValueChange(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d))
                        },
                        initYear,
                        initMonth,
                        initDay
                    ).show()
                }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    color = AppTheme.colors.ink
                )
                Text(
                    text = "📅",
                    fontSize = 14.sp
                )
            }
        }
    }
}

data class SelectOption(
    val value: String,
    val label: String
)

@Composable
fun SelectField(
    value: String,
    options: List<SelectOption>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    var openDialog by remember { mutableStateOf(false) }
    val currentLabel = options.firstOrNull { it.value == value }?.label ?: value

    val triggerContent = @Composable {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AppTheme.colors.bg)
                .border(1.dp, AppTheme.colors.line, RoundedCornerShape(10.dp))
                .clickable { openDialog = true }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentLabel,
                    fontSize = 15.sp,
                    color = AppTheme.colors.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = "▾",
                    fontSize = 14.sp,
                    color = AppTheme.colors.mut
                )
            }
        }
    }

    if (label != null) {
        Field(label = label, modifier = modifier) {
            triggerContent()
        }
    } else {
        Box(modifier = modifier) {
            triggerContent()
        }
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = {
                Text(
                    text = label ?: "Wybierz opcję",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.ink
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    items(options) { opt ->
                        val selected = opt.value == value
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) AppTheme.colors.bg else Color.Transparent)
                                .clickable {
                                    onSelect(opt.value)
                                    openDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = opt.label,
                                fontSize = 16.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) AppTheme.colors.acc else AppTheme.colors.ink
                            )
                            if (selected) {
                                Text(
                                    text = "✓",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppTheme.colors.acc
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text("Zamknij", color = AppTheme.colors.acc)
                }
            },
            containerColor = AppTheme.colors.card
        )
    }
}

data class ChartPoint(
    val d: String,
    val y: Double
)

@Composable
fun ChartCanvas(
    pts: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    if (pts.size < 2) {
        Text(
            text = "Potrzeba co najmniej dwóch pomiarów, żeby narysować wykres.",
            fontSize = 15.sp,
            color = AppTheme.colors.mut,
            modifier = modifier.padding(top = 6.dp)
        )
        return
    }

    val lineColor = AppTheme.colors.line
    val accColor = AppTheme.colors.acc
    val mutColor = AppTheme.colors.mut
    val density = LocalDensity.current

    val ys = pts.map { it.y }
    var lo = ys.minOrNull() ?: 0.0
    var hi = ys.maxOrNull() ?: 1.0
    if (hi == lo) {
        hi += 1.0
        lo -= 1.0
    }

    val textPaint = remember(density, mutColor) {
        android.graphics.Paint().apply {
            color = mutColor.toArgb()
            textSize = with(density) { 11.sp.toPx() }
            isAntiAlias = true
        }
    }

    val rightTextPaint = remember(density, mutColor) {
        android.graphics.Paint().apply {
            color = mutColor.toArgb()
            textSize = with(density) { 11.sp.toPx() }
            textAlign = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(165.dp)
            .padding(top = 8.dp)
    ) {
        val w = size.width
        val h = size.height
        val paddingLeft = 40.dp.toPx()
        val paddingRight = 10.dp.toPx()
        val bottomAreaHeight = 24.dp.toPx()
        val topAreaHeight = 16.dp.toPx()
        val chartHeight = h - bottomAreaHeight - topAreaHeight

        fun getX(i: Int): Float {
            val count = pts.size - 1
            return paddingLeft + (i.toFloat() / count) * (w - paddingLeft - paddingRight)
        }

        fun getY(v: Double): Float {
            val ratio = ((v - lo) / (hi - lo)).toFloat()
            return topAreaHeight + (1f - ratio) * chartHeight
        }

        val yHi = getY(hi)
        val yLo = getY(lo)
        drawLine(
            color = lineColor,
            start = Offset(paddingLeft, yHi),
            end = Offset(w - paddingRight, yHi),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = lineColor,
            start = Offset(paddingLeft, yLo),
            end = Offset(w - paddingRight, yLo),
            strokeWidth = 1.dp.toPx()
        )

        val hiStr = String.format(Locale.US, "%.1f", hi)
        val loStr = String.format(Locale.US, "%.1f", lo)
        drawContext.canvas.nativeCanvas.drawText(hiStr, 0f, yHi + 4.dp.toPx(), textPaint)
        drawContext.canvas.nativeCanvas.drawText(loStr, 0f, yLo + 4.dp.toPx(), textPaint)

        val path = Path()
        pts.forEachIndexed { i, p ->
            val px = getX(i)
            val py = getY(p.y)
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        drawPath(
            path = path,
            color = accColor,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        pts.forEachIndexed { i, p ->
            val px = getX(i)
            val py = getY(p.y)
            drawCircle(
                color = accColor,
                radius = 3.5.dp.toPx(),
                center = Offset(px, py)
            )
        }

        val firstDateLabel = DateUtils.dlabel(pts.first().d)
        val lastDateLabel = DateUtils.dlabel(pts.last().d)
        drawContext.canvas.nativeCanvas.drawText(
            firstDateLabel,
            paddingLeft,
            h - 4.dp.toPx(),
            textPaint
        )
        drawContext.canvas.nativeCanvas.drawText(
            lastDateLabel,
            w - paddingRight,
            h - 4.dp.toPx(),
            rightTextPaint
        )
    }
}

@Composable
fun AppBottomBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = AppTheme.colors.card,
        border = BorderStroke(1.dp, AppTheme.colors.line)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            AppTab.entries.forEach { tab ->
                val isSelected = tab == currentTab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 12.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(if (isSelected) AppTheme.colors.acc else Color.Transparent)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) AppTheme.colors.ink else AppTheme.colors.mut,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
