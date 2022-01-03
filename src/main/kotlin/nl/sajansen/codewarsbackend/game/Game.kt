package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.utils.*
import org.slf4j.LoggerFactory
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
            it.heading += it.rotation

            if (appliedForce != null) {
                it.appliedForce[0] = appliedForce * sin(degToRad(it.heading))
                it.appliedForce[1] = appliedForce * cos(degToRad(it.heading))
            }
        }
    }

    private fun step() {
        players.toTypedArray().forEach {
            calculatePlayerForces(it)
            constrainPlayerMovement(it)
        }
    }

    private fun calculatePlayerForces(it: Player) {
        val mass = it.size * Config.playerDensity
        val normalForce = mass * 9.81f
        val frictionForce = it.velocity.normalize().multiplyBy(-1 * Config.playerFrictionConstant * normalForce)
        val dragForce = it.velocity.normalize().multiplyBy(
            -0.5f * Config.airDensity
                    * it.velocity.length().pow(2)
                    * Config.boardDragCoefficient
                    * it.size
        )
        val netForce = it.appliedForce.add(frictionForce).add(dragForce)
        val acceleration = netForce.divideBy(mass)


        val accelerationStep = acceleration.divideBy(Config.gameStepsPerSecond)

        if (it.appliedForce.length() == 0f) {
            (0..1).forEach { direction ->
                if (accelerationStep[direction] < 0 && it.velocity[direction] > 0 && abs(accelerationStep[direction]) > it.velocity[direction]) {
                    accelerationStep[direction] = -1 * it.velocity[direction]
                } else if (accelerationStep[direction] > 0 && it.velocity[direction] < 0 && accelerationStep[direction] > abs(it.velocity[direction])) {
                    accelerationStep[direction] = -1 * it.velocity[direction]
                }
            }
        }

        it.velocity = it.velocity.add(accelerationStep)

        it.x += it.velocity[0] / Config.gameStepsPerSecond
        it.y -= it.velocity[1] / Config.gameStepsPerSecond
    }

    private fun constrainPlayerMovement(it: Player) {
        if (it.x - it.size / 2 < 0) {
            it.x = it.size / 2f
            it.velocity[0] = 0.0f
        } else if (it.x + it.size / 2 > Config.boardWidth) {
            it.x = Config.boardWidth - it.size / 2f
            it.velocity[0] = 0.0f
        }

        if (it.y - it.size / 2 < 0) {
            it.y = it.size / 2f
            it.velocity[1] = 0.0f
        } else if (it.y + it.size / 2 > Config.boardHeight) {
            it.y = Config.boardHeight - it.size / 2f
            it.velocity[1] = 0.0f
        }
    }
}