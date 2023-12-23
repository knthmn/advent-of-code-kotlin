package y2023.d22

import AdventOfCode
import pop
import util.IntVec3D

private fun parseInput(input: List<String>): List<List<IntVec3D>> = input.map { line ->
    val (startBlock, endBlock) = line.split("~").map { blockStr ->
        val (x, y, z) = blockStr.split(",").map { it.toInt() }
        IntVec3D(x, y, z)
    }
    when {
        startBlock.x != endBlock.x -> (startBlock.x..endBlock.x).map { startBlock.copy(x = it) }
        startBlock.y != endBlock.y -> (startBlock.y..endBlock.y).map { startBlock.copy(y = it) }
        startBlock.z != endBlock.z -> (startBlock.z..endBlock.z).map { startBlock.copy(z = it) }
        else -> listOf(startBlock)
    }
}

private fun getSettledState(input: List<String>): Pair<MutableMap<Char, List<IntVec3D>>, MutableMap<IntVec3D, Char>> {
    val bricks = parseInput(input).withIndex().associate { ('A'.code + it.index).toChar() to it.value }.toMutableMap()
    val allBlocks = bricks.flatMap { (key, value) -> value.map { it to key } }.toMap().toMutableMap()

    val bricksToCheck = bricks.keys.toMutableSet()
    while (bricksToCheck.isNotEmpty()) {
        val brickNumber = bricksToCheck.pop()
        val oldBlocks = bricks.getValue(brickNumber)
        val newBlocks = generateSequence(oldBlocks) { blocks -> blocks.map { it - IntVec3D(0, 0, 1) } }
            .drop(1)
            .takeWhile { blocks -> blocks.all { (it !in allBlocks || allBlocks[it] == brickNumber) && it.z > 0 } }
            .lastOrNull()
        if (newBlocks == null) continue
        oldBlocks.forEach { allBlocks.remove(it) }
        newBlocks.forEach { allBlocks[it] = brickNumber }
        bricks[brickNumber] = newBlocks
        oldBlocks.forEach { block ->
            val spaceAbove = block + IntVec3D(0, 0, 1)
            allBlocks[spaceAbove]?.let { bricksToCheck.add(it) }
        }
    }
    return bricks to allBlocks
}

class y2023d22p1 : AdventOfCode({
    testFile("t01.txt", 5)
}, { input ->
    val (bricks, allBlocks) = getSettledState(input)

    val mandatoryBlocks = bricks.entries
        .mapNotNull { (brickNumber, blocks) ->
            val supports = blocks.mapNotNull { allBlocks[it - IntVec3D(0, 0, 1)] }.filter { it != brickNumber }.toSet()
            if (supports.size == 1) supports.first() else null
        }.toSet()
    bricks.size - mandatoryBlocks.size
})

class y2023d22p2 : AdventOfCode({
    testFile("t01.txt", 7)
}, { input ->
    val (bricks, allBlocks) = getSettledState(input)
    val dependencies = bricks.mapValues { (brickNumber, blocks) ->
        blocks.mapNotNull { allBlocks[it - IntVec3D(0, 0, 1)] }.filter { it != brickNumber }.toSet()
    }
    val dependents = bricks.mapValues { (brickNumber, blocks) ->
        blocks.mapNotNull { allBlocks[it - IntVec3D(0, 0, -1)] }.filter { it != brickNumber }.toSet()
    }

    bricks.entries.sumOf { (brickNumber, _) ->
        val bricksToFall = mutableSetOf(brickNumber)
        val bricksCausedToFall = mutableSetOf<Char>()
        val availableDependencies = dependencies.mapValues { it.value.toMutableSet() }
        while (bricksToFall.isNotEmpty()) {
            val brick = bricksToFall.pop()
            dependents.getValue(brick).forEach { dependent ->
                val removed = availableDependencies.getValue(dependent).remove(brick)
                if (removed && availableDependencies.getValue(dependent).isEmpty()) {
                    bricksToFall.add(dependent)
                    bricksCausedToFall.add(dependent)
                }
            }
        }
        bricksCausedToFall.size
    }
})
