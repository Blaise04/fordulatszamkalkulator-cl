package hu.csiszolotarcsawebshop.fordulatszam

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalcTest {

    private fun plain(s: String) = s.replace(' ', ' ').replace(' ', ' ')

    @Test
    fun rpmFromDiameterAndSpeed() {
        // 125 mm, 80 m/s -> kb. 12 223 1/min
        assertEquals(12223.0, Calc.rpm(125.0, 80.0), 1.0)
    }

    @Test
    fun speedFromDiameterAndRpm() {
        // 230 mm, 6600 1/min -> kb. 79,5 m/s
        assertEquals(79.48, Calc.speed(230.0, 6600.0), 0.01)
    }

    @Test
    fun diameterFromRpmAndSpeed() {
        // 11 000 1/min, 80 m/s -> kb. 138,9 mm
        assertEquals(138.9, Calc.diameter(11000.0, 80.0), 0.05)
    }

    @Test
    fun formulasAreConsistent() {
        val n = Calc.rpm(180.0, 80.0)
        assertEquals(80.0, Calc.speed(180.0, n), 1e-9)
        assertEquals(180.0, Calc.diameter(n, 80.0), 1e-9)
    }

    @Test
    fun parseAcceptsHungarianInput() {
        assertEquals(1250.5, Calc.parse("1 250,5")!!, 1e-9)
        assertEquals(80.0, Calc.parse(" 80 ")!!, 1e-9)
        assertEquals(12.5, Calc.parse("12.5")!!, 1e-9)
    }

    @Test
    fun parseRejectsInvalidInput() {
        assertNull(Calc.parse(""))
        assertNull(Calc.parse("abc"))
        assertNull(Calc.parse("0"))
        assertNull(Calc.parse("-5"))
        assertNull(Calc.parse("1,2,3"))
    }

    @Test
    fun standardDiameter() {
        assertEquals(125, Calc.standardDiameterAtMost(138.9))
        assertEquals(230, Calc.standardDiameterAtMost(230.0))
        assertNull(Calc.standardDiameterAtMost(30.0))
    }

    @Test
    fun formatting() {
        assertEquals("12 223", plain(Calc.format(12223.3, 0)))
        assertEquals("79,5", plain(Calc.format(79.48, 1)))
        assertEquals("6 643", plain(Calc.formatFloor(6643.9)))
    }

    @Test
    fun shopLinks() {
        val url = Calc.shopSearchUrl(ToolType.CUTTING, 125)
        assertTrue(url.startsWith("https://www.csiszolotarcsawebshop.hu/shop_search.php?search="))
        assertTrue(url.endsWith("125+v%C3%A1g%C3%B3korong"))
        assertEquals("125 mm-es vágókorongok", Calc.shopLabel(ToolType.CUTTING, 125))
        assertEquals("Drótkefék", Calc.shopLabel(ToolType.WIRE, null))
    }

    @Test
    fun vitrifiedWheelHasLowerLimit() {
        assertEquals(35, ToolType.VITRIFIED.maxSpeed)
        assertTrue(ToolType.entries.filter { it != ToolType.VITRIFIED }.all { it.maxSpeed == 80 })
    }
}
