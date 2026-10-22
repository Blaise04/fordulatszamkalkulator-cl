package hu.csiszolotarcsawebshop.fordulatszam

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- Fejléc: logó (webshop link), hívás, email, sötét mód ----------

@Composable
fun TopBar(dark: Boolean, onToggleDark: () -> Unit) {
    val c = LocalAppColors.current
    val context = LocalContext.current
    Column(Modifier.background(c.bg).statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(start = 16.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable(role = Role.Button) { context.openUrl(Calc.SHOP_URL) }
                    .semantics { contentDescription = "Csiszolótárcsa WebShop – webshop megnyitása" }
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 150.dp, height = 35.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                RoundIconButton(
                    icon = AppIcons.Phone,
                    description = "Hívás: ${Calc.PHONE_DISPLAY}",
                    background = c.softBg,
                    tint = c.softText,
                    onClick = { context.dialShop() },
                )
                RoundIconButton(
                    icon = AppIcons.Mail,
                    description = "Email küldése",
                    background = c.softBg,
                    tint = c.softText,
                    onClick = { context.emailShop() },
                )
                RoundIconButton(
                    icon = if (dark) AppIcons.Sun else AppIcons.Moon,
                    description = if (dark) "Világos mód" else "Sötét mód",
                    background = c.surface,
                    tint = c.text,
                    borderColor = c.border,
                    onClick = onToggleDark,
                )
            }
        }
        HorizontalDivider(color = c.divider)
    }
}

@Composable
fun RoundIconButton(
    icon: ImageVector,
    description: String,
    background: Color,
    tint: Color,
    borderColor: Color? = null,
    onClick: () -> Unit,
) {
    val base = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(background)
    val bordered = if (borderColor != null) base.border(1.5.dp, borderColor, CircleShape) else base
    Box(
        modifier = bordered
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

// ---------- Alsó navigáció ----------

enum class Tab { RPM, SPEED, DIAMETER }

@Composable
fun BottomNav(selected: Tab?, onSelect: (Tab) -> Unit) {
    val c = LocalAppColors.current
    Column(Modifier.background(c.navBg).navigationBarsPadding()) {
        HorizontalDivider(color = c.divider)
        Row(
            Modifier
                .fillMaxWidth()
                .height(84.dp)
                .selectableGroup()
        ) {
            NavItem(Tab.RPM, "Fordulatszám", AppIcons.Rpm, selected, onSelect)
            NavItem(Tab.SPEED, "Kerületi seb.", AppIcons.Speed, selected, onSelect)
            NavItem(Tab.DIAMETER, "Átmérő", AppIcons.Diameter, selected, onSelect)
        }
    }
}

@Composable
private fun RowScope.NavItem(
    tab: Tab,
    label: String,
    icon: ImageVector,
    selected: Tab?,
    onSelect: (Tab) -> Unit,
) {
    val c = LocalAppColors.current
    val active = tab == selected
    val color = if (active) c.accentText else c.muted
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(selected = active, role = Role.Tab, onClick = { onSelect(tab) }),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            Modifier
                .size(width = 64.dp, height = 34.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(if (active) c.softBg else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

// ---------- Kalkulátor képernyő keret ----------

@Composable
fun ScreenColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
fun TitleRow(title: String, onInfo: () -> Unit) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            color = c.text,
            fontFamily = CondensedFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 34.sp,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
        )
        Row(
            modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(role = Role.Button, onClick = onInfo)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(AppIcons.Info, contentDescription = null, tint = c.accentText, modifier = Modifier.size(20.dp))
            Text("Ajánlott értékek", color = c.accentText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
fun FieldLabel(text: String, trailing: String? = null) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = text,
            color = c.label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (trailing != null) {
            Text(trailing, color = c.muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

// ---------- Választógombok ----------

@Composable
fun Chip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    horizontalPadding: Dp = 16.dp,
    fontSize: Int = 15,
    onClick: () -> Unit,
) {
    val c = LocalAppColors.current
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(if (selected) Brand else c.surface)
            .border(1.5.dp, if (selected) Brand else c.border, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else c.text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
fun ToolChips(selected: ToolType, showMax: Boolean, onSelect: (ToolType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel("Szerszám típusa", if (showMax) "max. ${selected.maxSpeed} m/s" else null)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ToolType.entries.forEach { tool ->
                Chip(text = tool.title, selected = tool == selected, onClick = { onSelect(tool) })
            }
        }
    }
}

@Composable
fun QuickDiameters(current: Double?, onPick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Calc.QUICK_DIAMETERS.forEach { size ->
            Chip(
                text = size.toString(),
                selected = current == size.toDouble(),
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "$size mm" },
                shape = RoundedCornerShape(10.dp),
                horizontalPadding = 0.dp,
                fontSize = 16,
                onClick = { onPick(size) },
            )
        }
    }
}

// ---------- Számbeviteli mező ----------

@Composable
fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    warn: Boolean = false,
    trailingHint: String? = null,
) {
    val c = LocalAppColors.current
    val focusManager = LocalFocusManager.current
    var focused by remember { mutableStateOf(false) }
    val borderColor = when {
        focused -> c.accentText
        warn -> c.warnBorder
        else -> c.border
    }
    val shape = RoundedCornerShape(12.dp)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label, trailingHint)
        BasicTextField(
            value = value,
            onValueChange = { s -> onValueChange(s.filter { it.isDigit() || it == ',' || it == '.' }.take(9)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            textStyle = TextStyle(color = c.text, fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
            cursorBrush = SolidColor(c.accentText),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .onFocusChanged { focused = it.isFocused }
                .semantics { contentDescription = "$label, $unit" },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(c.inputBg)
                        .border(if (focused) 2.dp else 1.5.dp, borderColor, shape)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.weight(1f)) { innerTextField() }
                    Spacer(Modifier.width(8.dp))
                    Text(unit, color = c.muted, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

// ---------- Eredmény, figyelmeztetés, webshop link ----------

@Composable
fun ResultCard(
    label: String,
    formula: String,
    value: String,
    unit: String,
    danger: Boolean = false,
    footer: String? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (danger) BrandDeep else Brand)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(formula, color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, maxLines = 1)
        }
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = value,
                color = Color.White,
                fontFamily = CondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
                lineHeight = 52.sp,
                modifier = Modifier.alignByBaseline(),
            )
            Text(
                text = unit,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alignByBaseline(),
            )
        }
        if (footer != null) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))
            Text(footer, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun WarningBanner(title: String, message: String) {
    val c = LocalAppColors.current
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(c.warnBg)
            .border(1.5.dp, c.warnBorder, shape)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Assertive }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(AppIcons.Warning, contentDescription = null, tint = c.warnText, modifier = Modifier.size(22.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(title) }
                append(" ")
                append(message)
            },
            color = c.warnText,
            fontSize = 14.sp,
            lineHeight = 19.sp,
        )
    }
}

@Composable
fun ShopLink(label: String, url: String) {
    val c = LocalAppColors.current
    val context = LocalContext.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(shape)
            .background(c.surface)
            .border(1.5.dp, c.border, shape)
            .clickable(role = Role.Button) { context.openUrl(url) }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(c.softBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(AppIcons.Cart, contentDescription = null, tint = c.softText, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text("Ilyen szerszámot keresel?", color = c.muted, fontSize = 13.sp)
            Text(
                text = label,
                color = c.text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(AppIcons.ArrowRight, contentDescription = null, tint = c.accentText, modifier = Modifier.size(20.dp))
    }
}
