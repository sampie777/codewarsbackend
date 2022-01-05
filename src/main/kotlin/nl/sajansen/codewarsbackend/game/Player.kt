package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import java.util.*

data class Player(
    val id: Int,
    val name: String,
    var appliedForce: Vector<Float> = Vector(listOf(0f, 0f)),
    var rotation: Float = 0f,
    var velocity: Vector<Float> = Vector(listOf(0f, 0f)),
    var x: Float = 0f,
    var y: Float = 0f,
    var size: Int = Config.playerDefaultSize,
    var orientation: Float = 0f,
) {
    fun copyFrom(player: Player) {
        appliedForce = player.appliedForce
        rotation = player.rotation
        velocity = player.velocity
        x = player.x
        y = player.y
        size = player.size
        orientation = player.orientation
    }
}