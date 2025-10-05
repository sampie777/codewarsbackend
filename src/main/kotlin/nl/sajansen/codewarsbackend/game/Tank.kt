package nl.sajansen.codewarsbackend.game

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import org.jbox2d.dynamics.joints.WeldJointDef

class Tank(
    world: World,
    x: Float = 100f,
    y: Float = 100f,
    val hullWidth: Float = 2f,
    val hullHeight: Float = 4f,
    val trackWidth: Float = 0.5f,
    val trackHeight: Float = 4f,
    val density: Float = 6f
) {
    val hull: Body
    val leftTrack: Body
    val rightTrack: Body

    init {
        // Create hull
        val hullDef = BodyDef()
        hullDef.type = BodyType.DYNAMIC
        hullDef.position.set(Vec2(x, y))
        hull = world.createBody(hullDef)
        val hullShape = PolygonShape()
        hullShape.setAsBox(hullWidth / 2, hullHeight / 2)
        val hullFixtureDef = FixtureDef().apply {
            shape = hullShape
            this.density = this@Tank.density
            friction = 0.4f
        }
        hull.createFixture(hullFixtureDef)
        hull.linearDamping = 20f

        // Create left track
        val leftTrackDef = BodyDef()
        leftTrackDef.type = BodyType.DYNAMIC
        leftTrackDef.position.set(Vec2(x - hullWidth / 2 - trackWidth / 2, y))
        leftTrack = world.createBody(leftTrackDef)
        val leftTrackShape = PolygonShape()
        leftTrackShape.setAsBox(trackWidth / 2, trackHeight / 2)
        val leftTrackFixtureDef = FixtureDef().apply {
            shape = leftTrackShape
            this.density = this@Tank.density
            friction = 0.5f
        }
        leftTrack.createFixture(leftTrackFixtureDef)

        // Create right track
        val rightTrackDef = BodyDef()
        rightTrackDef.type = BodyType.DYNAMIC
        rightTrackDef.position.set(Vec2(x + hullWidth / 2 + trackWidth / 2, y))
        rightTrack = world.createBody(rightTrackDef)
        val rightTrackShape = PolygonShape()
        rightTrackShape.setAsBox(trackWidth / 2, trackHeight / 2)
        val rightTrackFixtureDef = FixtureDef().apply {
            shape = rightTrackShape
            this.density = this@Tank.density
            friction = 0.5f
        }
        rightTrack.createFixture(rightTrackFixtureDef)

        // Weld left track to hull
        val leftJointDef = WeldJointDef()
        leftJointDef.initialize(hull, leftTrack, leftTrack.position)
        world.createJoint(leftJointDef)

        // Weld right track to hull
        val rightJointDef = WeldJointDef()
        rightJointDef.initialize(hull, rightTrack, rightTrack.position)
        world.createJoint(rightJointDef)
    }

    fun applyTrackForces(leftForce: Float, rightForce: Float) {
        // We inverse the force to fix the mirrored left/right forward/backward movement at the client
        val leftRelativeForce = leftTrack.getWorldVector(Vec2(0f, -1f * leftForce))
        leftTrack.applyForceToCenter(leftRelativeForce)

        val rightRelativeForce = rightTrack.getWorldVector(Vec2(0f, -1f * rightForce))
        rightTrack.applyForceToCenter(rightRelativeForce)

        handleDrift(0.0f)
    }

    fun handleDrift(driftFactor: Float = 0.0f) {
        listOf(hull, leftTrack, rightTrack).forEach { body ->
            val forwardNormal = body.getWorldVector(Vec2(0f, 1f))
            val lateralNormal = body.getWorldVector(Vec2(1f, 0f))

            val currentForwardSpeed = forwardNormal.mul(Vec2.dot(forwardNormal, body.linearVelocity))
            val currentLateralSpeed = lateralNormal.mul(Vec2.dot(lateralNormal, body.linearVelocity))

            body.linearVelocity = currentForwardSpeed.add(currentLateralSpeed.mul(driftFactor))
            body.angularVelocity *= driftFactor
        }
    }
}