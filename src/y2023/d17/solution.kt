package y2023.d17

import AdventOfCode
import util.IntVec2D
import util.IntVec2D.Companion.ORIGIN
import util.PosVel
import util.down
import util.right
import java.util.PriorityQueue
import kotlin.math.min

private data class CartState(val posVel: PosVel, val streak: Int)

private operator fun List<List<Int>>.get(pos: IntVec2D) = getOrNull(pos.y)?.getOrNull(pos.x)

private data class CartPath(val state: CartState, val cumHeatLost: Int)

private fun solution(
    lines: List<String>,
    minStreak: Int,
    maxStreak: Int,
): Int {
    val terrain = lines.map { line -> line.map { it.digitToInt() } }
    val heads = PriorityQueue<CartPath>(10) { a, b -> a.cumHeatLost - b.cumHeatLost }
    listOf(ORIGIN.right, ORIGIN.down).forEach { heads.offer(CartPath(CartState(PosVel(it, it), 1), terrain[it]!!)) }
    val minimumCumHeatLost = mutableMapOf<CartState, Int>()
    var bestTotalHeatLost = Int.MAX_VALUE
    val endPosition = IntVec2D(lines.last().lastIndex, lines.lastIndex)

    while (heads.isNotEmpty()) {
        val head = heads.poll()
        for ((nextCartState, stepsTaken) in head.state.nextStates(minStreak, maxStreak)) {
            terrain[nextCartState.posVel.position] ?: continue
            val newCumHeatLost =
                head.cumHeatLost + (0..<stepsTaken).sumOf { terrain[nextCartState.posVel.advance(-it).position]!! }
            if (
                newCumHeatLost >= bestTotalHeatLost ||
                nextCartState in minimumCumHeatLost &&
                newCumHeatLost >= minimumCumHeatLost.getValue(nextCartState)
            ) {
                continue
            }
            if (nextCartState.posVel.position == endPosition) {
                bestTotalHeatLost = newCumHeatLost
                continue
            }

            heads.offer(CartPath(nextCartState, newCumHeatLost))
            for (coveredStreak in nextCartState.streak..maxStreak) {
                val coveredCartState = nextCartState.copy(streak = coveredStreak)
                minimumCumHeatLost[coveredCartState] =
                    min(minimumCumHeatLost[coveredCartState] ?: Int.MAX_VALUE, newCumHeatLost)
            }
        }
    }
    return bestTotalHeatLost
}

private fun CartState.nextStates(minStraight: Int, maxStraight: Int) = buildList {
    add(CartState(posVel.turnLeft().advance(minStraight), minStraight) to minStraight)
    add(CartState(posVel.turnRight().advance(minStraight), minStraight) to minStraight)
    if (streak < maxStraight) add(CartState(posVel.advance(), streak + 1) to 1)
}

class y2023d17p1 : AdventOfCode({
    testFile("t01.txt", 102)
}, { lines ->
    solution(lines, minStreak = 1, maxStreak = 3)
})

class y2023d17p2 : AdventOfCode({
    testFile("t01.txt", 94)
    testFile("t02.txt", 71)
}, { lines ->
    solution(lines, minStreak = 4, 10)
})
