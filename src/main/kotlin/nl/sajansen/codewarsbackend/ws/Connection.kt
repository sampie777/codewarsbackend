package nl.sajansen.codewarsbackend.ws

import io.ktor.http.cio.websocket.*
import jsonBuilder
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val id = lastId.getAndIncrement()
    val name = "user${id}"
    var lastMessageTime = Date()
}

suspend fun Connection.sendJson(data: Any) {
    val text = jsonBuilder(prettyPrint = false)
        .toJson(data)
    session.send(text)
}

suspend fun Connection.identify() {
    sendJson(Message.Identify(id = id))
}