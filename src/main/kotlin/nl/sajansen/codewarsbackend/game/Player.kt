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
    var heading: Float = 0f,
)