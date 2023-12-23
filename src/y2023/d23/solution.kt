package y2023.d23

import AdventOfCode
import util.IntVec2D
import util.down
import util.get
import util.left
import util.neighbors
import util.right
import util.up

class y2023d23p1 : AdventOfCode({
    testFile("t01.txt", 94)
}, { terrain ->

    fun findLongestPath(currentLocation: IntVec2D, path: MutableSet<IntVec2D>): Int {
        if (currentLocation == IntVec2D(terrain[0].lastIndex - 1, terrain.lastIndex)) {
            return path.size
        }
        path.add(currentLocation)
        val possibleNextLocations = when (terrain[currentLocation]) {
            '^' -> listOf(currentLocation.up)
            '>' -> listOf(currentLocation.right)
            'v' -> listOf(currentLocation.down)
            '<' -> listOf(currentLocation.left)
            else -> currentLocation.neighbors
        }
            .filter { it !in path && terrain[it] != null && terrain[it] != '#' }
        val longestPath = possibleNextLocations.maxOfOrNull { findLongestPath(it, path) } ?: 0
        path.remove(currentLocation)
        return longestPath
    }

    findLongestPath(IntVec2D(1, 0), mutableSetOf())
})

class y2023d23p2 : AdventOfCode({
    testFile("t01.txt", 154)
}, { terrain ->
    val endLocation = IntVec2D(terrain.last().lastIndex - 1, terrain.lastIndex)

    val cache = mutableMapOf<IntVec2D, Map<IntVec2D, Int>>()
    fun findAdjacentNodes(startLocation: IntVec2D): Map<IntVec2D, Int> = cache.getOrPut(startLocation) {
        val adjacentNodes = mutableMapOf<IntVec2D, Int>()
        val searchedLocations = mutableSetOf<IntVec2D>()
        val toSearch = ArrayDeque(listOf(startLocation to 0))
        while (toSearch.isNotEmpty()) {
            val (location, distance) = toSearch.removeFirst()
            val validNeighbors = location.neighbors.filter { (terrain[it] ?: '#') != '#' }
            if (location == endLocation || validNeighbors.size > 2 && location != startLocation) {
                adjacentNodes[location] = distance
                continue
            }
            validNeighbors.forEach { neighbor ->
                if (searchedLocations.add(neighbor)) toSearch.add(neighbor to distance + 1)
            }
        }
        adjacentNodes
    }

    fun findLongestPath(currentNode: IntVec2D, visitedNodes: MutableSet<IntVec2D>, distance: Int): Int {
        if (currentNode == IntVec2D(terrain[0].lastIndex - 1, terrain.lastIndex)) {
            return distance
        }
        visitedNodes.add(currentNode)
        val possibleNextLocations = findAdjacentNodes(currentNode)
            .filter { it.key !in visitedNodes }
        val longestPath =
            possibleNextLocations.maxOfOrNull { findLongestPath(it.key, visitedNodes, distance + it.value) } ?: 0
        visitedNodes.remove(currentNode)
        return longestPath
    }
    findLongestPath(IntVec2D(1, 0), mutableSetOf(), 0)
})
