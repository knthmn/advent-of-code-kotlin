package y2023.d21

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.IntVec2D
import util.get
import util.mod
import util.neighbors
import util.positionOf

private fun search(terrain: List<String>, maxSteps: Int, tile: Boolean): Int {
    val startingLocation = terrain.positionOf('S')!!
    var numEvenTiles = 1
    var numOddTiles = 0

    var lastFrontier = listOf<IntVec2D>()
    var currentFrontier = listOf(startingLocation)
    var iNumStep = 1

    while (iNumStep <= maxSteps) {
        val nextFrontier = currentFrontier.asSequence()
            .flatMap {
                it.neighbors
            }
            .filter {
                val checkPosition = if (tile) it.mod(terrain.size) else it
                it !in lastFrontier &&
                    (terrain[checkPosition] == '.' || (tile && terrain[checkPosition] == 'S'))
            }.toSet()
        if (nextFrontier.isEmpty()) break
        if (iNumStep % 2 == 0) {
            numEvenTiles += nextFrontier.size
        } else {
            numOddTiles += nextFrontier.size
        }
        lastFrontier = currentFrontier
        currentFrontier = nextFrontier.toList()
        iNumStep += 1
    }
    if (maxSteps % 2 == 0) {
        return numEvenTiles
    } else {
        return numOddTiles
    }
}

private fun search2(terrain: List<String>, maxSteps: Int): Long {
    assert(terrain.size == terrain[0].length)
    val cycleSize = terrain.size * 2
    val remainder = maxSteps % cycleSize
    var iMaxSteps = remainder
    val results = mutableListOf<Int>()
    while (true) {
        results.add(search(terrain, iMaxSteps, tile = true))
        if (iMaxSteps == maxSteps) return results.last().toLong()
        val secondDerivatives = results.zipWithNext { a, b -> b - a }.zipWithNext { a, b -> b - a }
        if (secondDerivatives.size >= 2 && secondDerivatives.last() == secondDerivatives[secondDerivatives.lastIndex - 1]) {
            val cyclesLeft = (maxSteps - iMaxSteps) / cycleSize
            val currentFirstDerivative = results.last() - results[results.lastIndex - 1]
            return (cyclesLeft.toLong() * (cyclesLeft + 1)) / 2 * secondDerivatives.last() + currentFirstDerivative.toLong() * cyclesLeft + results.last()
        }
        iMaxSteps += cycleSize
    }
}

class y2023d21p1 : AdventOfCode({
    test("sample") {
        search(readTestFileLines("t01.txt"), 6, false) shouldBe 16
    }
}, {
    search(it, 64, false)
})

class y2023d21p2 : AdventOfCode({
    val tests = listOf(
        6 to 16,
        10 to 50,
        50 to 1594,
        100 to 6536,
        500 to 167004,
        1000 to 668697,
        5000 to 16733044,
    )
    for ((maxSteps, expected) in tests) {
        test("$maxSteps, $expected") {
            search2(readTestFileLines("t01.txt"), maxSteps) shouldBe expected
        }
    }
}, {
    search2(it, 26501365)
})
