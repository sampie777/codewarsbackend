package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.utils.*
import org.slf4j.LoggerFactory
import kotlin.concurrent.fixedRateTimer
import kotlin.math.cos
import kotlin.math.sin

object Game {
    private val logger = LoggerFactory.getLogger(this.toString())

    val players: ArrayList<Player> = arrayListOf()

    data class State(
        var name: String,
        var x: Float,
        var y: Float,
        var size: Int,
        var heading: Float,
        var players: List<Player>,
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

    fun createPlayer(id: Int, name: String) {
        val player = Player(id, name)
        players.add(player)
    }

    fun removePlayer(id: Int) {
        val player = getPlayer(id) ?: return
        logger.info("Removing player ${player.name}")
        players.remove(player)
        logger.info("Players left: ${players.size}")
    }

    private fun getPlayer(id: Int): Player? {
        return players.find { it.id == id }
    }

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

    fun getStateForPlayer(id: Int): State? {
        val player = getPlayer(id) ?: return null
        return State(
            name = player.name,
            x = player.x,
            y = player.y,
            size = player.size,
            heading = player.heading,
            players = players.filter { it.id != player.id }
        )
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
        val netForce = it.appliedForce.add(frictionForce)
        val acceleration = netForce.divideBy(mass)

        it.velocity = it.velocity.add(acceleration.divideBy(Config.gameStepsPerSecond))

        it.x += it.velocity[0] / Config.gameStepsPerSecond
        it.y -= it.velocity[1] / Config.gameStepsPerSecond
    }

    private fun constrainPlayerMovement(it: Player) {
        if (it.x - it.size / 2 < 0) {
            it.x = it.size / 2f
        } else if (it.x + it.size / 2 > Config.boardWidth) {
            it.x = Config.boardWidth - it.size / 2f
        }

        if (it.y - it.size / 2 < 0) {
            it.y = it.size / 2f
        } else if (it.y + it.size / 2 > Config.boardHeight) {
            it.y = Config.boardHeight - it.size / 2f
        }
    }
}