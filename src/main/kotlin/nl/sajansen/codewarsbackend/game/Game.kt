package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.fixedRateTimer

object Game {
    private val logger = LoggerFactory.getLogger(this.toString())

    private val players: MutableSet<Player> = Collections.synchronizedSet(LinkedHashSet())

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
    }

    private fun getPlayer(id: Int): Player? {
        return players.find { it.id == id }
    }

    fun updatePlayer(id: Int, acceleration: Float?, rotation: Float?) {
        getPlayer(id)?.let {
            it.acceleration = acceleration ?: it.acceleration
            it.rotation = rotation ?: it.rotation
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
            it.velocity += it.acceleration / Config.gameStepsPerSecond
            it.x += it.velocity / Config.gameStepsPerSecond
        }
    }
}