package y2021.d25

import AdventOfCode
import util.IntVec2D
import util.charSequence

private data class State(val east: Set<IntVec2D>, val south: Set<IntVec2D>, )

private fun step(state: State, width: Int, height: Int): Pair<State, Int> {
    var moved = 0
    val newEast = state.east.map { position ->
        val newPosition = position.copy(x = (position.x + 1).mod(width))
        if (newPosition !in state.east && newPosition !in state.south) {
            moved++
            return@map newPosition
        } else {
            return@map position
        }
    }.toSet()
    val newSouth = state.south.map { position ->
        val newPosition = position.copy(y = (position.y + 1).mod(height))
        if (newPosition !in newEast && newPosition !in state.south) {
            moved++
            return@map newPosition
        } else {
            return@map position
        }
    }.toSet()
    return Pair(State(newEast, newSouth), moved)
}

class y2021d25p1 : AdventOfCode({
    testFile("t01.txt", 58)
}, { lines ->
    val width = lines[0].length
    val height = lines.count { it.isNotEmpty() }
    val cucumbers = lines.charSequence().filter {it.value != ','}
    val south = cucumbers.filter {it.value == 'v'}.map {it.position}.toSet()
    val east = cucumbers.filter {it.value == '>'}.map {it.position}.toSet()
    var state = State(east, south)
    var numSteps = 0
    while (true) {
        val (newState, numMoved) = step(state, width, height)
        if (numMoved == 0) break
        state = newState
        numSteps += 1
    }
    numSteps + 1
})