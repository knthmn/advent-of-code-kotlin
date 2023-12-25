package y2023.d24

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.pairs
import java.math.BigDecimal

private val BigDecimal.sign
    get() = when {
        this > BigDecimal.ZERO -> 1
        this < BigDecimal.ZERO -> -1
        else -> 0
    }

private fun part1(lines: List<String>, minCoord: BigDecimal, maxCoord: BigDecimal): Int {
    val hailstones = lines.map { line ->
        line.replace(" @", ",").split(", ").map { it.trim().toBigDecimal() }
    }
    return hailstones.pairs().count { (hailstone1, hailStone2) ->
        val (x_01, y_01, _, v_x1, v_y1) = hailstone1
        val (x_02, y_02, _, v_x2, v_y2) = hailStone2
        if (v_y1 * v_x2 == v_y2 * v_x1) return@count false  // they are parallel
        val x_coll =
            v_x1 * (v_x2 * y_01 - v_x2 * y_02 - v_y2 * x_01 + v_y2 * x_02) / (v_x1 * v_y2 - v_x2 * v_y1) + x_01
        val y_coll =
            v_y1 * (v_x2 * y_01 - v_x2 * y_02 - v_y2 * x_01 + v_y2 * x_02) / (v_x1 * v_y2 - v_x2 * v_y1) + y_01
        // assuming the v_y are not zero (which was the case for the inputs)
        if ((y_coll - y_01).sign != v_y1.sign || (y_coll - y_02).sign != v_y2.sign) return@count false
        (x_coll in minCoord..maxCoord && y_coll in minCoord..maxCoord)
    }
}

class y2023d24p1 : AdventOfCode({
    test("example") {
        part1(readTestFileLines("t01.txt"), BigDecimal(7), BigDecimal(27)) shouldBe 2
    }
}, { lines ->
    part1(lines, BigDecimal("200000000000000"), BigDecimal("400000000000000"))
})