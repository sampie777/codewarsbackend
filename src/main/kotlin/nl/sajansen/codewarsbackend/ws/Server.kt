package nl.sajansen.codewarsbackend.ws

import io.ktor.http.cio.websocket.*
import jsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.fixedRateTimer

object Server {
    private val logger = LoggerFactory.getLogger(this.toString())

    private val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    fun start() {
        fixedRateTimer(name = "connectionCheckTimer", daemon = true, period = 1000) {
            connections
                .filter { isConnectionClosed(it) }
                .forEach { closeConnection(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun isConnectionClosed(it: Connection): Boolean {
        if (it.session.outgoing.isClosedForSend) {
            logger.info("Connection ${it.name} is closed for send")
            return true
        }

        if (it.session.incoming.isClosedForReceive) {
            logger.info("Connection ${it.name} is closed for receive")
            return true
        }
        return false
    }

    suspend fun newConnection(session: DefaultWebSocketSession): Connection {
        val connection = Connection(session)
        connections.add(connection)

        logger.info("New connection: ${connection.name}. Currently ${connections.size} connections.")
        connection.identify()
        return connection
    }

    fun closeConnection(connection: Connection) {
        logger.info("Closing connecting: ${connection.name}")
        connections.remove(connection)

        runBlocking {
            try {
                connection.session.close()
            } catch (t: Throwable) {
                logger.error("Failed to close connection session")
                t.printStackTrace()
            }
        }

        logger.info("Currently ${connections.size} connections.")
    }

    suspend fun handleTextMessage(connection: Connection, frame: Frame.Text) {
        val receivedText = frame.readText()
        logger.info("[${connection.name}] Received: " + receivedText)

        val data = jsonBuilder().fromJson(receivedText, Map::class.java)
        if (!data.containsKey("type")) {
            logger.warn("Unknown message type for message: ", receivedText)
        }

        when (Type.valueOf(data["type"] as String)) {
            Type.IDENTIFY -> {
                connection.identify()
            }
            Type.PLAYER_STATE -> {
                logger.info("Got player state")
            }
        }
    }

    suspend fun sendToAll(message: String) {
        connections.forEach {
            it.session.send(message)
        }
    }
}