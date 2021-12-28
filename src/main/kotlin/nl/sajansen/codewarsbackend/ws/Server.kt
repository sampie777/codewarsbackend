package nl.sajansen.codewarsbackend.ws

import io.ktor.http.cio.websocket.*
import jsonBuilder
import org.slf4j.LoggerFactory
import java.util.*

object Server {
    private val logger = LoggerFactory.getLogger(this.toString())

    private val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    suspend fun newConnection(session: DefaultWebSocketSession): Connection {
        val connection = Connection(session)
        connections.add(connection)

        logger.info("New connection: ${connection.name}. Currently ${connections.size} connections.")
        connection.identify()
        return connection
    }

    suspend fun closeConnection(connection: Connection) {
        logger.info("Closing connecting: ${connection.name}")
        connections.remove(connection)
        connection.session.close()
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