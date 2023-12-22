package y2023.d19

import AdventOfCode
import split

private typealias Part = Map<Char, Int>

private data class Rule(val key: Char, val comparison: Char, val value: Int, val destination: String)

private data class Workflow(val rules: List<Rule>, val otherwise: String)

private fun parseInput(lines: List<String>): Pair<Map<String, Workflow>, List<Map<Char, Int>>> {
    val (workflowsLines, partLines) = lines.split("")
    val workflows = workflowsLines.associate { line ->
        val workflowKey = line.substringBefore('{')
        val ruleStrings = line.substringAfter('{').dropLast(1).split(',')
        val rules = ruleStrings.dropLast(1).map { conditionString ->
            val (key, condition, value, destination) = """(\w)([<>])(\d+):(\w+)""".toRegex()
                .matchEntire(conditionString)!!.destructured
            Rule(key[0], condition[0], value.toInt(), destination)
        }
        workflowKey to Workflow(rules, ruleStrings.last())
    }
    val parts = partLines.map { partLine ->
        partLine.drop(1).dropLast(1).split(",").associate {
            val (key, value) = it.split('=')
            key[0] to value.toInt()
        }
    }
    return workflows to parts
}

class y2023d19p1 : AdventOfCode({
    testFile("t01.txt", 19114)
}, { lines ->
    val (workflows, parts) = parseInput(lines)
    fun Workflow.findDestination(part: Part): String {
        for (rule in rules) {
            val value = part.getValue(rule.key)
            val condition = if (rule.comparison == '<') value < rule.value else value > rule.value
            if (condition) return rule.destination
        }
        return otherwise
    }
    parts.sumOf { part ->
        var workflowKey = "in"
        while (true) {
            workflowKey = workflows.getValue(workflowKey).findDestination(part)
            if (workflowKey == "A") return@sumOf part.values.sum()
            if (workflowKey == "R") break
        }
        0
    }
})

class y2023d19p2 : AdventOfCode({
    testFile("t01.txt", 167409079868000)
}, { lines ->
    val (workflows, _) = parseInput(lines)
    var numAccepted = 0L

    fun searchAccepted(workflowKey: String, partSet: Map<Char, IntRange>) {
        if (workflowKey == "R") {
            return
        }
        if (workflowKey == "A") {
            numAccepted += partSet.values.fold(1L) { acc, range -> if (range.isEmpty()) 0 else acc * (range.last - range.first + 1) }
            return
        }
        val workflow = workflows.getValue(workflowKey)
        var currentPartSet = partSet
        for (rule in workflow.rules) {
            val range = currentPartSet.getValue(rule.key)
            val matchedRange =
                if (rule.comparison == '<') range.first..(rule.value - 1) else (rule.value + 1)..range.last
            val filteredRange = if (rule.comparison == '<') rule.value..range.last else range.first..rule.value
            if (!matchedRange.isEmpty()) {
                val matchedPartSet = currentPartSet + (rule.key to matchedRange)
                searchAccepted(rule.destination, matchedPartSet)
            }
            currentPartSet = currentPartSet + (rule.key to filteredRange)
            if (filteredRange.isEmpty()) break
        }
        searchAccepted(workflow.otherwise, currentPartSet)
    }
    searchAccepted("in", "xmas".associate { it to 1..4000 })
    numAccepted
})
