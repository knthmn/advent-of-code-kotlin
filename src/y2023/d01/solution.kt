package y2023.d01

import AdventOfCode
import util.findAllOverlapped
import util.keysAsRegex

class y2023d01p1 : AdventOfCode({
    testFile("t01.txt", 142)
}, { lines ->
    lines.sumOf { line ->
        val digits = line.filter { it.isDigit() }
        "${line.first { it.isDigit() }}${digits.last { it.isDigit() }}".toInt()
    }
})

class y2023d01p2 : AdventOfCode({
    testFile("t02.txt", 281)
    testString("overlapping", "threeight", 38)
}, { lines ->
    lines.sumOf { line ->
        val digits = Regex("""(?:${numberMap.keysAsRegex()}|\d)""").findAllOverlapped(line).map { it.value }.map {
            numberMap.getOrDefault(it, it)
        }
        "${digits.first()}${digits.last()}".toInt()
    }
})

private val numberMap = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9",
)
