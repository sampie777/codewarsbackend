package nl.sajansen.codewarsbackend.ws

import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ws.routes")

fun Route.websocketRoutes() {
    webSocket("/ws") {
        try {
            val connection = Server.newConnection(this)
            handleMessages(connection)
        } catch (e: ClosedReceiveChannelException) {
            logger.error("Websocket closing: ${closeReason.await()}")
        } catch (e: Throwable) {
            logger.error("Websocket error: ${closeReason.await()}")
            e.printStackTrace()
        }
    }
}

private suspend fun DefaultWebSocketServerSession.handleMessages(connection: Connection) {
    for (frame in incoming) {
        when (frame) {
            is Frame.Text -> Server.handleTextMessage(connection, frame)
            is Frame.Close -> Server.closeConnection(connection)
            is Frame.Ping -> logger.info("${connection.name}: Ping")
            is Frame.Pong -> logger.info("${connection.name}: Pong")
        }
    }
}