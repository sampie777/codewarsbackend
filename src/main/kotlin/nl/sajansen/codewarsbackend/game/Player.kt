package nl.sajansen.codewarsbackend.game

data class Player(
    val id: Int,
    val name: String,
    var acceleration: Float = 0f,
    var rotation: Float = 0f,
    var x: Float = 0f,
    var y: Float = 0f,
    var size: Int = 0,
    var heading: Float = 0f,
)