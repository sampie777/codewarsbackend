package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.utils.degToRad
import nl.sajansen.codewarsbackend.utils.limitDegrees
import org.slf4j.LoggerFactory
import kotlin.concurrent.fixedRateTimer
import kotlin.math.cos
import kotlin.math.sin

object Game {
    private val logger = LoggerFactory.getLogger(this.toString())

    val players: ArrayList<Player> = arrayListOf()
    val configuration = Configuration(
        mapWidth = Config.boardWidth,
        mapHeight = Config.boardHeight
    )

    fun start() {
        logger.info("Starting game at ${Config.gameStepsPerSecond} steps per second")
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
            it.orientation = limitDegrees(it.orientation + it.rotation)

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
        player.calculateAndApplyForces()
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