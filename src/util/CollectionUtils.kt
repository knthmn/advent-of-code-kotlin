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
