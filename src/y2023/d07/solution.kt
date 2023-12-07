package y2023.d07

import AdventOfCode

private fun getTypeRank1(hand: String) = hand.groupingBy { it }.eachCount().values

private fun getTypeRank2(hand: String) = hand.filter { it != 'J' }.groupingBy { it }.eachCount().values.sorted().let { counts ->
    counts.dropLast(1) + ((counts.lastOrNull() ?: 0) + hand.count { it == 'J' })
}

private fun solution(lines: List<String>, getType: (String) -> Collection<Int>, cardOrder: String) = lines.map { line ->
    val (cards, bet) = line.split(" ")
    val value = cards.map { cardOrder.indexOf(it) }.reversed()
        // technically we only need cardValue * 13 ^ index, but there is no integer pow function
        .mapIndexed { index, cardValue -> cardValue shl (4 * index) }.sum()
    Pair(getType(cards).sumOf { 1L shl (3 * it + 20) } + value, bet.toInt())
}.sortedBy { it.first }.mapIndexed { idx, play ->
    (idx + 1) * play.second
}.sum()

class y2023d07p1 : AdventOfCode({
    testFile("t01.txt", 6440)
}, { lines ->
    solution(lines, ::getTypeRank1, "23456789TJQKA")
})

class y2023d07p2 : AdventOfCode({
    testFile("t01.txt", 5905)
}, { lines ->
    solution(lines, ::getTypeRank2, "J123456789TQKA")
})
