package y2023.d13

import AdventOfCode
import split

private fun findReflectionValue1(grid: List<String>): Int {
    outer@ for (reflectionRow in 0..<grid.lastIndex) {
        var aboveRow = reflectionRow
        var belowRow = reflectionRow + 1
        while (true) {
            if (grid[aboveRow] != grid[belowRow]) continue@outer
            aboveRow -= 1
            belowRow += 1
            if (aboveRow < 0 || belowRow >= grid.size) break
        }
        return (reflectionRow + 1) * 100
    }
    val width = grid[0].length
    outer@ for (reflectionColumn in 0..<width - 1) {
        var leftColumn = reflectionColumn
        var rightColumn = reflectionColumn + 1
        while (true) {
            if (grid.map { it[leftColumn] } != grid.map { it[rightColumn] }) continue@outer
            leftColumn -= 1
            rightColumn += 1
            if (leftColumn < 0 || rightColumn >= width) break
        }
        return reflectionColumn + 1
    }
    error("reflection not found")
}

private fun findReflectionValue2(grid: List<String>): Int {
    outer@ for (reflectionRow in 0..<grid.lastIndex) {
        var diffFound = false
        var aboveRow = reflectionRow
        var belowRow = reflectionRow + 1
        while (true) {
            val numDiffs = (grid[aboveRow] zip grid[belowRow]).count { it.first != it.second }
            if (numDiffs > 1 || numDiffs == 1 && diffFound) continue@outer
            if (numDiffs == 1) diffFound = true
            aboveRow -= 1
            belowRow += 1
            if (aboveRow < 0 || belowRow >= grid.size) break
        }
        if (diffFound) return (reflectionRow + 1) * 100
    }
    val width = grid[0].length
    outer@ for (reflectionColumn in 0..<width - 1) {
        var diffFound = false
        var leftColumn = reflectionColumn
        var rightColumn = reflectionColumn + 1
        while (true) {
            val numDiffs =
                (grid.map { it[leftColumn] } zip grid.map { it[rightColumn] }).count { it.first != it.second }
            if (numDiffs > 1 || numDiffs == 1 && diffFound) continue@outer
            if (numDiffs == 1) diffFound = true
            leftColumn -= 1
            rightColumn += 1
            if (leftColumn < 0 || rightColumn >= width) break
        }
        if (diffFound) return reflectionColumn + 1
    }
    error("reflection not found")
}

class y2023d13p1 : AdventOfCode({
    testFile("t01.txt", 5)
    testFile("t02.txt", 400)
}, { lines ->
    lines.split("").sumOf(::findReflectionValue1)
})

class y2023d13p2 : AdventOfCode({
    testFile("t01.txt", 300)
    testFile("t02.txt", 100)
}, { lines ->
    lines.split("").sumOf(::findReflectionValue2)
})
