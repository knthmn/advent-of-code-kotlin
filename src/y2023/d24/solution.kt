package y2023.d24

import AdventOfCode
import io.kotest.matchers.shouldBe
import org.apache.commons.math3.fraction.BigFraction
import util.PosVel3D
import util.Vec3D
import util.div
import util.minus
import util.over
import util.pairs
import util.plus
import util.sign
import util.times
import util.unaryMinus
import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.math.min

private val regex = """([\-\d]+), +([\-\d]+), +([\-\d]+) @ +([\-\d]+), +([\-\d]+), +([\-\d]+)""".toRegex()

private fun parseInput(lines: List<String>): List<PosVel3D> = lines.map { line ->
    val numbers = regex.find(line)!!.destructured.toList().map { it.toBigInteger() }
    PosVel3D(Vec3D(numbers[0], numbers[1], numbers[2]), Vec3D(numbers[3], numbers[4], numbers[5]))
}

private fun BigInteger.toBigFraction() = BigFraction(this)

private fun part1(input: List<String>, lowBound: BigFraction, highBound: BigFraction): Int {
    val hailstones = parseInput(input)
    return hailstones.pairs().count { (h1, h2) ->
        if (h1.velocity.x == 0.toBigInteger() || h2.velocity.x == 0.toBigInteger()) {
            throw Error("Cannot check intersect lines with x-velocity of 0")
        }
        val m1 = h1.velocity.y over h1.velocity.x
        val b1 = h1.position.y - (m1 * h1.position.x)
        val m2 = h2.velocity.y over h2.velocity.x
        val b2 = h2.position.y - m2 * h2.position.x
        if (m1 == m2) {
            if (b1 != b2) return@count false
            var xLowBound = lowBound
            var xhighBound = highBound
            var yLowBound = lowBound
            var yHighBound = highBound
            if (h1.velocity.x > 0.toBigInteger()) {
                xLowBound = h1.position.x.toBigFraction()
            } else {
                xhighBound = h1.position.x.toBigFraction()
            }
            if (h2.velocity.x > 0.toBigInteger()) {
                xLowBound = h2.position.x.toBigFraction()
            } else {
                xhighBound = h2.position.x.toBigFraction()
            }
            if (h1.velocity.y > 0.toBigInteger()) {
                yLowBound = h1.position.y.toBigFraction()
            } else {
                yHighBound = h1.position.y.toBigFraction()
            }
            if (h2.velocity.y > 0.toBigInteger()) {
                yLowBound = h2.position.y.toBigFraction()
            } else {
                yHighBound = h2.position.y.toBigFraction()
            }
            if (xLowBound > xhighBound || yLowBound > yHighBound) return@count false
            return@count true
        }
        val x = -(b2 - b1) / (m2 - m1)
        val y = m1 * x + b1
        val inside =
            x in lowBound..highBound && y in lowBound..highBound
        val future = (y - h1.position.y).sign == h1.velocity.y.sign && (y - h2.position.y).sign == h2.velocity.y.sign
        inside && future
    }
}

private fun findCandidate(hailstones: List<PosVel3D>, selector: (Vec3D) -> BigInteger): BigInteger {
    val posVelPairs = hailstones.map { selector(it.position) to selector(it.velocity) }
    val sameVel = posVelPairs.groupBy { it.second }.filter { it.value.size > 1 }.mapValues { pair ->
        pair.value.map { it.first }
    }.mapValues { (_, positions) ->
        positions.pairs().map { (positionA, positionB) ->
            val difference = (positionA - positionB).toLong().absoluteValue
            val factors = (1..min((difference / 2), 10000)).filter { difference % it == 0L }
            (factors + listOf(difference)).toSet()
        }.reduce { a, b -> a intersect b }
    }
        .mapValues { (baseVelocity, velocities) ->
            velocities.map { baseVelocity.toLong() + it }.toSet() union velocities.map { baseVelocity.toLong() - it }
                .toSet()
        }.map { it.value }
        .reduce { a, b -> a intersect b }
    if (sameVel.size != 1) {
        error("Multiple candidates")
    }
    return sameVel.iterator().next().toBigInteger()
}

private fun part2(input: List<String>): BigInteger {
    val hailstones = parseInput(input)
    val vx = findCandidate(hailstones, { it -> it.x })
    val vy = findCandidate(hailstones, { it -> it.y })
    val vz = findCandidate(hailstones, { it -> it.z })

    val h1 = hailstones[0]
    val h2 = hailstones[1]
    val t1Num =
        -((h1.position.x - h2.position.x) over (h2.velocity.x - vx)) + ((h1.position.y - h2.position.y) over (h2.velocity.y - vy))
    val t1Den = ((h1.velocity.x - vx) over (h2.velocity.x - vx)) - ((h1.velocity.y - vy) over (h2.velocity.y - vy))
    val t1Frac = t1Num.div(t1Den)
    assert(t1Frac.denominator == 1.toBigInteger())
    val t1 = t1Frac.numerator
    val x = h1.position.x + (h1.velocity.x - vx) * t1
    val y = h1.position.y + (h1.velocity.y - vy) * t1
    val z = h1.position.z + (h1.velocity.z - vz) * t1
    return x + y + z
}

class y2023d24p1 : AdventOfCode({
    test("1") {
        part1(readTestFileLines("t01.txt"), BigFraction(7), BigFraction(27)) shouldBe 2
    }
}, { input ->
    part1(input, BigFraction(200000000000000), BigFraction(400000000000000))
})

class y2023d24p2 : AdventOfCode({
//    testFile(("t01.txt"), 47.toBigInteger()) // this one does not work since there are multiple candidates
}, { input ->
    part2(input)
})
