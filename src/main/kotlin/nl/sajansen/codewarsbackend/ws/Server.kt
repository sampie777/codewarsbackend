package nl.sajansen.codewarsbackend.ws

import io.ktor.http.cio.websocket.*
import jsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.game.Game
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.fixedRateTimer

object Server {
    private val logger = LoggerFactory.getLogger(this.toString())

    val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    fun start() {
        fixedRateTimer(
            name = "connectionCheckTimer",
            daemon = true,
            period = Config.serverCheckDisconnectedTimeout,
            initialDelay = Config.serverCheckDisconnectedTimeout,
        ) {
            try {
                connections
                    .filter { isConnectionClosed(it) }
                    .forEach { closeConnection(it) }
                Game.players
                    .filter { connections.find { connection -> connection.id == it.id } == null }
                    .forEach { Game.removePlayer(it.id) }
            } catch (t: Throwable) {
                logger.error("Exception during checking and removing disconnected connections")
                t.printStackTrace()
            }
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

        if (isConnectionLastMessageTimedOut(it)) {
            logger.info("Connection ${it.name} hasn't send messages for a while")
            return true
        }

        return false
    }

    fun isConnectionLastMessageTimedOut(it: Connection, maxMilliSeconds: Int = 1000): Boolean {
        val minExpectedLastMessageTime = Date(Date().time - maxMilliSeconds)
        return it.lastMessageTime.before(minExpectedLastMessageTime)
    }

    suspend fun newConnection(session: DefaultWebSocketSession): Connection {
        val connection = Connection(session)
        connections.add(connection)

        logger.info("New connection: ${connection.name}. Currently ${connections.size} connections.")

        Game.createPlayer(connection.id, connection.name)

        sendGameConfiguration(connection)
        return connection
    }

    fun closeConnection(connection: Connection) {
        logger.info("Closing connecting: ${connection.name}")

        Game.removePlayer(connection.id)
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
        connection.lastMessageTime = Date()

        val type = try {
            jsonBuilder().fromJson(receivedText, Message.BaseMessage::class.java).type
        } catch (t: Throwable) {
            logger.error("Unknown message type: ", receivedText)
            return
        }

        val data = getTypedData(receivedText, type)

        when (data.type) {
            Message.Type.IDENTIFY -> handleIdentifyMessage(connection, data as Message.Identify)
            Message.Type.PLAYER_STATE -> handlePlayerStateMessage(connection, data as Message.PlayerState)
            Message.Type.GAME_STATE -> handleGameStateMessage(connection, data as Message.GameState)
        }
    }

    private fun getTypedData(text: String, type: Message.Type): Message.BaseMessage {
        return when (type) {
            Message.Type.IDENTIFY -> jsonBuilder().fromJson(text, Message.Identify::class.java)
            Message.Type.PLAYER_STATE -> jsonBuilder().fromJson(text, Message.PlayerState::class.java)
            Message.Type.GAME_STATE -> jsonBuilder().fromJson(text, Message.GameState::class.java)
            Message.Type.GAME_CONFIGURATION -> jsonBuilder().fromJson(text, Message.GameConfiguration::class.java)
        }
    }

    private suspend fun handleIdentifyMessage(connection: Connection, data: Message.Identify) {
        if (data.id != null) {
            // todo: Should map with old session
            bindConnectionWithOlderConnection(connection, data.id)
        }
        connection.identify()
    }

    private fun bindConnectionWithOlderConnection(connection: Connection, oldId: Int) {
        // Only copy player object if another connection isn't using it
        if (connections.any { it.id == oldId }) {
            return
        }

        val player = Game.getPlayer(oldId)
            ?: return

        val newPlayer = Game.getPlayer(connection.id)
            ?: Game.createPlayer(connection.id, player.name)

        newPlayer.copyFrom(player)
        Game.removePlayer(player.id)
    }

    private suspend fun handlePlayerStateMessage(connection: Connection, data: Message.PlayerState) {
        Game.updatePlayer(
            connection.id,
            appliedForce = data.appliedForce,
            rotation = data.rotation
        )

        sendGameState(connection)
    }

    private suspend fun sendGameConfiguration(connection: Connection) {
        connection.sendJson(
            Message.GameConfiguration(
                mapWidth = Game.configuration.mapWidth,
                mapHeight = Game.configuration.mapHeight,
            )
        )
    }

    private suspend fun sendGameState(connection: Connection) {
        val player = Game.getPlayer(connection.id) ?: return
        val players = Game.players.filter { it.id != player.id }

        connection.sendJson(
            Message.GameState(
                player = Message.Player(
                    id = player.id,
                    name = player.name,
                    x = player.x,
                    y = player.y,
                    size = player.size,
                    heading = player.heading,
                ),
                players = players.map {
                    Message.Player(
                        id = it.id,
                        name = it.name,
                        x = it.x,
                        y = it.y,
                        size = it.size,
                        heading = it.heading,
                    )
                }
            )
        )
    }

    private suspend fun handleGameStateMessage(connection: Connection, data: Message.GameState) {
        sendGameState(connection)
    }
}