package y2021.d23

import AdventOfCode
import org.apache.commons.math3.util.ArithmeticUtils.pow
import util.IntVec2D
import util.PositionedValue
import util.charSequence
import util.search
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.min


private fun parseInput(lines: List<String>): Set<PositionedValue<Char>> {
    return lines.charSequence().filter { it.value in 'A'..'D' }.toSet()
}

data class MapState(val amphipods: Set<PositionedValue<Char>>, val energy: Int)

private fun generatePossibleMoves(
    amphipods: Set<PositionedValue<Char>>, amphipod: PositionedValue<Char>, playableSpots: Set<IntVec2D>
): List<Pair<PositionedValue<Char>, Int>> {
    val position = amphipod.position
    val order = amphipod.value.code - 'A'.code
    val homeColumn = 3 + order * 2

    val depth = amphipods.size / 4
    var frozenMinY = 2 + depth
    for (y in (frozenMinY - 1) downTo 2) {
        if (PositionedValue(IntVec2D(homeColumn, y), amphipod.value) in amphipods) {
            frozenMinY = y
        } else {
            break
        }
    }

    if (
        position.x == homeColumn &&
        position.y >= frozenMinY
    ) {
        return listOf()
    }

    val occupiedPositions = amphipods.filter { it != amphipod }.map { it.position }
    val reachableSpots = search(
        position,
        { it !in occupiedPositions && it in playableSpots }
    )
    val validDestinations = reachableSpots.filter { destination ->
        position.y >= 2 && destination.y == 1 && destination.x in setOf(1, 2, 4, 6, 8, 10, 11) ||
                destination.x == homeColumn && destination.y == frozenMinY - 1
    }.filter { it != position }.toList()
    return validDestinations.map { destination ->
        val distanceMoved = abs(destination.x - position.x) + (destination.y - 1) + (position.y - 1)
        val energy = distanceMoved * pow(10, order)
        Pair(PositionedValue(destination, amphipod.value), energy)
    }
}

private fun solve(lines: List<String>): Int {
    val statesToSearch = PriorityQueue<MapState>(10, compareBy { it.energy })
    val initialState = MapState(parseInput(lines), 0)
    val playableSpots =
        lines.charSequence().filter { it.value == '.' || it.value in 'A'..'D' }.map { it.position }.toSet()
    statesToSearch.offer(initialState)
    val stateToMinEnergy = mutableMapOf<Set<PositionedValue<Char>>, Int>()
    var solutionEnergy = Int.MAX_VALUE

    fun isSolution(amphipods: Set<PositionedValue<Char>>): Boolean {
        return amphipods.all { amphipod ->
            val position = amphipod.position
            position.y >= 2 && position.x == 3 + (amphipod.value.code - 'A'.code) * 2
        }
    }

    while (statesToSearch.isNotEmpty()) {
        val state = statesToSearch.poll()
        if (state.energy >= solutionEnergy) {
            continue
        }

        val nextStates = mutableListOf<MapState>()

        amphipodLoop@ for (amphipod in state.amphipods) {
            val moves = generatePossibleMoves(state.amphipods, amphipod, playableSpots)

            for (move in moves) {
                val newAmphipods = buildSet {
                    addAll(state.amphipods)
                    remove(amphipod)
                    add(move.first)
                }
                val newEnergy = state.energy + move.second
                if (isSolution(newAmphipods)) {
                    solutionEnergy = min(solutionEnergy, newEnergy)
                }
                if (move.first.position.y >= 2) {
                    nextStates.clear()
                    nextStates.add(MapState(newAmphipods, state.energy + move.second))
                    break@amphipodLoop
                }
                val newState = MapState(newAmphipods, newEnergy)
                nextStates.add(newState)
            }
        }

        nextStates.forEach { newState ->
            if (newState.energy >= stateToMinEnergy.getOrDefault(
                    newState.amphipods,
                    Int.MAX_VALUE
                ) || newState.energy > solutionEnergy
            ) {
                return@forEach
            }
            stateToMinEnergy[newState.amphipods] = newState.energy
            statesToSearch.offer(newState)
        }
    }
    return solutionEnergy

}

class y2021d23p1 : AdventOfCode({
    testFile("t01.txt", 12521)
}, { lines ->
    solve(lines)
})

class y2021d23p2 : AdventOfCode({
    testFile("t01.txt", 44169)
}, { lines ->
    val realLines = lines.subList(0, 3) + listOf(
        "  #D#C#B#A#  ",
        "  #D#B#A#C#  "
    ) + lines.subList(3, lines.size)
    solve(realLines)
})
