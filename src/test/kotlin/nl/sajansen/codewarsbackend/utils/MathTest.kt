package nl.sajansen.codewarsbackend.utils

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class MathTest {
    @Test
    fun `test calculating angle of a vector`() {
        assertEquals(0f, Vector(listOf(0f, 1f)).angle())
        assertEquals(45f, Vector(listOf(1f, 1f)).angle())
        assertEquals(90f, Vector(listOf(1f, 0f)).angle())
        assertEquals(135f, Vector(listOf(1f, -1f)).angle())
        assertEquals(180f, Vector(listOf(0f, -1f)).angle())
        assertEquals(225f, Vector(listOf(-1f, -1f)).angle())
        assertEquals(270f, Vector(listOf(-1f, 0f)).angle())
        assertEquals(315f, Vector(listOf(-1f, 1f)).angle())
    }

    @Test
    fun `test limiting degrees to 0-360`() {
        assertEquals(0f, limitDegrees(0f))
        assertEquals(0f, limitDegrees(360f))
        assertEquals(0f, limitDegrees(2 * 360f))
        assertEquals(0f, limitDegrees(-360f))
        assertEquals(0f, limitDegrees(-2 * 360f))
        assertEquals(180f, limitDegrees(180f))
        assertEquals(180f, limitDegrees(3 * 180f))
        assertEquals(180f, limitDegrees(-180f))
        assertEquals(180f, limitDegrees(-3 * 180f))
        assertEquals(90f, limitDegrees(90f))
        assertEquals(90f, limitDegrees(360 + 90f))
        assertEquals(270f, limitDegrees(-90f))
        assertEquals(270f, limitDegrees(-360 - 90f))
    }
}