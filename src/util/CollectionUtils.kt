fun <T> List<T>.split(delimeter: T): List<List<T>> = buildList {
    var buffer = mutableListOf<T>()
    for (element in this@split) {
        if (element == delimeter) {
            add(buffer.toList())
            buffer = mutableListOf()
        } else {
            buffer.add(element)
        }
    }
    if (buffer.isNotEmpty()) {
        add(buffer.toList())
    }
}

fun <T> Iterable<T>.repeatInfinitely() = sequence {
    while (true) yieldAll(this@repeatInfinitely)
}

fun <T> List<T>.repeat(n: Int) = buildList(size * n) {
    repeat(n) {
        addAll(this@repeat)
    }
}

fun <T> MutableSet<T>.pop(): T {
    val element = iterator().next()
    remove(element)
    return element
}
