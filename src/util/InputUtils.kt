package util

fun List<String>.positionOf(char: Char): IntVec2D? {
    for ((y, line) in this.withIndex()) {
        for ((x, c) in line.withIndex()) {
            if (c == char) return IntVec2D(x, y)
        }
    }
    return null
}

operator fun List<String>.get(position: IntVec2D): Char? {
    val line = getOrNull(position.y) ?: return null
    return line.getOrNull(position.x)
}
