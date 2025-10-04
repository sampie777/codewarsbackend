package nl.sajansen.codewarsbackend.game

import nl.sajansen.codewarsbackend.config.Config
import nl.sajansen.codewarsbackend.game.physics.CarPhysicModel
import nl.sajansen.codewarsbackend.game.physics.PhysicModel
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.World
import org.jbox2d.dynamics.joints.WeldJointDef
import java.util.*

data class Player(
    val id: Int,
    val name: String,
    val world: World,
    var appliedForce: Vector<Float> = Vector(listOf(0f, 0f)),
    var rotation: Float = 0f,
    var velocity: Vector<Float> = Vector(listOf(0f, 0f)),
    var x: Float = 100f,
    var y: Float = 100f,
    var size: Int = Config.playerDefaultSize,
    var orientation: Float = 0f,
    var physicModel: PhysicModel = CarPhysicModel(),
) {

    val tank: Tank = Tank(world, x, y, size.toFloat(), size.toFloat(), (size / 4).toFloat(), size.toFloat())

    fun copyFrom(player: Player) {
        appliedForce = player.appliedForce
        rotation = player.rotation
        velocity = player.velocity
        x = player.x
        y = player.y
        size = player.size
        orientation = player.orientation
        physicModel = player.physicModel
    }

    fun calculateAndApplyForces() {
//        physicModel.calculateAndApplyPlayerForces(this)
        tank.applyTrackForces(appliedForce[0], appliedForce[1])

        x = tank.hull.position.x
        y = tank.hull.position.y
        orientation = tank.hull.angle * (180f / Math.PI).toFloat() + 90f
        velocity[0] = tank.hull.linearVelocity.x
        velocity[1] = tank.hull.linearVelocity.y
    }
}