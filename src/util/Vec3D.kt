package util

import org.apache.commons.math3.fraction.BigFraction
import java.math.BigInteger

data class Vec3D(val x: BigInteger, val y: BigInteger, val z: BigInteger) {
    operator fun plus(other: Vec3D) = Vec3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3D) = Vec3D(x - other.x, y - other.y, z - other.z)
    operator fun unaryMinus() = Vec3D(-x, -y, -z)
    operator fun times(scalar: BigInteger) = Vec3D(scalar * x, scalar * y, scalar * z)
    operator fun div(scalar: BigInteger) = Vec3D(x / scalar, y / scalar, z / scalar)
    override fun toString() = "($x, $y, $z)"
}

operator fun BigInteger.times(vec: Vec3D) = vec * this

data class PosVel3D(val position: Vec3D, val velocity: Vec3D) {
    fun advance(steps: BigInteger = 1.toBigInteger()) = copy(position = position + steps * velocity)
    override fun toString() = "$position▶$velocity"
}

infix fun BigInteger.over(other: BigInteger) = BigFraction(this, other)

operator fun BigFraction.plus(other: BigFraction): BigFraction = this.add(other)
operator fun BigFraction.minus(other: BigFraction): BigFraction = this.subtract(other)
operator fun BigFraction.times(other: BigFraction): BigFraction = this.multiply(other)
operator fun BigFraction.div(other: BigFraction): BigFraction = this.divide(other)
operator fun BigFraction.unaryMinus(): BigFraction = this.times((-1).toBigInteger())
val BigInteger.sign get() = if (this > 0.toBigInteger()) 1 else if (this < 0.toBigInteger()) -1 else 0
val BigFraction.sign get() = if (this > BigFraction(0)) 1 else if (this < BigFraction(0)) -1 else 0

operator fun BigFraction.plus(other: BigInteger): BigFraction = this.add(other)
operator fun BigFraction.minus(other: BigInteger): BigFraction = this.subtract(other)
operator fun BigFraction.times(other: BigInteger): BigFraction = this.multiply(other)
operator fun BigFraction.div(other: BigInteger): BigFraction = this.divide(other)

operator fun BigInteger.plus(other: BigFraction): BigFraction = other + this
operator fun BigInteger.minus(other: BigFraction): BigFraction = (-other) + this
operator fun BigInteger.times(other: BigFraction): BigFraction = other * this
operator fun BigInteger.div(other: BigFraction): BigFraction = this * other.reciprocal()
