package y2023.d14

import AdventOfCode
import util.IntVec2D
import util.charSequence
import util.down
import util.left
import util.right
import util.up

private fun tilt(
    roundRocks: Set<IntVec2D>,
    squareRocks: Set<IntVec2D>,
    direction: (IntVec2D) -> IntVec2D,
    maxBound: IntVec2D,
): MutableSet<IntVec2D> {
    val rollingRoundRocks = roundRocks.toMutableSet()
    for (rock in roundRocks) {
        rollingRoundRocks.remove(rock)
        val newRock =
            generateSequence(rock) { direction(it) }.takeWhile { it.x in 0..maxBound.x && it.y in 0..maxBound.y && it !in squareRocks }
                .filter { it !in rollingRoundRocks }.last()
        rollingRoundRocks.add(newRock)
    }
    return rollingRoundRocks
}

class y2023d14p1 : AdventOfCode({
    testFile("t01.txt", 136)
}, { lines ->
    val roundRocks = lines.charSequence().filter { it.value == 'O' }.map { it.position }.toSet()
    val squareRocks = lines.charSequence().filter { it.value == '#' }.map { it.position }.toSet()
    tilt(
        roundRocks,
        squareRocks,
        IntVec2D::up,
        IntVec2D(lines[0].lastIndex, lines.lastIndex),
    ).sumOf { lines.size - it.y }
})

class y2023d14p2 : AdventOfCode({
    testFile("t01.txt", 64)
}, { lines ->
    var roundRocks = lines.charSequence().filter { it.value == 'O' }.map { it.position }.toSet()
    val squareRocks = lines.charSequence().filter { it.value == '#' }.map { it.position }.toSet()
    val bounds = IntVec2D(lines[0].lastIndex, lines.lastIndex)
    val states = mutableMapOf<Set<IntVec2D>, Int>()
    for (cycle in 1..1000000000) {
        roundRocks = roundRocks.let { tilt(it, squareRocks, IntVec2D::up, bounds) }
            .let { tilt(it, squareRocks, IntVec2D::left, bounds) }.let { tilt(it, squareRocks, IntVec2D::down, bounds) }
            .let { tilt(it, squareRocks, IntVec2D::right, bounds) }
        if (roundRocks in states) {
            val previousCycle = states.getValue(roundRocks)
            val cycleLeft = 1_000_000_000 - cycle
            val remainder = cycleLeft % (cycle - previousCycle)
            val finalRocks = states.entries.filter { it.value == previousCycle + remainder }.first().key
            roundRocks = finalRocks
            break
        }
        states[roundRocks] = cycle
    }
    roundRocks.sumOf { lines.size - it.y }
})
