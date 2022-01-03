package nl.sajansen.codewarsbackend.game

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GameTest {
    @Before
    fun before() {
        Game.players.clear()
    }

    @Test
    fun `test removing players`() {
        Game.createPlayer(0, "player0")
        Game.createPlayer(1, "player1")
        assertEquals(2, Game.players.size)

        Game.removePlayer(1)

        assertEquals(1, Game.players.size)
        assertEquals(0, Game.players.first().id)
    }
}