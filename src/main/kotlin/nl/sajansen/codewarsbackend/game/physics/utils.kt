package nl.sajansen.codewarsbackend.game.physics

import nl.sajansen.codewarsbackend.game.Player
import java.util.*
import kotlin.math.abs


fun preventVelocityGlitchingByTooLargeFriction(player: Player, accelerationStep: Vector<Float>) {
    (0..1).forEach { direction ->
        if (accelerationStep[direction] < 0 && player.velocity[direction] > 0 && abs(accelerationStep[direction]) > player.velocity[direction]) {
            accelerationStep[direction] = -1 * player.velocity[direction]
        } else if (accelerationStep[direction] > 0 && player.velocity[direction] < 0 && accelerationStep[direction] > abs(player.velocity[direction])) {
            accelerationStep[direction] = -1 * player.velocity[direction]
        }
    }
}