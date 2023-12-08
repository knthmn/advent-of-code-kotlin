package y2023.d08

import AdventOfCode
import repeatInfinitely
import util.lcm

private fun parseInput(lines: List<String>): Pair<String, Map<String, Pair<String, String>>> {
    val instructions = lines[0]
    val nodes = lines.drop(2).associate { line ->
        val (origin, left, right) = """(\w{3}) = \((\w{3}), (\w{3})\)""".toRegex().matchEntire(line)!!.destructured
        origin to (left to right)
    }
    return instructions to nodes
}

private fun nextLocation(nodes: Map<String, Pair<String, String>>, location: String, instruction: Char) =
    when (instruction) {
        'L' -> nodes.getValue(location).first
        'R' -> nodes.getValue(location).second
        else -> error("Invalid instruction: $instruction")
    }

class y2023d08p1 : AdventOfCode({
    testFile("t01.txt", 2)
    testFile("t02.txt", 6)
}, { lines ->
    val (instructions, nodes) = parseInput(lines)
    var numSteps = 0
    var currentLocation = "AAA"
    for (instruction in instructions.asIterable().repeatInfinitely()) {
        if (currentLocation == "ZZZ") break
        numSteps += 1
        currentLocation = nextLocation(nodes, currentLocation, instruction)
    }
    numSteps
})

class y2023d08p2 : AdventOfCode({
    testFile("t03.txt", 6)
}, { lines ->
    val (instructions, nodes) = parseInput(lines)
    /*
    This solution does not work in the general case but worked for the test case and my input. Assumptions:
      - Each path takes N steps to arrive at the first "Z" ending location
      - Then each path has an N steps cycle
      - All paths will only simultaneous arrive at "Z" ending locations at the start their respective cycle
     */
    nodes.keys.filter { it.endsWith("A") }
        .map { startingLocation ->
            val instructionIterator = instructions.asIterable().repeatInfinitely().iterator()
            generateSequence(startingLocation) { location ->
                nextLocation(
                    nodes,
                    location,
                    instructionIterator.next(),
                )
            }.takeWhile { !it.endsWith("Z") }.count()
        }
        .fold(1L) { acc, num -> lcm(acc, num.toLong()) }
})
