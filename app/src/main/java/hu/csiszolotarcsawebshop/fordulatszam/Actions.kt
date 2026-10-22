package hu.csiszolotarcsawebshop.fordulatszam

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/** Webhely megnyitása a böngészőben. */
fun Context.openUrl(url: String) {
    safeStart(Intent(Intent.ACTION_VIEW, Uri.parse(url)), "Nem található böngésző a link megnyitásához.")
}

/** A tárcsázó megnyitása a webshop telefonszámával (híváshoz nem kell engedély). */
fun Context.dialShop() {
    safeStart(
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Calc.PHONE}")),
        "Nem található tárcsázó. Hívj minket: ${Calc.PHONE_DISPLAY}",
    )
}

/** Levélírás a webshopnak, előre kitöltött tárggyal. */
fun Context.emailShop() {
    val uri = Uri.parse("mailto:${Calc.EMAIL}?subject=${Uri.encode(Calc.EMAIL_SUBJECT)}")
    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra(Intent.EXTRA_SUBJECT, Calc.EMAIL_SUBJECT)
    }
    safeStart(intent, "Nincs levelezőalkalmazás. Írj nekünk: ${Calc.EMAIL}")
}

private fun Context.safeStart(intent: Intent, fallbackMessage: String) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, fallbackMessage, Toast.LENGTH_LONG).show()
    }
}
