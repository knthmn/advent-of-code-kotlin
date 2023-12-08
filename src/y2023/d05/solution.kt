package y2023.d05

import AdventOfCode
import split
import util.intersect

private data class MappingEntry(val range: LongRange, val shift: Long)

private fun parseInput(lines: List<String>): Pair<List<Long>, List<List<MappingEntry>>> {
    val seeds = lines[0].substring(7).split(" ").map { it.toLong() }
    val mappings = lines.drop(2).split("").map { section ->
        section.drop(1).map { line ->
            val (destStart, sourceStart, length) = line.split(" ").map { it.toLong() }
            MappingEntry(sourceStart..<sourceStart + length, destStart - sourceStart)
        }.sortedBy { it.range.first }
    }
    return seeds to mappings
}

class y2023d05p1 : AdventOfCode({
    testFile("t01.txt", 35)
}, { lines ->
    val (seeds, mappings) = parseInput(lines)
    seeds.minOf { seed ->
        mappings.fold(seed) { category, mapping ->
            category + (mapping.firstOrNull { category in it.range }?.shift ?: 0)
        }
    }
})

class y2023d05p2 : AdventOfCode({
    testFile("t01.txt", 46)
}, { lines ->
    val (rawSeeds, mappings) = parseInput(lines)
    val seeds = rawSeeds.chunked(2).map { it[0]..<it[0] + it[1] }

    val result = mappings.fold(seeds) { categories, mapping ->
        categories.flatMap { categoryRange ->
            val mappableRanges =
                mapping.map { it.copy(range = it.range intersect categoryRange) }.filter { it.range.isEmpty().not() }
            val mappedRanges = mappableRanges.map { it.range.first + it.shift..it.range.last + it.shift }
            val nonMappedRanges = buildList {
                add(categoryRange.first)
                mappableRanges.forEach {
                    add(it.range.first - 1)
                    add(it.range.last + 1)
                }
                add(categoryRange.last)
            }.chunked(2).map { it[0]..it[1] }.filter { !it.isEmpty() }
            mappedRanges + nonMappedRanges
        }
    }
    result.minOf { it.first }
})
