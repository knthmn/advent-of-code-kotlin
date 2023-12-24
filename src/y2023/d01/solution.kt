package y2023.d01

import AdventOfCode

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
        numberMap.getValue(line.findAnyOf(numberMap.keys)!!.second) * 10 +
                numberMap.getValue(line.findLastAnyOf(numberMap.keys)!!.second)
    }
})

private val numberMap = listOf(
    "one",
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
).withIndex().associate { it.value to it.index + 1 } + (0..9).associateBy { it.toString() }
