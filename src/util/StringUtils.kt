package util

fun Regex.findAllOverlapped(
    input: String,
    startIndex: Int = 0,
) = """(?=($pattern)).""".toRegex().findAll(input, startIndex).map { it.groups[1]!!.value }.map { matchEntire(it)!! }

fun <T> Map<String, T>.keysAsRegex() = "(?:" + this.keys.joinToString("|") { Regex.escape(it) } + ")"
