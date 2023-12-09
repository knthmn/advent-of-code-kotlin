package y2023.d03

import AdventOfCode
import util.IntVec2D
import util.left
import util.mooreNeighbors
import util.right

private data class Number(
    val value: Int,
    val startPosition: IntVec2D,
) {
    val positions = generateSequence(startPosition) { it.right }.take(value.toString().length)
}

private fun Map<IntVec2D, Char>.getNumber(position: IntVec2D): Number? {
    if (this[position]?.isDigit() != true) return null
    val startPos =
        generateSequence(position) { it.left }.takeWhile { this[it]?.isDigit() ?: false }.last()
    val positions = generateSequence(startPos) { it.right }.takeWhile { this[it]?.isDigit() ?: false }
    val value = positions.joinToString("") { getValue(it).toString() }.toInt()
    return Number(value, startPos)
}

private fun parseSchematic(lines: List<String>) = buildMap<IntVec2D, Char> {
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c != '.') put(IntVec2D(x, y), c)
        }
    }
}

private fun findAllNumbers(schematic: Map<IntVec2D, Char>): Set<Number> {
    val scannedNumberPositions = mutableSetOf<IntVec2D>()
    val numbers = mutableSetOf<Number>()
    for ((position) in schematic) {
        if (position in scannedNumberPositions) continue
        val number = schematic.getNumber(position) ?: continue
        scannedNumberPositions.addAll(number.positions)
        numbers.add(number)
    }
    return numbers
}

class y2023d03p1 : AdventOfCode({
    testFile("t01.txt", 4361)
}, { lines ->
    val schematic = parseSchematic(lines)
    val numbers = findAllNumbers(schematic)
    val partNumbers = numbers.filter { number ->
        number.positions.any { position ->
            position.mooreNeighbors.any {
                schematic[it]?.isDigit()?.not() ?: false
            }
        }
    }
    partNumbers.sumOf { it.value }
})

class y2023d03p2 : AdventOfCode({
    testFile("t01.txt", 467835)
}, { lines ->
    val schematic = parseSchematic(lines)
    schematic.entries.asSequence().filter { it.value == '*' }
        .map { gearPosition ->
            gearPosition.key.mooreNeighbors.mapNotNull { schematic.getNumber(it) }.toSet()
        }
        .filter { it.size == 2 }
        .sumOf { numbers -> numbers.fold(1L) { acc, number -> acc * number.value } }
})
