package y2023.d07

import AdventOfCode

val types = listOf(
    mapOf(1 to 5),
    mapOf(2 to 1, 1 to 3),
    mapOf(2 to 2, 1 to 1),
    mapOf(3 to 1, 1 to 2),
    mapOf(3 to 1, 2 to 1),
    mapOf(4 to 1, 1 to 1),
    mapOf(5 to 1),
)

private fun getTypeRank1(hand: String) =
    hand.groupingBy { it }.eachCount().values.groupingBy { it }.eachCount().let { types.indexOf(it) }

private fun getTypeRank2(hand: String) = hand.filter { it != 'J' }.groupingBy { it }.eachCount().values.sorted().let {
    it.dropLast(1) + ((it.lastOrNull() ?: 0) + hand.count { it == 'J' })
}.groupingBy { it }.eachCount().let { types.indexOf(it) }

private fun compareStringWithOrder(order: String) = Comparator<String> { s1, s2 ->
    s1.zip(s2).firstOrNull { (a, b) -> a != b }?.let { (a, b) ->
        order.indexOf(a) - order.indexOf(b)
    } ?: 0
}

private fun solution(lines: List<String>, comparator: Comparator<String>) = lines.map { line ->
    val (cards, bet) = line.split(" ")
    cards to bet.toInt()
}.sortedWith { a, b -> comparator.compare(a.first, b.first) }.mapIndexed { idx, play ->
    (idx + 1) * play.second
}.sum()

class y2023d07p1 : AdventOfCode({
    testFile("t01.txt", 6440)
}, { lines ->
    solution(lines, compareBy<String> { getTypeRank1(it) }.then(compareStringWithOrder("23456789TJQKA")))
})

class y2023d07p2 : AdventOfCode({
    testFile("t01.txt", 5905)
}, { lines ->
    solution(lines, compareBy<String> { getTypeRank2(it) }.then(compareStringWithOrder("J123456789TQKA")))
})
