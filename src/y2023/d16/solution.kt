package y2023.d16

import AdventOfCode
import util.IntVec2D
import util.IntVec2D.Companion.ORIGIN
import util.PosVel
import util.down
import util.get
import util.left
import util.right
import util.up

private fun getNextFront(front: PosVel, lines: List<String>): List<PosVel> {
    val (position, heading) = front
    return when (lines[position]) {
        null -> listOf()
        '.' -> listOf(front.advance())
        '/' -> when (heading) {
            ORIGIN.right, ORIGIN.left -> listOf(front.turnLeft().advance())
            else -> listOf(front.turnRight().advance())
        }
        '\\' -> when (heading) {
            ORIGIN.right, ORIGIN.left -> listOf(front.turnRight().advance())
            else -> listOf(front.turnLeft().advance())
        }
        '-' -> when (heading) {
            ORIGIN.left, ORIGIN.right -> listOf(front.advance())
            else -> listOf(front.turnLeft().advance(), front.turnRight().advance())
        }
        '|' -> when (heading) {
            ORIGIN.up, ORIGIN.down -> listOf(front.advance())
            else -> listOf(front.turnLeft().advance(), front.turnRight().advance())
        }
        else -> error("")
    }
}

class y2023d16p1 : AdventOfCode({
    testFile("t01.txt", 46)
}, { lines ->
    val fronts = mutableListOf(PosVel(ORIGIN, ORIGIN.right))
    val visited = fronts.toMutableSet()
    while (fronts.isNotEmpty()) {
        val front = fronts.removeLast()
        getNextFront(front, lines).forEach { nextFront ->
            if (lines[nextFront.position] == null) return@forEach
            if (visited.add(nextFront)) fronts.add(nextFront)
        }
    }
    visited.map { it.position }.toSet().size
})

private fun findPathUntilSplit(
    initialFront: PosVel,
    lines: List<String>,
    cache: MutableMap<PosVel, List<PosVel>>,
): List<PosVel> = cache.getOrPut(initialFront) {
    buildList {
        var front = initialFront
        add(front)
        while (true) {
            val nextFronts = getNextFront(front, lines)
            if (nextFronts.size != 1) break
            front = nextFronts.first()
            if (lines[front.position] == null) break
            add(front)
            front = nextFronts.first()
        }
    }
}

class y2023d16p2 : AdventOfCode({
    testFile("t01.txt", 51)
}, { lines ->
    val cache = mutableMapOf<PosVel, List<PosVel>>()

    fun findNumEnergizedTiles(initialFront: PosVel): Int {
        val fronts = mutableListOf(initialFront)
        val visited = mutableSetOf(initialFront)
        while (fronts.isNotEmpty()) {
            val front = fronts.removeLast()
            val path = findPathUntilSplit(front, lines, cache)
            visited.addAll(path)
            if (path.isEmpty()) continue
            val nextFronts = getNextFront(path.last(), lines)
            nextFronts.forEach {
                if (lines[it.position] == null) return@forEach
                if (visited.add(it)) fronts.add(it)
            }
        }
        return visited.map { it.position }.toSet().size
    }

    val top = (0..lines[0].lastIndex).map { PosVel(IntVec2D(it, 0), ORIGIN.down) }
    val bottom = (0..lines[0].lastIndex).map { PosVel(IntVec2D(it, lines.lastIndex), ORIGIN.up) }
    val left = (0..lines.lastIndex).map { PosVel(IntVec2D(0, it), ORIGIN.right) }
    val right = (0..lines.lastIndex).map { PosVel(IntVec2D(lines[0].lastIndex, it), ORIGIN.left) }
    (top + bottom + left + right).maxOf { findNumEnergizedTiles(it) }
})
