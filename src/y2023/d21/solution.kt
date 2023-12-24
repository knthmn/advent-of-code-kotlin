package y2023.d21

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.get
import util.mod
import util.positionOf
import util.searchWithSteps

private fun part1(terrain: List<String>, maxSteps: Int): Int {
    val startingLocation = terrain.positionOf('S')!!
    return searchWithSteps(
        startingLocation,
        predicate = { pos, numSteps ->
            (terrain[pos] == '.' || terrain[pos] == 'S') && numSteps <= maxSteps
        }
    )
        .count { it.value % 2 == 0 }
}

private fun part2(terrain: List<String>, maxSteps: Long): Long {
    val startingLocation = terrain.positionOf('S')!!
    assert(terrain[0].length == terrain.size)
    val width = terrain.size
    val numPlots = mutableListOf<Int>()
//    val velPlots = mutableListOf<Int>()
//    val accPlots =
    val remainderMaxSteps = maxSteps % (2 * width)
//    val testNumSteps = mutableListOf<Int>()
    for (i in 0..6) {
        val testMaxSteps = remainderMaxSteps + i * 2 * width
        println("testing for $testMaxSteps")
        val num = searchWithSteps(
            startingLocation,
            predicate = { pos, numSteps ->
                (terrain[pos mod width] == '.' || terrain[pos mod width] == 'S') && numSteps <= testMaxSteps
            }
        )
            .count { it.value % 2 == 0 }
//        testNumSteps.add(testMaxSteps)
        numPlots.add(num)
    }
    val velPlots = numPlots.zipWithNext { a, b -> b - a }
    val accPlots = velPlots.zipWithNext { a, b -> b - a }
    assert(accPlots.takeLast(3).toSet().size == 1)

//    println(testNumSteps)
    println(numPlots)
    println(velPlots)
    println(accPlots)
    val z = numPlots[2]
    val y = velPlots[2]
    val x = accPlots[2]
    fun d(n: Long) = z + n * y + n * (n - 1) / 2 * x
    println(List(10) { ((it + 2) * 2 * width) + remainderMaxSteps to d(it.toLong()) })
//    println("$maxSteps, $width")
//    println(maxSteps / (2 * width) - 2)
    return d(maxSteps / (2 * width) - 2)
}

/***
 * z, z + y    , z + 2y + x, z + 3y + 3x, z + 3y + 6x
 * y, y + x    , y + 2x    , y + 3x     , y + 4x
 * x
 */

class y2023d21p1 : AdventOfCode({
    test("sample") {
        part1(readTestFileLines("t01.txt"), 6) shouldBe 16
    }
}, {
    part1(it, 64)
})

class y2023d21p2 : AdventOfCode({
    for ((numSteps, answer) in listOf(
        50 to 1594,
        100 to 6536,
        500 to 167004,
        1000 to 668697,
        5000 to 16733044
    )) {
        test("$numSteps steps") {
            part2(readTestFileLines("t01.txt"), numSteps.toLong()) shouldBe answer
        }
    }
//    test("sample") {
//        part2(readTestFileLines("t01.txt"), 1000) shouldBe 668697
//    }
//    submission = false
}, {
    part2(it, 26501365)
})
