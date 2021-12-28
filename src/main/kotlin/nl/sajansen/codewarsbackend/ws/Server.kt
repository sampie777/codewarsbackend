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

    fun newConnection(session: DefaultWebSocketSession): Connection {
        val connection = Connection(session)
        connections.add(connection)

        logger.info("New connection: ${connection.name}. Currently ${connections.size} connections.")
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

        val type = try {
            jsonBuilder().fromJson(receivedText, Message.BaseMessage::class.java).type
        } catch (t: Throwable) {
            logger.error("Unknown message type: ", receivedText)
            return
        }

        val data = getTypedData(receivedText, type)

        when (data.type) {
            Type.IDENTIFY -> handleIdentifyMessage(connection, data as Message.Identify)
            Type.PLAYER_STATE -> handlePlayerStateMessage(connection, data as Message.PlayerState)
            Type.GAME_STATE -> handleGameStateMessage(connection, data as Message.GameState)
        }
    }

    private fun getTypedData(text: String, type: Type): Message.BaseMessage {
        return when (type) {
            Type.IDENTIFY -> jsonBuilder().fromJson(text, Message.Identify::class.java)
            Type.PLAYER_STATE -> jsonBuilder().fromJson(text, Message.PlayerState::class.java)
            Type.GAME_STATE -> jsonBuilder().fromJson(text, Message.GameState::class.java)
        }
    }

    private suspend fun handleIdentifyMessage(connection: Connection, data: Message.Identify) {
        if (data.id != null) {
            // todo: Should map with old session
            connection.identify()
        } else {
            connection.identify()
        }
    }

    private suspend fun handlePlayerStateMessage(connection: Connection, data: Message.PlayerState) {
        connection.sendJson(
            Message.GameState(
                x = 0f,
                y = 0f,
                size = 10f,
                heading = 0f,
            )
        )
    }

    private fun handleGameStateMessage(connection: Connection, data: Message.GameState) {
    }
}