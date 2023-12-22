package y2023.d20

import AdventOfCode
import util.lcm
import kotlin.math.pow

private data class Signal(val from: String, val to: String, val isHigh: Boolean)

private sealed class Module {
    abstract val name: String
    abstract fun serializeState(): String
    abstract val numPossibleStates: Int
    abstract val destinations: List<String>

    data class FlipFlopModule(override val name: String, override val destinations: List<String>) : Module() {
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

    data class ConjunctionModule(
        override val name: String,
        override val destinations: List<String>,
        val inputs: List<String>,
    ) :
        Module() {
        val memory = inputs.associateWith { false }.toMutableMap()
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

    repeat(1000) {
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

class y2023d20p2 : AdventOfCode(
    {
    },
    solution@{ lines ->
        val (broadcasterDestinations, modules) = parseInput(lines)
        val signalsToProcess = ArrayDeque<Signal>(10)
        var numTimesPressed = 0

        val conjunctionModuleName = modules.filterValues { value ->
            "rx" in value.destinations
        }.also { assert(it.size == 1) }.keys.toList()[0]
        val sectorFirstHighPressesNeeded: MutableMap<String, Int?> =
            modules.filterValues { value -> conjunctionModuleName in value.destinations }
                .keys.associateWith { null }.toMutableMap()

        outer@ while (true) {
            numTimesPressed += 1
            signalsToProcess.addAll(broadcasterDestinations.map { Signal("broadcaster", it, false) })
            while (signalsToProcess.isNotEmpty()) {
                val signal = signalsToProcess.removeFirst()
                val module = modules[signal.to] ?: continue
                val newSignals = module.process(signal)
                if (module is Module.ConjunctionModule && signal.to == conjunctionModuleName) {
                    for ((sector, sectorValue) in module.memory) {
                        if (sectorValue && sectorFirstHighPressesNeeded[sector] == null) {
                            sectorFirstHighPressesNeeded[sector] = numTimesPressed
                        }
                    }
                }
                signalsToProcess.addAll(newSignals)
            }
            if (sectorFirstHighPressesNeeded.values.all { it !== null }) {
                return@solution sectorFirstHighPressesNeeded.values.fold(1L) { a, b -> lcm(a, b!!.toLong()) }
            }
        }
    },
)
