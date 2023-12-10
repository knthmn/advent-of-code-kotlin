package y2023.d10

import AdventOfCode
import util.IntVec2D
import util.IntVec2D.Companion.ORIGIN
import util.antiClockwise
import util.clockwise
import util.down
import util.get
import util.left
import util.neighbors
import util.positionOf
import util.right
import util.search
import util.up

private fun getNextDirection(map: List<String>, pipePosition: IntVec2D, enterDirection: IntVec2D): IntVec2D? {
    val pipe = map[pipePosition] ?: return null
    fun getExitDirection(exitDirection1: IntVec2D, exitDirection2: IntVec2D) =
        if (enterDirection == -exitDirection1) exitDirection2 else if (enterDirection == -exitDirection2) exitDirection1 else null
    return when (pipe) {
        '|' -> getExitDirection(ORIGIN.up, ORIGIN.down)
        '-' -> getExitDirection(ORIGIN.left, ORIGIN.right)
        'L' -> getExitDirection(ORIGIN.up, ORIGIN.right)
        'J' -> getExitDirection(ORIGIN.left, ORIGIN.up)
        '7' -> getExitDirection(ORIGIN.left, ORIGIN.down)
        'F' -> getExitDirection(ORIGIN.down, ORIGIN.right)
        else -> null
    }
}

class y2023d10p1 : AdventOfCode({
    testFile("t01.txt", 4)
    testFile("t02.txt", 8)
}, { lines ->
    val startingLocation = lines.positionOf('S') ?: error("cannot find S")
    var heads = ORIGIN.neighbors.filter { direction ->
        getNextDirection(lines, startingLocation + direction, direction) != null
    }.map { startingLocation to it }
    var step = 0
    while (true) {
        step += 1
        heads = heads.map { (location, direction) ->
            val nextLocation = location + direction
            nextLocation to (getNextDirection(lines, nextLocation, direction) ?: error(""))
        }
        if (heads.map { it.first }.toSet().size == 1) break
    }
    step
})

class y2023d10p2 : AdventOfCode({
    testFile("t03.txt", 4)
    testFile("t04.txt", 8)
}, { lines ->
    val startingPosition = lines.positionOf('S') ?: error("cannot find S")
    val startingDirection =
        ORIGIN.neighbors.first { direction -> getNextDirection(lines, startingPosition + direction, direction) != null }
    val loop = buildSet {
        var position = startingPosition
        var direction = startingDirection
        add(startingPosition)
        while (true) {
            position += direction
            if (lines[position] == 'S') break
            direction = getNextDirection(lines, position, direction) ?: error("")
            add(position)
        }
    }

    val leftTiles = mutableSetOf<IntVec2D>()
    val rightTiles = mutableSetOf<IntVec2D>()
    var position = startingPosition
    var direction = startingDirection
    fun addTiles() {
        fun predicate(newPosition: IntVec2D) =
            newPosition !in loop && newPosition !in leftTiles && newPosition !in rightTiles && lines[newPosition] != null
        leftTiles += search(position + direction.clockwise, ::predicate)
        rightTiles += search(position + direction.antiClockwise, ::predicate)
    }
    while (true) {
        position += direction
        addTiles()
        if (lines[position.y][position.x] == 'S') break
        direction = getNextDirection(lines, position, direction) ?: error("")
        addTiles()
    }
    if (ORIGIN in leftTiles) rightTiles.size else leftTiles.size
})
