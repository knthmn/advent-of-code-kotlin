package y2021.d24

import AdventOfCode
import org.chocosolver.solver.Model

fun findModelNumber(program: List<String>, maximize: Boolean): Long {
    val chunks = program.chunked(18)
    val params = chunks.map { line ->
        val a = line[4].split(" ")[2].toInt()
        val b = line[5].split(" ")[2].toInt()
        val c = line[15].split(" ")[2].toInt()
        Triple(a, b, c)
    }
    val sequence = params.mapIndexed { index, triple -> index to triple.first}.toMutableList()
    val pushToPullIndexes = buildMap {
        while (sequence.isNotEmpty()) {
            val index = (0..<sequence.size).first { sequence[it].second == 1 && sequence[it + 1].second == 26 }
            set(sequence[index].first, sequence[index + 1].first)
            sequence.removeAt(index + 1)
            sequence.removeAt(index)
        }
    }
    val model = Model()
    val inputs = List(14) { model.intVar("input_$it", 1, 9)}
    for ((pushIndex, pullIndex) in pushToPullIndexes) {
        model.arithm(inputs[pushIndex].add(params[pushIndex].third).add(params[pullIndex].second).intVar(), "=", inputs[pullIndex]).post()
    }
    val targets = inputs.mapIndexed { index, intVar -> index to intVar }.filter { it.first in pushToPullIndexes.keys }.map { it.second }
    val front = model.solver.findParetoFront(targets.toTypedArray(), maximize)
    val number = inputs.map { front.first().getIntVal(it) }.joinToString("").toLong()
    return number
}

class y2021d24p1 : AdventOfCode({}, { program ->
    findModelNumber(program, true)
})

class y2021d24p2 : AdventOfCode({}, { program ->
    findModelNumber(program, false)
})