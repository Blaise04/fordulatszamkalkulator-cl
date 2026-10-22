package hu.csiszolotarcsawebshop.fordulatszam

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun App(dark: Boolean, onToggleDark: () -> Unit) {
    val c = LocalAppColors.current

    var tab by rememberSaveable { mutableStateOf(Tab.RPM) }
    var showTable by rememberSaveable { mutableStateOf(false) }

    // Fordulatszám kalkulátor
    var rpmTool by rememberSaveable { mutableStateOf(ToolType.CUTTING) }
    var rpmD by rememberSaveable { mutableStateOf("125") }
    var rpmV by rememberSaveable { mutableStateOf("80") }

    // Kerületi sebesség kalkulátor
    var spdTool by rememberSaveable { mutableStateOf(ToolType.CUTTING) }
    var spdD by rememberSaveable { mutableStateOf("125") }
    var spdN by rememberSaveable { mutableStateOf("11000") }

    // Átmérő kalkulátor
    var diaTool by rememberSaveable { mutableStateOf(ToolType.CUTTING) }
    var diaN by rememberSaveable { mutableStateOf("11000") }
    var diaV by rememberSaveable { mutableStateOf("80") }

    BackHandler(enabled = showTable) { showTable = false }
    BackHandler(enabled = !showTable && tab != Tab.RPM) { tab = Tab.RPM }

    Scaffold(
        containerColor = c.bg,
        contentColor = c.text,
        topBar = { TopBar(dark = dark, onToggleDark = onToggleDark) },
        bottomBar = {
            BottomNav(
                selected = if (showTable) null else tab,
                onSelect = { tab = it; showTable = false },
            )
        },
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (showTable) {
                RecommendedScreen(onBack = { showTable = false })
            } else {
                when (tab) {
                    Tab.RPM -> RpmScreen(
                        tool = rpmTool,
                        onTool = { rpmTool = it; rpmV = it.maxSpeed.toString() },
                        d = rpmD, onD = { rpmD = it },
                        v = rpmV, onV = { rpmV = it },
                        onInfo = { showTable = true },
                    )
                    Tab.SPEED -> SpeedScreen(
                        tool = spdTool,
                        onTool = { spdTool = it },
                        d = spdD, onD = { spdD = it },
                        n = spdN, onN = { spdN = it },
                        onInfo = { showTable = true },
                    )
                    Tab.DIAMETER -> DiameterScreen(
                        tool = diaTool,
                        onTool = { diaTool = it; diaV = it.maxSpeed.toString() },
                        n = diaN, onN = { diaN = it },
                        v = diaV, onV = { diaV = it },
                        onInfo = { showTable = true },
                    )
                }
            }
        }
    }
}

// ---------- 1. Fordulatszám ----------

@Composable
fun RpmScreen(
    tool: ToolType,
    onTool: (ToolType) -> Unit,
    d: String,
    onD: (String) -> Unit,
    v: String,
    onV: (String) -> Unit,
    onInfo: () -> Unit,
) {
    val dv = Calc.parse(d)
    val vv = Calc.parse(v)
    val warn = vv != null && vv > tool.maxSpeed
    val result = if (dv != null && vv != null) Calc.format(Calc.rpm(dv, vv), 0) else "–"
    val shopDiameter = dv?.roundToInt()

    ScreenColumn {
        TitleRow("Fordulatszám", onInfo)
        ToolChips(selected = tool, showMax = false, onSelect = onTool)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberField("A csiszolószerszám átmérője", d, onD, "mm")
            QuickDiameters(current = dv, onPick = { onD(it.toString()) })
        }
        NumberField(
            label = "Az elérni kívánt kerületi sebesség",
            value = v,
            onValueChange = onV,
            unit = "m/s",
            warn = warn,
            trailingHint = "max. ${tool.maxSpeed} m/s",
        )
        ResultCard("Szükséges fordulatszám", "n = v · 60 000 / (π · d)", result, "1/min")
        if (warn) {
            WarningBanner(
                "Túl nagy sebesség!",
                "A ${tool.lower} megengedett maximuma ${tool.maxSpeed} m/s. Válassz kisebb értéket, különben balesetveszélyes!",
            )
        }
        ShopLink(Calc.shopLabel(tool, shopDiameter), Calc.shopSearchUrl(tool, shopDiameter))
    }
}

// ---------- 2. Kerületi sebesség ----------

@Composable
fun SpeedScreen(
    tool: ToolType,
    onTool: (ToolType) -> Unit,
    d: String,
    onD: (String) -> Unit,
    n: String,
    onN: (String) -> Unit,
    onInfo: () -> Unit,
) {
    val dv = Calc.parse(d)
    val nv = Calc.parse(n)
    val speed = if (dv != null && nv != null) Calc.speed(dv, nv) else null
    val warn = speed != null && speed > tool.maxSpeed
    val maxRpm = if (dv != null) Calc.rpm(dv, tool.maxSpeed.toDouble()) else null
    val shopDiameter = dv?.roundToInt()

    ScreenColumn {
        TitleRow("Kerületi sebesség", onInfo)
        ToolChips(selected = tool, showMax = true, onSelect = onTool)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            NumberField("A csiszolószerszám átmérője", d, onD, "mm")
            QuickDiameters(current = dv, onPick = { onD(it.toString()) })
        }
        NumberField("A szerszám fordulatszáma", n, onN, "1/min")
        ResultCard(
            label = "Kerületi sebesség",
            formula = "v = π · d · n / 60 000",
            value = if (speed != null) Calc.format(speed, 1) else "–",
            unit = "m/s",
            danger = warn,
        )
        if (warn && speed != null) {
            val limit = if (maxRpm != null) Calc.formatFloor(maxRpm) else "–"
            WarningBanner(
                "Balesetveszély!",
                "${Calc.format(speed, 1)} m/s meghaladja a ${tool.lower} ${tool.maxSpeed} m/s-os maximumát. " +
                    "Ennél az átmérőnél legfeljebb $limit 1/min engedélyezett.",
            )
        }
        ShopLink(Calc.shopLabel(tool, shopDiameter), Calc.shopSearchUrl(tool, shopDiameter))
    }
}

