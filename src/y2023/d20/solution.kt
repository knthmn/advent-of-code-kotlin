package y2023.d20

import AdventOfCode
import java.math.BigInteger
import kotlin.math.pow

private data class Signal(val from: String, val to: String, val isHigh: Boolean)

private sealed class Module() {
    abstract val name: String
    abstract fun serializeState(): String
    abstract val numPossibleStates: Int

    data class FlipFlopModule(override val name: String, val destinations: List<String>) : Module() {
        private var state = false
        override val numPossibleStates: Int
            get() = 2

        override fun process(signal: Signal): List<Signal> {
            if (signal.isHigh) return listOf()
            state = !state
            return destinations.map { Signal(name, it, state) }
        }

        override fun serializeState(): String = state.toString()
    }

    data class ConjunctionModule(override val name: String, val destinations: List<String>, val inputs: List<String>) :
        Module() {
        private val memory = inputs.associateWith { false }.toMutableMap()
        override val numPossibleStates: Int
            get() = 2.toDouble().pow(memory.size).toInt().coerceAtLeast(1)

        override fun process(signal: Signal): List<Signal> {
            memory[signal.from] = signal.isHigh
            return destinations.map { destination -> Signal(name, destination, memory.values.all { it }.not()) }
        }

        override fun serializeState(): String = memory.entries.sortedBy { it.key }.map { it.value }.joinToString(",")
    }

    abstract fun process(signal: Signal): List<Signal>
}

private fun parseInput(lines: List<String>): Pair<List<String>, Map<String, Module>> {
    val broadcasterDestinations =
        lines.find { it.startsWith("broadcaster -> ") }!!.drop("broadcaster -> ".length).split(", ")
    val parsePattern = """([%&])(\w+) -> ([\w, ]+)""".toRegex()
    val parsedModuleLines = lines.mapNotNull {
        val (type, name, destination) = parsePattern.matchEntire(it)?.destructured ?: return@mapNotNull null
        return@mapNotNull Triple(type, name, destination.split(", "))
    }
    val inputs = mutableMapOf<String, MutableList<String>>()
    for ((_, name, destinations) in parsedModuleLines) {
        destinations.forEach { inputs.getOrPut(it) { mutableListOf() }.add(name) }
    }
    broadcasterDestinations.forEach { inputs.getOrPut(it) { mutableListOf() }.add("broadcaster") }
//    println("$inputs")
    val modules = parsedModuleLines.map { (type, name, destination) ->
        if (type == "%") {
            Module.FlipFlopModule(name, destination)
        } else {
            Module.ConjunctionModule(
                name,
                destination,
                inputs.getValue(name),
            )
        }
    }.associateBy { it.name }
    return broadcasterDestinations to modules
}

class y2023d20p1 : AdventOfCode({
    testFile("t01.txt", 32000000)
    testFile("t02.txt", 11687500)
}, { lines ->
    val (broadcasterDestinations, modules) = parseInput(lines)
    var numLowSignalProcessed = 0
    var numHighSignalProcessed = 0
    val signalsToProcess = ArrayDeque<Signal>(10)

    repeat(1000) { n ->
        numLowSignalProcessed += 1
        signalsToProcess.addAll(broadcasterDestinations.map { Signal("broadcaster", it, false) })
        while (signalsToProcess.isNotEmpty()) {
            val signal = signalsToProcess.removeFirst()
            if (signal.isHigh) {
                numHighSignalProcessed += 1
            } else {
                numLowSignalProcessed += 1
            }
            val module = modules[signal.to] ?: continue
            val newSignals = module.process(signal)
            signalsToProcess.addAll(newSignals)
        }
    }
    numLowSignalProcessed * numHighSignalProcessed
})

// answer is more than 500,000 (or 100,000)
// manually checked up to 383,940,000
// possible state is 2^86

class y2023d20p2 : AdventOfCode({
//    testFile("t01.txt", 32000000)
//    testFile("t02.txt", 11687500)
}, { lines ->
    val (broadcasterDestinations, modules) = parseInput(lines)
    val signalsToProcess = ArrayDeque<Signal>(10)
    var numTimesPressed = 0

    println(modules.values.map { it.numPossibleStates })
    println(modules.values.map { it.numPossibleStates }.fold(BigInteger("1")) { acc, m -> acc * m.toBigInteger() })

//    val serializedStates = mutableMapOf<Map<String, String>, Int>()

//    outer@ while (true) {
//        numTimesPressed += 1
//        if (numTimesPressed % 10000 == 0) println("numPressed = ${numTimesPressed}")
//        signalsToProcess.addAll(broadcasterDestinations.map { Signal("broadcaster", it, false) })
//        while (signalsToProcess.isNotEmpty()) {
//            val signal = signalsToProcess.removeFirst()
//            if (signal.to == "rx" && !signal.isHigh) break@outer
// //            if (signal.to == "rx") {
// //                println("getting $signal")
// //            }
//            val module = modules[signal.to] ?: continue
//            val newSignals = module.process(signal)
//            signalsToProcess.addAll(newSignals)
//        }
//
// //        val serializedModules = modules.mapValues { it.value.serializeState() }
// //        if (serializedModules in serializedStates) {
// //            error("$serializedModules is found in ${serializedStates[serializedModules]}")
// //        }
// //        serializedStates[serializedModules] = numTimesPressed
//    }
    numTimesPressed
})
