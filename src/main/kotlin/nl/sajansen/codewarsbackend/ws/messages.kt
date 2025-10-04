package nl.sajansen.codewarsbackend.ws

import java.util.Vector

object Message {
    enum class Type {
        IDENTIFY,
        PLAYER_STATE,
        GAME_STATE,
        GAME_CONFIGURATION,
        SERVER_INFORMATION,
    }

    open class BaseMessage(val type: Type)

    data class Identify(
        val id: Int?,
    ) : BaseMessage(type = Type.IDENTIFY)

    data class PlayerState(
        val appliedForce: Vector<Float>?,
    ) : BaseMessage(type = Type.PLAYER_STATE)

    data class Player(
        val id: Int,
        val name: String,
        val x: Float,
        val y: Float,
        val size: Int,
        val orientation: Float,
        val hull: Any? = null,
        val leftTrack: Any? = null,
        val rightTrack: Any? = null,
    )

    data class GameState(
        val player: Player,
        val players: List<Player>,
    ) : BaseMessage(type = Type.GAME_STATE)

    data class GameConfiguration(
        val mapWidth: Int,
        val mapHeight: Int,
    ) : BaseMessage(type = Type.GAME_CONFIGURATION)

    data class ServerInformation(
        val version: String,
    ) : BaseMessage(type = Type.SERVER_INFORMATION)
}