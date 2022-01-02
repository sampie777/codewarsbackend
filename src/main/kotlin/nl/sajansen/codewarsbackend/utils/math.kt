package nl.sajansen.codewarsbackend.utils

import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sqrt

fun Vector<Float>.add(vector: Vector<Float>): Vector<Float> {
    if (this.size != vector.size) {
        throw ArrayIndexOutOfBoundsException("Vectors are not the same size: ${this.size} != ${vector.size}")
    }

    val result = Vector<Float>(this.size)
    (0 until this.size).forEach { i -> result.add(this[i] + vector[i]) }
    return result
}

fun Vector<Float>.divideBy(by: Int): Vector<Float> {
    val result = Vector<Float>(this.size)
    this.forEach { result.add(it / by) }
    return result
}

fun Vector<Float>.divideBy(by: Float): Vector<Float> {
    val result = Vector<Float>(this.size)
    this.forEach { result.add(it / by) }
    return result
}

fun Vector<Float>.multiplyBy(by: Float): Vector<Float> {
    val result = Vector<Float>(this.size)
    this.forEach { result.add(it * by) }
    return result
}

fun Vector<Float>.length(): Float {
    return when (this.size) {
        0 -> 0f
        1 -> abs(this[0])
        2 -> sqrt(this[0] * this[0] + this[1] * this[1])
        else -> throw NotImplementedError("Length calculation for vector with size ${this.size} not yet implemented")
    }
}

fun Vector<Float>.normalize(): Vector<Float> {
    val result = Vector<Float>(this.size)
    val length = this.length()
    if (length == 0f) {
        this.forEach { result.add(0f) }
    } else {
        this.forEach { result.add(it / length) }
    }
    return result
}

fun degToRad(deg: Float) = (deg / 180.0f * PI).toFloat()