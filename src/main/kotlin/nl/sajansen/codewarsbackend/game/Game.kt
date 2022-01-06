package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.utils.*
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

object Game {
    private val logger = LoggerFactory.getLogger(this.toString())

    val players: ArrayList<Player> = arrayListOf()
    val configuration = Configuration(
        mapWidth = Config.boardWidth,
        mapHeight = Config.boardHeight
    )

    fun start() {
        fixedRateTimer(
            name = "gameStepTimer",
            daemon = true,
            period = (1000 / Config.gameStepsPerSecond).toLong()
        ) {
            step()
        }
    }

    fun createPlayer(id: Int, name: String): Player {
        val player = Player(id, name)
        players.add(player)
        return player
    }

    fun removePlayer(id: Int) {
        val player = getPlayer(id) ?: return
        logger.info("Removing player ${player.name}")
        players.remove(player)
        logger.info("Players left: ${players.size}")
    }

    fun getPlayer(id: Int): Player? = players.find { it.id == id }

    fun updatePlayer(id: Int, appliedForce: Float?, rotation: Float?) {
        getPlayer(id)?.let {
            it.rotation = rotation ?: it.rotation
            it.orientation += it.rotation

            if (appliedForce != null) {
                it.appliedForce[0] = appliedForce * sin(degToRad(it.orientation))
                it.appliedForce[1] = appliedForce * cos(degToRad(it.orientation))
            }
        }
    }

    private fun step() {
        players.toTypedArray().forEach {
            calculatePlayerForces(it)
            constrainPlayerMovement(it)
        }
    }

    private fun calculatePlayerForces(player: Player) {
        val mass = player.size * Config.playerDensity
        val normalForce = mass * 9.81f
        val frictionForce = player.velocity.normalize().multiplyBy(-1 * Config.playerFrictionConstant * normalForce)
        val dragForce = player.velocity.normalize().multiplyBy(
            -0.5f * Config.airDensity
                    * player.velocity.length().pow(2)
                    * Config.boardDragCoefficient
                    * player.size
        )
        val netForce = player.appliedForce.add(frictionForce).add(dragForce)
        val acceleration = netForce.divideBy(mass)

        val accelerationStep = acceleration.divideBy(Config.gameStepsPerSecond)

        if (player.appliedForce.length() == 0f) {
            preventVelocityGlitchingByTooLargeFriction(player, accelerationStep)
        }

        player.velocity = player.velocity.add(accelerationStep)

        player.x += player.velocity[0] / Config.gameStepsPerSecond
        player.y -= player.velocity[1] / Config.gameStepsPerSecond
    }

    private fun preventVelocityGlitchingByTooLargeFriction(player: Player, accelerationStep: Vector<Float>) {
        (0..1).forEach { direction ->
            if (accelerationStep[direction] < 0 && player.velocity[direction] > 0 && abs(accelerationStep[direction]) > player.velocity[direction]) {
                accelerationStep[direction] = -1 * player.velocity[direction]
            } else if (accelerationStep[direction] > 0 && player.velocity[direction] < 0 && accelerationStep[direction] > abs(player.velocity[direction])) {
                accelerationStep[direction] = -1 * player.velocity[direction]
            }
        }
    }

    private fun constrainPlayerMovement(player: Player) {
        if (player.x - player.size / 2 < 0) {
            player.x = player.size / 2f
            player.velocity[0] = 0.0f
        } else if (player.x + player.size / 2 > Config.boardWidth) {
            player.x = Config.boardWidth - player.size / 2f
            player.velocity[0] = 0.0f
        }

        if (player.y - player.size / 2 < 0) {
            player.y = player.size / 2f
            player.velocity[1] = 0.0f
        } else if (player.y + player.size / 2 > Config.boardHeight) {
            player.y = Config.boardHeight - player.size / 2f
            player.velocity[1] = 0.0f
        }
    }
}