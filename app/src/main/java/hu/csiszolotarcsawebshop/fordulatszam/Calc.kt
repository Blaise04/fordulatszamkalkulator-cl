package hu.csiszolotarcsawebshop.fordulatszam

import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.PI
import kotlin.math.floor

/**
 * Szerszámtípusok a megengedett legnagyobb kerületi sebességgel (m/s).
 * Tipikus értékek – mindig a szerszámon feltüntetett érték az irányadó.
 */
enum class ToolType(
    val title: String,
    val lower: String,
    val plural: String,
    val keyword: String,
    val maxSpeed: Int,
) {
    CUTTING("Vágókorong", "vágókorong", "vágókorongok", "vágókorong", 80),
    CLEANING("Tisztítókorong", "tisztítókorong", "tisztítókorongok", "tisztítókorong", 80),
    FLAP("Lamellás tárcsa", "lamellás tárcsa", "lamellás tárcsák", "lamellás", 80),
    FIBRE("Fíbertárcsa", "fíbertárcsa", "fíbertárcsák", "fíbertárcsa", 80),
    VITRIFIED("Kerámia köszörűkorong", "kerámia köszörűkorong", "kerámia köszörűkorongok", "köszörűkorong", 35),
    WIRE("Drótkefe", "drótkefe", "drótkefék", "drótkefe", 80),
}

object Calc {
    const val SHOP_URL = "https://www.csiszolotarcsawebshop.hu/"
    const val PHONE = "+36304827634"
    const val PHONE_DISPLAY = "+36 30 482 7634"
    const val EMAIL = "rendeles@csiszolotarcsawebshop.hu"
    const val EMAIL_SUBJECT = "Érdeklődés érkezett az applikációból"

    val QUICK_DIAMETERS = listOf(115, 125, 150, 180, 230)
    val STANDARD_DIAMETERS = listOf(50, 75, 100, 115, 125, 150, 180, 200, 230, 250, 300, 350, 400)

    private val HU = Locale("hu", "HU")

    /** Felhasználói bevitel számmá alakítása; vessző és szóköz is megengedett. Csak pozitív szám érvényes. */
    fun parse(input: String): Double? {
        val cleaned = input.filterNot { it.isWhitespace() || it == ' ' || it == ' ' }.replace(',', '.')
        val x = cleaned.toDoubleOrNull() ?: return null
        return if (x.isFinite() && x > 0.0) x else null
    }

    /** Fordulatszám (1/min) az átmérőből (mm) és a kerületi sebességből (m/s). */
    fun rpm(diameterMm: Double, speedMs: Double): Double = speedMs * 60_000.0 / (PI * diameterMm)

    /** Kerületi sebesség (m/s) az átmérőből (mm) és a fordulatszámból (1/min). */
    fun speed(diameterMm: Double, rpm: Double): Double = PI * diameterMm * rpm / 60_000.0

    /** Átmérő (mm) a fordulatszámból (1/min) és a kerületi sebességből (m/s). */
    fun diameter(rpm: Double, speedMs: Double): Double = speedMs * 60_000.0 / (PI * rpm)

    /** A számított átmérőnél nem nagyobb, legközelebbi szabványos méret. */
    fun standardDiameterAtMost(diameterMm: Double): Int? = STANDARD_DIAMETERS.lastOrNull { it <= diameterMm }

    fun format(x: Double, decimals: Int): String {
        val f = NumberFormat.getNumberInstance(HU)
        f.minimumFractionDigits = decimals
        f.maximumFractionDigits = decimals
        f.isGroupingUsed = true
        return f.format(x)
    }

    /** Lefelé kerekített egész – a megengedett maximumoknál így biztosan nem lépjük túl a határt. */
    fun formatFloor(x: Double): String = format(floor(x), 0)

    fun shopSearchUrl(tool: ToolType, diameterMm: Int?): String {
        val query = (if (diameterMm != null) "$diameterMm " else "") + tool.keyword
        return SHOP_URL + "shop_search.php?search=" + URLEncoder.encode(query, "UTF-8")
    }

    fun shopLabel(tool: ToolType, diameterMm: Int?): String =
        if (diameterMm != null) "$diameterMm mm-es ${tool.plural}"
        else tool.plural.replaceFirstChar { it.uppercase(HU) }
}
