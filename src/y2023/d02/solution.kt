package y2023.d02

import AdventOfCode

class y2023d02p1 : AdventOfCode({
    testFile("t01.txt", 8)
}, { lines ->
    lines.sumOf { line ->
        val gameId = Regex("Game (\\d+):").find(line)!!.groupValues[1].toInt()
        val (red, green, blue) = listOf("red", "green", "blue").map { color ->
            Regex(" (\\d+) $color").findAll(line).maxOf { it.groupValues[1].toInt() }
        }
        if (red <= 12 && green <= 13 && blue <= 14) gameId else 0
    }
})

class y2023d02p2 : AdventOfCode({
    testFile("t01.txt", 2286)
}, { lines ->
    lines.sumOf { line ->
        val (red, green, blue) = listOf("red", "green", "blue").map { color ->
            Regex(" (\\d+) $color").findAll(line).maxOf { it.groupValues[1].toInt() }
        }
        red * blue * green
    }
})
