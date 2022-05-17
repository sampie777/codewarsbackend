package nl.sajansen.codewarsbackend.game.physics

import nl.sajansen.codewarsbackend.game.Player

interface PhysicModel {
    fun calculateAndApplyPlayerForces(player: Player)
}