package nl.sajansen.codewarsbackend.ws

object Message {
    enum class Type {
        IDENTIFY,
        PLAYER_STATE,
        GAME_STATE,
    }

    open class BaseMessage(val type: Type)

    data class Identify(
        val id: Int?,
    ) : BaseMessage(type = Type.IDENTIFY)


    data class PlayerState(
        val appliedForce: Float?,
        val rotation: Float?,
    ) : BaseMessage(type = Type.PLAYER_STATE)


    data class Player(
        val id: Int,
        val name: String,
        val x: Float,
        val y: Float,
        val size: Int,
        val heading: Float,
    )


    data class GameState(
        val name: String,
        val x: Float,
        val y: Float,
        val size: Int,
        val heading: Float,
        val players: List<Player>,
    ) : BaseMessage(type = Type.GAME_STATE)
}