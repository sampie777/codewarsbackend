package nl.sajansen.codewarsbackend.ws

import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import nl.sajansen.codewarsbackend.module
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ServerTest {
    @Before
    fun before() {
        Server.connections.forEach { Server.closeConnection(it) }
    }

    @Test
    fun `test check if connection last message is timed out`() {
        withTestApplication({ module(testing = true) }) {
            handleWebSocketConversation("/ws") { _, _ ->
                delay(100)
                assertEquals(1, Server.connections.size)

                val connection = Server.connections.first()
                connection.lastMessageTime = Date(0)

                val result = Server.isConnectionLastMessageTimedOut(connection, 1000)
                assertTrue(result)
            }
        }
    }

    @Test
    fun `test check if connection last message is timed out 2`() {
        withTestApplication({ module(testing = true) }) {
            handleWebSocketConversation("/ws") { _, _ ->
                delay(100)
                assertEquals(1, Server.connections.size)

                val connection = Server.connections.first()
                connection.lastMessageTime = Date(Date().time - 2000)

                val result = Server.isConnectionLastMessageTimedOut(connection, 1000)
                assertTrue(result)
            }
        }
    }

    @Test
    fun `test check if connection last message is timed out 3`() {
        withTestApplication({ module(testing = true) }) {
            handleWebSocketConversation("/ws") { _, _ ->
                delay(100)
                assertEquals(1, Server.connections.size)

                val connection = Server.connections.first()
                connection.lastMessageTime = Date()

                val result = Server.isConnectionLastMessageTimedOut(connection, 1000)
                assertFalse(result)
            }
        }
    }

    @Test
    fun `test check if connection last message is timed out 4`() {
        withTestApplication({ module(testing = true) }) {
            handleWebSocketConversation("/ws") { _, _ ->
                delay(100)
                assertEquals(1, Server.connections.size)

                val connection = Server.connections.first()
                connection.lastMessageTime = Date(Date().time - 500)

                val result = Server.isConnectionLastMessageTimedOut(connection, 1000)
                assertFalse(result)
            }
        }
    }
}