package y2023.d12

import AdventOfCode
import repeat

private fun countPossibleArrangements(pattern: String, groups: List<Int>): Long {
    val trimmedPattern = pattern.replace("""\.+""".toRegex(), ".").trim('.')
    val cache = mutableMapOf<Pair<Int, Int>, Long>()

    fun countPossibleArrangements(index: Int, groupIndex: Int): Long {
        val cachedValue = cache[index to groupIndex]
        if (cachedValue !== null) return cachedValue
        if (groupIndex == groups.size) {
            return when {
                index >= trimmedPattern.length -> 1
                trimmedPattern.substring(index).all { it == '?' || it == '.' } -> 1
                else -> 0
            }
        }
        if (index >= trimmedPattern.length) return 0
        val char = trimmedPattern[index]
        val group = groups[groupIndex]
        if (char == '.') {
            return countPossibleArrangements(index + 1, groupIndex).also {
                cache[index to groupIndex] = it
            }
        }
        var numArrangements = if (char == '?') countPossibleArrangements(index + 1, groupIndex) else 0
        if (index + group > trimmedPattern.length) return 0
        val matched = trimmedPattern.substring(index, index + group).all { it == '?' || it == '#' }
        val spaced =
            index + group >= trimmedPattern.length || trimmedPattern[index + group] == '?' || trimmedPattern[index + group] == '.'
        if (matched && spaced) numArrangements += countPossibleArrangements(index + group + 1, groupIndex + 1)
        cache[index to groupIndex] = numArrangements
        return numArrangements
    }
    return countPossibleArrangements(0, 0)
}

class y2023d12p1 : AdventOfCode({
    testString("a", "???.### 1,1,3", 1)
    testString("b", ".??..??...?##. 1,1,3", 4)
    testString("c", "?#?#?#?#?#?#?#? 1,3,1,6", 1)
    testString("d", "????.#...#... 4,1,1", 1)
    testString("e", "????.######..#####. 1,6,5", 4)
    testString("f", "?###???????? 3,2,1", 10)
}, { lines ->
    lines.sumOf { line ->
        val (pattern, groups) = line.split(" ")
        countPossibleArrangements(pattern, groups.split(",").map { it.toInt() })
    }
})

class y2023d12p2 : AdventOfCode({
    testString("a", "???.### 1,1,3", 1)
    testString("b", ".??..??...?##. 1,1,3", 16384)
    testString("c", "?#?#?#?#?#?#?#? 1,3,1,6", 1)
    testString("d", "????.#...#... 4,1,1", 16)
    testString("e", "????.######..#####. 1,6,5", 2500)
    testString("f", "?###???????? 3,2,1", 506250)
}, { lines ->
    lines.sumOf { line ->
        val (pattern, groups) = line.split(" ")
        countPossibleArrangements(
            listOf(pattern).repeat(5).joinToString("?"),
            groups.split(",").map { it.toInt() }.repeat(5),
        )
    }
})
