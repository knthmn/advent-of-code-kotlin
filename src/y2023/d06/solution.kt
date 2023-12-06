package y2023.d06

import AdventOfCode

class y2023d06p1 : AdventOfCode(
    {
        testFile("t01.txt", 288)
    },
    { lines ->
        val parsedLines = lines.map { line ->
            Regex("\\d+").findAll(line).map { it.value }
        }
        val races = parsedLines[0].zip(parsedLines[1]).map {
            it.first.toInt() to it.second.toInt()
        }.toList()
        races.fold(1) { acc, race ->
            val (time, distance) = race
            acc * (1..time).count { chargeTime ->
                chargeTime * (time - chargeTime) > distance
            }
        }
    },
)

class y2023d06p2 : AdventOfCode(
    {
        testFile("t01.txt", 71503)
    },
    { lines ->
        val (time, distance) = lines.map { it.replace("\\D".toRegex(), "") }.map { it.toLong() }
        // It's not a lot of values to check anyway :D
        (1..time).count { chargeTime ->
            chargeTime * (time - chargeTime) > distance
        }
    },
)
