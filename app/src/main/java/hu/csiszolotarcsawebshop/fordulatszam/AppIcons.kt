package hu.csiszolotarcsawebshop.fordulatszam

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Vonalas ikonok (24×24, 2 px vonal) – ugyanazok, mint a designban. */
object AppIcons {

    private fun circle(cx: Float, cy: Float, r: Float): String =
        "M${cx - r},$cy a$r,$r 0 1,0 ${2 * r},0 a$r,$r 0 1,0 ${-2 * r},0"

    private fun icon(name: String, vararg paths: String, strokeWidth: Float = 2f): ImageVector {
        val builder = ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        for (p in paths) {
            builder.addPath(
                pathData = addPathNodes(p),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = strokeWidth,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
        return builder.build()
    }

    private fun filledDot(name: String, vararg strokePaths: String, dot: String): ImageVector {
        val builder = ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        for (p in strokePaths) {
            builder.addPath(
                pathData = addPathNodes(p),
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
        builder.addPath(pathData = addPathNodes(dot), fill = SolidColor(Color.Black))
        return builder.build()
    }

    val Phone: ImageVector = icon(
        "Phone",
        "M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3.1 19.5 19.5 0 0 1-6-6A19.8 19.8 0 0 1 2.1 4.2 " +
            "2 2 0 0 1 4.1 2h3a2 2 0 0 1 2 1.7c0.1 0.9 0.4 1.8 0.7 2.7a2 2 0 0 1-0.5 2.1L8 9.8a16 16 0 0 0 6 6" +
            "l1.3-1.3a2 2 0 0 1 2.1-0.4c0.9 0.3 1.8 0.6 2.7 0.7a2 2 0 0 1 1.7 2z",
    )

    val Mail: ImageVector = icon(
        "Mail",
        "M4 4h16a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
        "M22 6L12 13L2 6",
    )

    val Moon: ImageVector = icon("Moon", "M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z")

    val Sun: ImageVector = icon(
        "Sun",
        circle(12f, 12f, 4f),
        "M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4",
    )

    val Info: ImageVector = icon("Info", circle(12f, 12f, 10f), "M12 16v-4M12 8h0.01")

    val ArrowRight: ImageVector = icon("ArrowRight", "M5 12h14M13 6l6 6-6 6", strokeWidth = 2.2f)

    val ArrowLeft: ImageVector = icon("ArrowLeft", "M19 12H5M11 18l-6-6 6-6", strokeWidth = 2.2f)

    val Warning: ImageVector = icon(
        "Warning",
        "M10.3 3.9L1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.9a2 2 0 0 0-3.4 0z",
        "M12 9v4M12 17h0.01",
        strokeWidth = 2.2f,
    )

    val Cart: ImageVector = icon(
        "Cart",
        circle(9f, 21f, 1f),
        circle(20f, 21f, 1f),
        "M1 1h4l2.7 13.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L23 6H6",
    )

    val Rpm: ImageVector = icon("Rpm", "M21 12a9 9 0 1 1-3-6.7", "M21 4v5h-5", "M12 12l3-3")

    val Speed: ImageVector = filledDot(
        "Speed",
        "M4 18a9 9 0 1 1 16 0",
        "M12 15l4-6",
        dot = circle(12f, 15f, 1.6f),
    )

    val Diameter: ImageVector = icon("Diameter", circle(12f, 12f, 9f), "M5.6 18.4L18.4 5.6")
}
