package nl.sajansen.codewarsbackend.utils

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class MathTest {
    @Test
    fun t(){
        assertEquals(0f, Vector(listOf(0f, 1f)).angle())
        assertEquals(45f, Vector(listOf(1f, 1f)).angle())
        assertEquals(90f, Vector(listOf(1f, 0f)).angle())
        assertEquals(135f, Vector(listOf(1f, -1f)).angle())
        assertEquals(180f, Vector(listOf(0f, -1f)).angle())
        assertEquals(225f, Vector(listOf(-1f, -1f)).angle())
        assertEquals(270f, Vector(listOf(-1f, 0f)).angle())
        assertEquals(315f, Vector(listOf(-1f, 1f)).angle())
    }
}