// ---------- 3. Átmérő ----------

@Composable
fun DiameterScreen(
    tool: ToolType,
    onTool: (ToolType) -> Unit,
    n: String,
    onN: (String) -> Unit,
    v: String,
    onV: (String) -> Unit,
    onInfo: () -> Unit,
) {
    val nv = Calc.parse(n)
    val vv = Calc.parse(v)
    val diameter = if (nv != null && vv != null) Calc.diameter(nv, vv) else null
    val std = diameter?.let { Calc.standardDiameterAtMost(it) }
    val warn = vv != null && vv > tool.maxSpeed

    ScreenColumn {
        TitleRow("Átmérő", onInfo)
        ToolChips(selected = tool, showMax = false, onSelect = onTool)
        NumberField("A szerszám (gép) fordulatszáma", n, onN, "1/min")
        NumberField(
            label = "Az elérni kívánt kerületi sebesség",
            value = v,
            onValueChange = onV,
            unit = "m/s",
            warn = warn,
            trailingHint = "max. ${tool.maxSpeed} m/s",
        )
        ResultCard(
            label = "Legnagyobb átmérő",
            formula = "d = v · 60 000 / (π · n)",
            value = if (diameter != null) Calc.format(diameter, 1) else "–",
            unit = "mm",
            footer = "Javasolt szabványos méret: " + (if (std != null) "$std mm" else "–"),
        )
        if (warn) {
            WarningBanner(
                "Túl nagy sebesség!",
                "A ${tool.lower} megengedett maximuma ${tool.maxSpeed} m/s. Válassz kisebb értéket, különben balesetveszélyes!",
            )
        }
        ShopLink(Calc.shopLabel(tool, std), Calc.shopSearchUrl(tool, std))
    }
}

// ---------- 4. Ajánlott értékek ----------

@Composable
fun RecommendedScreen(onBack: () -> Unit) {
    val c = LocalAppColors.current
    val tableShape = RoundedCornerShape(14.dp)

    ScreenColumn {
        Row(
            modifier = Modifier
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(role = Role.Button, onClick = onBack)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(AppIcons.ArrowLeft, contentDescription = null, tint = c.accentText, modifier = Modifier.size(22.dp))
            Text("Vissza", color = c.accentText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Ajánlott értékek",
                color = c.text,
                fontFamily = CondensedFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "Megengedett legnagyobb kerületi sebesség szerszámtípusonként, és az ebből adódó " +
                    "max. fordulatszám két gyakori átmérőnél.",
                color = c.muted,
                fontSize = 15.sp,
                lineHeight = 20.sp,
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .clip(tableShape)
                .background(c.surface)
                .border(1.5.dp, c.border, tableShape)
        ) {
            TableRow(
                cells = listOf("Szerszám", "m/s", "Ø125\n1/min", "Ø230\n1/min"),
                header = true,
            )
            ToolType.entries.forEach { tool ->
                HorizontalDivider(color = c.divider)
                TableRow(
                    cells = listOf(
                        tool.title,
                        tool.maxSpeed.toString(),
                        Calc.formatFloor(Calc.rpm(125.0, tool.maxSpeed.toDouble())),
                        Calc.formatFloor(Calc.rpm(230.0, tool.maxSpeed.toDouble())),
                    ),
                    header = false,
                )
            }
        }

        WarningBanner(
            "Figyelem!",
            "A táblázat tájékoztató jellegű. Mindig a szerszámon feltüntetett legnagyobb kerületi sebesség és fordulatszám az irányadó.",
        )
    }
}

@Composable
private fun TableRow(cells: List<String>, header: Boolean) {
    val c = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (header) c.headBg else c.surface)
            .heightIn(min = 52.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        cells.forEachIndexed { i, text ->
            val weightMod = if (i == 0) Modifier.weight(1f) else Modifier.width(if (i == 1) 44.dp else 72.dp)
            Text(
                text = text,
                modifier = weightMod,
                color = when {
                    header -> c.label
                    i == 1 -> c.accentText
                    else -> c.text
                },
                fontSize = if (header) 13.sp else 15.sp,
                fontWeight = when {
                    header -> FontWeight.Bold
                    i <= 1 -> FontWeight.Bold
                    else -> FontWeight.Normal
                },
                lineHeight = if (header) 16.sp else 19.sp,
                textAlign = if (i == 0) TextAlign.Start else TextAlign.End,
            )
        }
    }
}
