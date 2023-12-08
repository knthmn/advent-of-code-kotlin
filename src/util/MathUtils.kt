package util

fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

fun gcd(a: Long, b: Long): Long {
    return if (b == 0L) a else gcd(b, a % b)
}
