package y2023.d25


import AdventOfCode
import org.jgrapht.alg.flow.EdmondsKarpMFImpl
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import util.pairs

private fun parseInput(lines: List<String>): MutableMap<String, MutableSet<String>> {
    val links = mutableMapOf<String, MutableSet<String>>()
    for (line in lines) {
        val (src, dsts) = line.split(": ")
        for (dst in dsts.split(" ")) {
            links.getOrPut(src) { mutableSetOf() }.add(dst)
            links.getOrPut(dst) { mutableSetOf() }.add(src)
        }
    }
    return links
}

class y2023d25p1 : AdventOfCode(
    {
        testFile("t01.txt", 54)
    },
    main@{ input ->
        val links = parseInput(input)
        val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
        links.keys.forEach { vertex -> graph.addVertex(vertex) }
        links.forEach { src, dsts ->
            dsts.forEach { dst -> graph.addEdge(src, dst) }
        }
        val algo = EdmondsKarpMFImpl(graph)
        graph.vertexSet().pairs().forEach { (a, b) ->
            val minCut = algo.calculateMinCut(a, b)
            if (minCut.toInt() == 3) {
                return@main algo.sinkPartition.size * algo.sourcePartition.size
            }
        }
        error("No cut fount")
    },
)
