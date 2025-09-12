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
    var orientation: Float = 110f,
    var physicModel: PhysicModel = CarPhysicModel(),
) {

    val body: Body
    val wheelLeft: Body
    val wheelRight: Body

    fun createWheelBody(): Body {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DYNAMIC
        val body = world.createBody(bodyDef)

        val dynamicBox = PolygonShape()
        dynamicBox.setAsBox(size.toFloat(), size.toFloat())
        val fixture = body.createFixture(dynamicBox, 1.0f)
        fixture.friction = 0.3f

        return body
    }

    init {

        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DYNAMIC
        bodyDef.position.set(Vec2(x, y))
        body = world.createBody(bodyDef)
        val dynamicBox = PolygonShape()
        dynamicBox.setAsBox(size.toFloat(), size.toFloat())

        val fixture = body.createFixture(dynamicBox, 1.0f)
        fixture.friction = 0.3f

        wheelLeft = createWheelBody()
        wheelLeft.position.set(Vec2((x - size / 2), (y - size / 2)))
        wheelRight = createWheelBody()
        wheelRight.position.set(Vec2((x + size / 2), (y + size / 2)))

        val jointDefLeft = WeldJointDef()
        jointDefLeft.initialize(wheelLeft, body, Vec2((size / -2).toFloat(), (size / -2).toFloat()))
        world.createJoint(jointDefLeft)

        val jointDefRight = WeldJointDef()
        jointDefRight.initialize(body, wheelRight, Vec2((size / -2).toFloat(), (size / -2).toFloat()))
        world.createJoint(jointDefRight)

    }

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
        wheelLeft.applyForceToCenter(Vec2(100000f, 0f))
        x = body.position.x
        y = body.position.y
        velocity[0] = body.linearVelocity.x
        velocity[1] = body.linearVelocity.y
    }
}