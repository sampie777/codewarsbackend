package nl.sajansen.codewarsbackend.game.physics

import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.game.Player
import nl.sajansen.codewarsbackend.utils.*
import java.util.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class CarPhysicModel : PhysicModel {
    override fun calculateAndApplyPlayerForces(player: Player) {
        val mass = player.size * Config.playerDensity
        val normalForce = mass * 9.81f
        val frictionForce = player.velocity.normalize().multiplyBy(-1 * Config.playerFrictionConstant * normalForce)
        val dragForce = player.velocity.normalize().multiplyBy(
            -0.5f * Config.airDensity
                    * player.velocity.length().pow(2)
                    * Config.boardDragCoefficient
                    * player.size
        )
        val engineIdleFrictionForce = if (player.appliedForce.length() == 0f) {
            player.velocity.normalize().multiplyBy(-1 * Config.playerEngineIdleFrictionConstant)
        } else Vector(listOf(0f, 0f))

        val netForce = player.appliedForce
            .add(frictionForce)
            .add(dragForce)
            .add(engineIdleFrictionForce)

        val acceleration = netForce.divideBy(mass)
        val accelerationStep = acceleration.divideBy(Config.gameStepsPerSecond)

        if (player.appliedForce.length() == 0f) {
            preventVelocityGlitchingByTooLargeFriction(player, accelerationStep)
        }

        player.velocity = player.velocity.add(accelerationStep)

        // Calculate forward directed result vector
        var velocityAmplitude = player.velocity.length()
        val orientationDifferenceWithVelocity = abs(player.orientation - player.velocity.angle())
        if (orientationDifferenceWithVelocity > 90 && orientationDifferenceWithVelocity < 270) {
            velocityAmplitude *= -1
        }
        player.velocity[0] = velocityAmplitude * sin(degToRad(player.orientation))
        player.velocity[1] = velocityAmplitude * cos(degToRad(player.orientation))

        player.x += player.velocity[0] / Config.gameStepsPerSecond
        player.y -= player.velocity[1] / Config.gameStepsPerSecond
    }
}