package util

data class IntVec3D(val x: Int, val y: Int, val z: Int) {

    companion object {
        val ORIGIN = IntVec3D(0, 0, 0)
    }

    operator fun plus(other: IntVec3D) = IntVec3D(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: IntVec3D) = IntVec3D(x - other.x, y - other.y, z - other.z)

    operator fun unaryMinus() = IntVec3D(-x, -y, -z)

    operator fun times(scalar: Int) = IntVec3D(scalar * x, scalar * y, scalar * z)

    override fun toString() = "($x, $y, $z)"
}
