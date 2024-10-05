package y2021.d22

import AdventOfCode
import io.kotest.matchers.shouldBe
import util.IntVec3D
import java.util.PriorityQueue
import kotlin.math.max
import kotlin.math.min


private data class Instruction(val on: Boolean, val start: IntVec3D, val stop: IntVec3D)

private fun parse(lines: List<String>): List<Instruction> = lines.map { line ->
    val on = line.subSequence(0, 2) == "on"
    val integers = Regex("""-?\d+""").findAll(line).map { it.value.toInt() }.toList()
    Instruction(on, IntVec3D(integers[0], integers[2], integers[4]), IntVec3D(integers[1], integers[3], integers[5]))
}

private fun part1(instructions: List<Instruction>): Int {
    val lit = mutableSetOf<IntVec3D>()
    fun IntRange.coerce() = IntRange(max(-50, start), min(50, endInclusive))
    for ((on, start, stop) in instructions) {
        // val xRange =
        for (x in (start.x..stop.x).coerce()) {
            for (y in (start.y..stop.y).coerce()) {
                for (z in (start.z..stop.z).coerce()) {
                    val point = IntVec3D(x, y, z)
                    if (on) lit.add(point) else lit.remove(point)
                }
            }
        }
    }
    return lit.size
}

private data class Region(val start: IntVec3D, val end: IntVec3D)

private val Region.size get() = (end.x - start.x + 1).toLong() * (end.y - start.y + 1) * (end.z - start.z + 1)

private fun part2(instructions: List<Instruction>): Long {
    val wholeRegionStart = IntVec3D(
        instructions.map { it.start.x }.min(),
        instructions.map { it.start.y }.min(),
        instructions.map { it.start.z }.min(),
    )
    val wholeRegionEnd = IntVec3D(
        instructions.map { it.stop.x }.max(),
        instructions.map { it.stop.y }.max(),
        instructions.map { it.stop.z }.max(),
    )
    val wholeRegion = Region(wholeRegionStart, wholeRegionEnd)
    val regionsToCheck = PriorityQueue<Region>(1, compareBy { it.size })
    regionsToCheck.add(wholeRegion)
    val offRegion = mutableListOf<Region>()
    val onRegion = mutableListOf<Region>()

    fun checkRegion(region: Region) {
        val instruction = instructions.lastOrNull { instruction ->
            instruction.start.x <= region.end.x &&
                    instruction.start.y <= region.end.y &&
                    instruction.start.z <= region.end.z &&
                    instruction.stop.x >= region.start.x &&
                    instruction.stop.y >= region.start.y &&
                    instruction.stop.z >= region.start.z
        }
        if (instruction == null) {
            offRegion.add(region)
            return
        }
        var currentRegion = region
        if (instruction.start.x > currentRegion.start.x) {
            val cutRegion = currentRegion.copy(end = currentRegion.end.copy(x = instruction.start.x - 1))
            currentRegion = currentRegion.copy(start = currentRegion.start.copy(x = instruction.start.x))
            regionsToCheck.offer(cutRegion)
        }
        if (instruction.start.y > currentRegion.start.y) {
            val cutRegion = currentRegion.copy(end = currentRegion.end.copy(y = instruction.start.y - 1))
            currentRegion = currentRegion.copy(start = currentRegion.start.copy(y = instruction.start.y))
            regionsToCheck.offer(cutRegion)
        }
        if (instruction.start.z > currentRegion.start.z) {
            val cutRegion = currentRegion.copy(end = currentRegion.end.copy(z = instruction.start.z - 1))
            currentRegion = currentRegion.copy(start = currentRegion.start.copy(z = instruction.start.z))
            regionsToCheck.offer(cutRegion)
        }
        if (instruction.stop.x < currentRegion.end.x) {
            val cutRegion = currentRegion.copy(start = currentRegion.start.copy(x = instruction.stop.x + 1))
            currentRegion = currentRegion.copy(end = currentRegion.end.copy(x = instruction.stop.x))
            regionsToCheck.offer(cutRegion)
        }
        if (instruction.stop.y < currentRegion.end.y) {
            val cutRegion = currentRegion.copy(start = currentRegion.start.copy(y = instruction.stop.y + 1))
            currentRegion = currentRegion.copy(end = currentRegion.end.copy(y = instruction.stop.y))
            regionsToCheck.offer(cutRegion)
        }
        if (instruction.stop.z < currentRegion.end.z) {
            val cutRegion = currentRegion.copy(start = currentRegion.start.copy(z = instruction.stop.z + 1))
            currentRegion = currentRegion.copy(end = currentRegion.end.copy(z = instruction.stop.z))
            regionsToCheck.offer(cutRegion)
        }
        if (instruction.on) {
            onRegion.add(currentRegion)
        } else {
            offRegion.add(currentRegion)
        }
    }
    var checked = 0
    while (regionsToCheck.isNotEmpty()) {
        val region = regionsToCheck.poll()
        checkRegion(region)
        checked += 1
    }
    return onRegion.sumOf { it.size }
}

class y2021d22p1 : AdventOfCode({
    testFile("t01.txt", 39)
    testFile("t02.txt", 590784)
    submission = true
}, { input ->
    val instructions = parse(input)
    part1(instructions)
})

class y2021d22p2 : AdventOfCode({
    testFile("t03.txt", 2758514936282235)
    testFile("t01.txt", 39)
    test("t02.txt") {
        part2(parse(readTestFileLines("t02.txt")).dropLast((2))) shouldBe 590784
    }
    submission = true
}, { input ->
    val instructions = parse(input)
    part2(instructions)
})



