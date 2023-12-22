package y2023.d21

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.get
import util.neighbors
import util.positionOf

private fun part1(terrain: List<String>, maxSteps: Int): Int {
    val startingLocation = terrain.positionOf('S')!!
    val scannedPositions = mutableMapOf(startingLocation to 0)
    val searchPositions = ArrayDeque(listOf(startingLocation to 0))
    while (searchPositions.isNotEmpty()) {
        val (position, numSteps) = searchPositions.removeFirst()
        if (numSteps + 1 > maxSteps) break
        val neighbors = position.neighbors
        for (neighbor in neighbors) {
            if (neighbor in scannedPositions) continue
            if (terrain[neighbor] != '.') continue
            scannedPositions[neighbor] = numSteps + 1
            searchPositions.add(neighbor to numSteps + 1)
        }
    }
    return scannedPositions.count { it.value % 2 == 0 }
}

// private fun part2(terrain: List<String>, maxSteps: Long): Int {
//    val startingLocation = terrain.positionOf('S')!!
//
// }

class y2023d21p1 : AdventOfCode({
    test("sample") {
        part1(readTestFileLines("t01.txt"), 6) shouldBe 16
    }
}, {
    part1(it, 64)
})

// class y2023d21p2: AdventOfCode({
//    test("sample") {
//        part1(readTestFileLines("t01.txt"), 6) shouldBe 16
//    }
// }, {
//    part2(it, 26501365)
// })
