package nl.sajansen.codewarsbackend.ws

object Message {
    open class BaseMessage(val type: Type)

    data class Identify(
        val id: Int?,
    ) : BaseMessage(type = Type.IDENTIFY)


    data class PlayerState(
        val acceleration: Float?,
        val rotation: Float?,
    ) : BaseMessage(type = Type.PLAYER_STATE)


    data class GameState(
        val x: Float,
        val y: Float,
        val size: Float,
        val heading: Float,
    ) : BaseMessage(type = Type.PLAYER_STATE)
}