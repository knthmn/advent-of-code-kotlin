package y2023.d18

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.IntVec2D
import util.IntVec2D.Companion.ORIGIN
import util.down
import util.left
import util.right
import util.search
import util.times
import util.up
import kotlin.math.max
import kotlin.math.min

data class Instruction(val direction: IntVec2D, val steps: Int)

private infix fun Int.dualDirectionTo(that: Int) = min(this, that)..max(this, that)

fun parseInstruction1(line: String): Instruction {
    val (directionStr, stepsStr) = """([UDLR]) (\d+) \(#[0-9a-f]{6}\)""".toRegex().matchEntire(line)!!.destructured
    val direction = when (directionStr) {
        "U" -> ORIGIN.up
        "D" -> ORIGIN.down
        "L" -> ORIGIN.left
        "R" -> ORIGIN.right
        else -> error("")
    }
    return Instruction(direction, stepsStr.toInt())
}

fun parseInstruction2(line: String): Instruction {
    val code = """\(#([0-9a-f]{6})\)""".toRegex().find(line)!!.groupValues[1]
    val direction = when (code.last()) {
        '0' -> ORIGIN.right
        '1' -> ORIGIN.down
        '2' -> ORIGIN.left
        '3' -> ORIGIN.up
        else -> error("")
    }
    return Instruction(direction, code.dropLast(1).toInt(16))
}

class y2023d18p1 : AdventOfCode({
    testFile("t01.txt", 62)
}, { lines ->
    val instructions = lines.map(::parseInstruction1)
    var position = ORIGIN
    val dugPositions = mutableSetOf<IntVec2D>()
    for (instruction in instructions) {
        repeat(instruction.steps) {
            position += instruction.direction
            dugPositions.add(position)
        }
    }
    position shouldBe ORIGIN
    val topPoint = dugPositions.asSequence().filter { it.down !in dugPositions }.minBy { it.y }
    search(topPoint.down, predicate = { it !in dugPositions }).forEach { dugPositions.add(it) }
    dugPositions.size
})

class y2023d18p2 : AdventOfCode({
    test("parsing") {
        parseInstruction2("(#70c710)") shouldBe Instruction(ORIGIN.right, 461937)
    }
    testFile("t01.txt", 952408144115)
}, { lines ->
    val instructions = lines.map(::parseInstruction2)
    var position = ORIGIN
    val corners = mutableSetOf(ORIGIN)
    for (instruction in instructions) {
        position += instruction.steps * instruction.direction
        corners.add(position)
    }
//    position shouldBe ORIGIN
    val compressionX =
        corners.asSequence().map { it.x }.toSet().sorted().withIndex().associate { it.value to it.index * 2 }
    val compressionY =
        corners.asSequence().map { it.y }.toSet().sorted().withIndex().associate { it.value to it.index * 2 }
    val expansionX = compressionX.map { it }.associate { it.value to it.key }
    val expansionY = compressionY.map { it }.associate { it.value to it.key }
    fun compress(vec: IntVec2D) = IntVec2D(compressionX.getValue(vec.x), compressionY.getValue(vec.y))
    fun expand(vec: IntVec2D) = IntVec2D(expansionX.getValue(vec.x), expansionY.getValue(vec.y))

    var compressedPosition = compress(ORIGIN)
    val compressedBoundary = mutableSetOf(compressedPosition)
    for (instruction in instructions) {
        val targetCompressedPosition = compress(expand(compressedPosition) + instruction.steps * instruction.direction)
        val compressedEdge = if (instruction.direction.x != 0) {
            (compressedPosition.x dualDirectionTo targetCompressedPosition.x).map { IntVec2D(it, compressedPosition.y) }
        } else {
            (compressedPosition.y dualDirectionTo targetCompressedPosition.y).map { IntVec2D(compressedPosition.x, it) }
        }
        compressedBoundary.addAll(compressedEdge)
        compressedPosition = targetCompressedPosition
    }
//    compressedPosition shouldBe compress(ORIGIN)
    val topCompressedPoint = compressedBoundary.filter { it.down !in compressedBoundary }.minBy { it.y }
    val compressedInterior = search(topCompressedPoint.down, predicate = { it !in compressedBoundary }).toSet()

    val interiorArea = compressedInterior.sumOf { compressedSpace ->
        val (x, y) = compressedSpace
        val width = if (x % 2 == 0) 1 else expansionX.getValue(x + 1) - expansionX.getValue(x - 1) - 1
        val height = if (y % 2 == 0) 1 else expansionY.getValue(y + 1) - expansionY.getValue(y - 1) - 1
        width.toLong() * height.toLong()
    }
    val boundaryArea = instructions.sumOf { it.steps.toLong() }
    interiorArea + boundaryArea
})
