package y2023.d05

import AdventOfCode
import split
import util.SparseLongSet
import util.intersect
import util.quickMap
import util.rangeDifference

private data class MappingEntry(val range: LongRange, val shift: Long)

private data class Mapping(val entries: List<MappingEntry>) {
    fun map(category: Long): Long = category + (entries.firstOrNull { category in it.range }?.shift ?: 0)

    fun map(categoryRange: LongRange): List<LongRange> {
        val mappedRanges = mutableListOf<LongRange>()
        var nonMappedRanges = listOf(categoryRange)
        for (entry in entries) {
            nonMappedRanges = nonMappedRanges.flatMap { range ->
                val intersect = range intersect entry.range
                val difference = range rangeDifference entry.range
                if (!intersect.isEmpty()) {
                    mappedRanges.add(
                        LongRange(
                            intersect.first + entry.shift,
                            intersect.last + entry.shift,
                        ),
                    )
                }
                difference
            }
        }
        return mappedRanges + nonMappedRanges
    }
}

private fun parseInput(lines: List<String>): Pair<List<Long>, List<Mapping>> {
    val seeds = lines[0].substring(7).split(" ").map { it.toLong() }
    val mappings = lines.drop(2).split("").map { section ->
        val entries = section
            .drop(1)
            .map { line ->
                val (destStart, sourceStart, length) = line.split(" ").map { it.toLong() }
                MappingEntry(sourceStart..<sourceStart + length, destStart - sourceStart)
            }
        Mapping(entries)
    }
    return seeds to mappings
}

class y2023d05p1 : AdventOfCode({
    testFile("t01.txt", 35)
}, { lines ->
    val (seeds, mappings) = parseInput(lines)
    seeds.minOf { seed ->
        mappings.fold(seed) { category, mapping -> mapping.map(category) }
    }
})

class y2023d05p2 : AdventOfCode({
    testFile("t01.txt", 46)
}, { lines ->
    val (rawSeeds, mappings) = parseInput(lines)
    val seeds = SparseLongSet.from(rawSeeds.windowed(2, step = 2).map { it[0]..<it[0] + it[1] })
    val result = mappings.fold(seeds) { categories, mapping ->
        categories.quickMap {
            mapping.map(it)
        }
    }
    result.ranges[0].first
})
