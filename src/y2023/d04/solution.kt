package y2023.d04

import AdventOfCode

private fun getScore(line: String): Int {
    val (winningNumbersString, numbersString) = line.substringAfter(":").split("|")
    val winningNumbers = winningNumbersString.split(" ").filter(String::isNotEmpty).map(String::toInt).toSet()
    return numbersString.split(" ").filter(String::isNotEmpty).map(String::toInt).count { it in winningNumbers }
}

class y2023d04p1 : AdventOfCode({
    testFile("t01.txt", 13)
}, { lines ->
    lines.sumOf { line ->
        val score = getScore(line)
        // The following line is not good code but ¯\_(ツ)_/¯
        (2 shl (score - 2)).coerceAtLeast(score)
    }
})

class y2023d04p2 : AdventOfCode({
    testFile("t01.txt", 30)
}, { lines ->
    val scores = lines.map(::getScore)
    val numsCards = MutableList(scores.size) { 1 }
    for ((index, score) in scores.withIndex()) {
        for (bonusIndex in 1..score) {
            if (index + bonusIndex > numsCards.size) break
            numsCards[index + bonusIndex] += numsCards[index]
        }
    }
    numsCards.sum()
})
