package y2023.d06

import AdventOfCode
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

fun getNumWays(time: Long, distance: Long): Int {
    val time = time.toDouble()
    val distance = distance.toDouble()
    val discriminant = time.pow(2) - 4 * distance
    // this check is technically correct despite not required for the test case and submission
    if (discriminant <= 0) return 0
    val lower = (time - sqrt(discriminant)) / 2
    val upper = (time + sqrt(discriminant)) / 2
    return (if (upper % 1 == 0.0) upper - 1 else floor(upper)).toInt() - (if (lower % 1 == 0.0) lower + 1 else ceil(lower)).toInt() + 1
}

class y2023d06p1 : AdventOfCode(
    {
        testFile("t01.txt", 288)
    },
    { lines ->
        val parsedLines = lines.map { line ->
            Regex("\\d+").findAll(line).map { it.value.toLong() }
        }
        val races = parsedLines[0].zip(parsedLines[1]).map {
            it.first to it.second
        }.toList()
        races.fold(1) { acc, race ->
            val (time, distance) = race
            acc * getNumWays(time, distance)
        }
    },
)

class y2023d06p2 : AdventOfCode(
    {
        testFile("t01.txt", 71503)
    },
    { lines ->
        val (time, distance) = lines.map { it.replace("\\D".toRegex(), "") }.map { it.toLong() }
        getNumWays(time, distance)
    },
)
