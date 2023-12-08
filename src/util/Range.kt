package util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.math.max
import kotlin.math.min

class SparseLongSet private constructor(val ranges: List<LongRange>) : Iterable<Long> {
    companion object {
        fun from(rawRanges: Collection<LongRange>): SparseLongSet {
            val sortedRanges = rawRanges.filter { !it.isEmpty() }.sortedBy { it.first }
            val ranges = buildList<LongRange> {
                for (range in sortedRanges) {
                    if (size == 0 || range.first > last().last + 1) {
                        add(range)
                        continue
                    }
                    val lastRange = removeLast()
                    val newRange = min(range.first, lastRange.first)..max(range.last, lastRange.last)
                    add(newRange)
                }
            }
            return SparseLongSet(ranges)
        }

        fun from(vararg rawRanges: LongRange) = from(rawRanges.toList())
    }

    override fun iterator(): Iterator<Long> = iterator {
        ranges.forEach { yieldAll(it) }
    }

    override fun equals(other: Any?): Boolean = other is SparseLongSet && this.ranges == other.ranges

    override fun hashCode(): Int = ranges.hashCode()

    override fun toString(): String {
        return ranges.joinToString(
            prefix = "[",
            postfix = "]",
        ) { if (it.first == it.last) "${it.first}" else "${it.first}..${it.last}" }
    }
}

infix fun LongRange.intersect(other: LongRange): LongRange =
    LongRange(max(this.first, other.first), min(this.last, other.last))

class RangeTest : FunSpec({
    test("rangeIntersect") {
        LongRange(1, 2).intersect(LongRange(3, 4)).isEmpty() shouldBe true
        LongRange(1, 3).intersect(LongRange(3, 4)) shouldBe LongRange(3, 3)
        LongRange(1, 4).intersect(LongRange(2, 3)) shouldBe LongRange(2, 3)
    }
    test("SparseLongRange") {
        SparseLongSet.from(1L..2L, 3L..4L) shouldBe SparseLongSet.from(1L..4L)
        SparseLongSet.from(1L..4L, 2L..3L) shouldBe SparseLongSet.from(1L..4L)
        SparseLongSet.from(1L..2L, 4L..5L).ranges shouldBe listOf(1L..2L, 4L..5L)
        SparseLongSet.from(1L..2L, 4L..5L, 1L..6L) shouldBe SparseLongSet.from(1L..6L)
        SparseLongSet.from(1L..2L, 4L..5L, 3L..3L) shouldBe SparseLongSet.from(1L..5L)
    }
})
