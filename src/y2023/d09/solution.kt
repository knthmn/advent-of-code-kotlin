class y2023d09p1 : AdventOfCode({
    testString("1", "0   3   6   9  12  15", 18)
    testString("2", "1   3   6  10  15  21", 28)
    testString("3", "10  13  16  21  30  45", 68)
}, { lines ->
    lines.sumOf { line ->
        val sequences = mutableListOf(line.split("\\s+".toRegex()).map { it.toInt() })
        while (sequences.last().any { it != 0 }) {
            sequences.add(sequences.last().zipWithNext { a, b -> b - a })
        }
        sequences.sumOf { it.last() }
    }
})

class y2023d09p2 : AdventOfCode({
    testString("3", "10  13  16  21  30  45", 5)
}, { lines ->
    lines.sumOf { line ->
        val sequences = mutableListOf(line.split("\\s+".toRegex()).map { it.toInt() }.reversed())
        while (sequences.last().any { it != 0 }) {
            sequences.add(sequences.last().zipWithNext { a, b -> b - a })
        }
        sequences.sumOf { it.last() }
    }
})
