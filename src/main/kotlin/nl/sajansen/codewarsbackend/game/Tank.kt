package nl.sajansen.codewarsbackend.game

import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Rot
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import org.jbox2d.dynamics.joints.WeldJointDef
import kotlin.math.roundToInt

class Tank(
    world: World,
    x: Float = 100f,
    y: Float = 100f,
    val hullWidth: Float = 2f,
    val hullHeight: Float = 4f,
    val trackWidth: Float = 0.5f,
    val trackHeight: Float = 4f
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
            density = 1.0f
            friction = 0.0f
        }
        hull.createFixture(hullFixtureDef)

        // Create left track
        val leftTrackDef = BodyDef()
        leftTrackDef.type = BodyType.DYNAMIC
        leftTrackDef.position.set(Vec2(x - hullWidth / 2 - trackWidth / 2, y))
        leftTrack = world.createBody(leftTrackDef)
        val leftTrackShape = PolygonShape()
        leftTrackShape.setAsBox(trackWidth / 2, trackHeight / 2)
        val leftTrackFixtureDef = FixtureDef().apply {
            shape = leftTrackShape
            density = 1.0f
            friction = 1.0f
        }
        leftTrack.createFixture(leftTrackFixtureDef)
        leftTrack.linearDamping = 200f

        // Create right track
        val rightTrackDef = BodyDef()
        rightTrackDef.type = BodyType.DYNAMIC
        rightTrackDef.position.set(Vec2(x + hullWidth / 2 + trackWidth / 2, y))
        rightTrack = world.createBody(rightTrackDef)
        val rightTrackShape = PolygonShape()
        rightTrackShape.setAsBox(trackWidth / 2, trackHeight / 2)
        val rightTrackFixtureDef = FixtureDef().apply {
            shape = rightTrackShape
            density = 1.0f
            friction = 1.0f
        }
        rightTrack.createFixture(rightTrackFixtureDef)
        rightTrack.linearDamping = 200f

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
        val leftRelativeForce = Vec2().also { Rot.mulToOut(Rot(leftTrack.angle), Vec2(0f, -1 * leftForce), it)}
        leftTrack.applyForceToCenter(leftRelativeForce)
        leftTrack.linearDamping = if (leftForce.roundToInt() == 0) 200f else 0f

        val rightRelativeForce = Vec2().also { Rot.mulToOut(Rot(rightTrack.angle), Vec2(0f, -1 * rightForce), it)}
        rightTrack.applyForceToCenter(rightRelativeForce)
        rightTrack.linearDamping = if (rightForce.roundToInt() == 0) 200f else 0f
    }
}