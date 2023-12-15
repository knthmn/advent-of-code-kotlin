import io.kotest.matchers.shouldBe

private fun hash(string: String) = string.fold(0) { acc, c -> (17 * (acc + c.code)) % 256 }

class y2023d15p1 : AdventOfCode({
    test("hash algo") {
        hash("HASH") shouldBe 52
    }
    testString("test 1", "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7", 1320)
}, { lines ->
    lines.joinToString("").split(",").sumOf { hash(it) }
})

class y2023d15p2 : AdventOfCode({
    testString("test 1", "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7", 145)
}, { lines ->
    val boxes = List(256) { mutableSetOf<String>() to mutableMapOf<String, Int>() }
    for (instruction in lines.joinToString("").split(",")) {
        val (label, op, focusLength) = """(\w+)([=\-])(\d?)""".toRegex().matchEntire(instruction)?.destructured ?: error(
            "cannot match $instruction",
        )
        val (set, map) = boxes[hash(label)]
        when (op) {
            "=" -> {
                if (label !in set) set.add(label)
                map[label] = focusLength.toInt()
            }
            "-" -> set.remove(label)
            else -> error("")
        }
    }
    boxes.withIndex().sumOf { (boxIndex, box) ->
        val (set, map) = box
        set.withIndex().sumOf { (slotIndex, label) -> (boxIndex + 1) * (slotIndex + 1) * map.getValue(label) }
    }
})
