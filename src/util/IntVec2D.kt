package util

data class IntVec2D(val x: Int, val y: Int) {

    companion object {
        val ORIGIN = IntVec2D(0, 0)
    }

    operator fun plus(other: IntVec2D) = IntVec2D(x + other.x, y + other.y)

    operator fun minus(other: IntVec2D) = IntVec2D(x - other.x, y - other.y)

    operator fun unaryMinus() = IntVec2D(-x, -y)

    override fun toString() = "($x, $y)"
}

val IntVec2D.neighbors
    get() = listOf(
        this + IntVec2D(0, -1),
        this + IntVec2D(-1, 0),
        this + IntVec2D(1, 0),
        this + IntVec2D(0, 1),
    )

val IntVec2D.mooreNeighbors
    get() = listOf(
        this + IntVec2D(-1, -1),
        this + IntVec2D(0, -1),
        this + IntVec2D(1, -1),
        this + IntVec2D(-1, 0),
        this + IntVec2D(1, 0),
        this + IntVec2D(-1, 1),
        this + IntVec2D(0, 1),
        this + IntVec2D(1, 1),
    )

val IntVec2D.up get() = this + IntVec2D(0, -1)

val IntVec2D.left get() = this + IntVec2D(-1, 0)

val IntVec2D.right get() = this + IntVec2D(1, 0)

val IntVec2D.down get() = this + IntVec2D(0, 1)

val IntVec2D.clockwise get() = IntVec2D(-y, x)

val IntVec2D.antiClockwise get() = IntVec2D(y, -x)

fun search(
    startingLocation: IntVec2D,
    predicate: (IntVec2D) -> Boolean = { true },
    nextLocation: (IntVec2D) -> Collection<IntVec2D> = { it.neighbors },
): Sequence<IntVec2D> = sequence {
    if (!predicate(startingLocation)) return@sequence
    val addedToSearch = mutableSetOf(startingLocation)
    val toSearch = mutableListOf(startingLocation)
    while (toSearch.isNotEmpty()) {
        val head = toSearch.removeLast()
        yield(head)
        val nextPoints = nextLocation(head).filter { it !in addedToSearch && predicate(it) }
        addedToSearch.addAll(nextPoints)
        toSearch.addAll(nextPoints)
    }
}
