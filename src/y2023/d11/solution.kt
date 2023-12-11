package y2023.d11

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.IntVec2D
import util.pairs
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private fun solution(input: List<String>, expansionFactor: Int): Long {
    val stars = buildSet {
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') add(IntVec2D(x, y))
            }
        }
    }
    val colsWithStars = stars.map { it.x }.toSet()
    val rowsWithStars = stars.map { it.y }.toSet()
    return stars.pairs().sumOf { pair ->
        val (star1, star2) = pair
        val dist = abs(star1.x - star2.x) + abs(star1.y - star2.y)
        val numEmptyCols = (min(star1.x, star2.x)..max(star1.x, star2.x)).count { it !in colsWithStars }
        val numEmptyRows = (min(star1.y, star2.y)..max(star1.y, star2.y)).count { it !in rowsWithStars }
        dist.toLong() + numEmptyRows * (expansionFactor - 1) + numEmptyCols * (expansionFactor - 1)
    }
}

class y2023d11p1 : AdventOfCode({
    testFile("t01.txt", 374)
}, { lines ->
    solution(lines, 2)
})

class y2023d11p2 : AdventOfCode({
    test("10x") { solution(readTestFileLines("t01.txt"), 10) shouldBe 1030 }
    test("100x") { solution(readTestFileLines("t01.txt"), 100) shouldBe 8410 }
}, { lines ->
    solution(lines, 1000000)
})
