import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.formUrlEncode
import io.ktor.http.parametersOf
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists
import kotlin.io.path.outputStream
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.math.ceil
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

typealias Implementation = suspend (lines: List<String>) -> Any

open class AdventOfCode(
    configuration: (AdventOfCode.() -> Unit),
    private val implementation: Implementation,
) : FunSpec() {
    var submission: Boolean = true
    private val problemDirectory = Path(this::class.qualifiedName.orEmpty().split(".").dropLast(1).joinToString("/"))

    init {
        lateinit var input: String
        val (year, day, partNumber) = Regex("""y(\d{4})d(\d{2})p(\d)""").matchEntire(this::class.simpleName.orEmpty())?.groupValues?.drop(
            1,
        )?.map { it.toInt() } ?: error("Invalid class name")
        val answerCache = AnswerCache(year, day, partNumber)

        beforeSpec {
            input = getUserInput(year, day)
        }

        configuration()
        extensions(BlockSubmission(submission))

        // The tests are naturally ordered so submission is always run last
        generateSubmissionTestCase(
            year,
            day,
            partNumber,
            { input },
            implementation,
            answerCache,
        )
    }

    fun testFile(
        fileName: String,
        answer: Any,
    ) = test("File: $fileName", readResourceFile(problemDirectory / fileName), answer)

    fun testString(
        name: String,
        input: String,
        answer: Any,
    ) = test("String: $name", input, answer)

    private fun test(
        name: String,
        input: String,
        answer: Any,
    ) = test(name) {
        println("Input:\n${quoteStyle(input)}")
        val (output, duration) = measureTimedValue { implementation(input.split("\n")) }
        output shouldBe answer
        println("Execution time $duration. Output:\n${quoteStyle(output.toString())}")
    }
}

private class BlockSubmission(private val submission: Boolean) : TestCaseExtension {
    private var failed = false

    override suspend fun intercept(
        testCase: TestCase,
        execute: suspend (TestCase) -> TestResult,
    ): TestResult {
        if (testCase.name.testName == ("Submission")) {
            if (!submission) {
                return TestResult.Ignored("Manually skipped")
            }
            if (failed) {
                return TestResult.Ignored("There were errors in test cases.")
            }
        }
        return execute(testCase).also { failed = failed || it.isFailure }
    }
}

private val workingDir = Path("data")
private val inputDir = workingDir / "input"

private fun inputPathOf(
    year: Int,
    day: Int,
) = inputDir / "y${year}d${day.toString().padStart(2, '0')}.txt"

private val httpClient = run {
    val token = (workingDir / "session").readLines()[0]
    HttpClient {
        defaultRequest {
            header("Cookie", "session=$token")
            header("User-Agent", "https://github.com/knthmn/advent-of-code-kotlin by knthmn0@gmail.com")
        }
    }
}

private suspend fun getUserInput(
    year: Int,
    day: Int,
): String {
    if (!inputDir.isDirectory()) {
        inputDir.createDirectories()
    }
    val inputPath = inputPathOf(year, day)
    if (inputPath.notExists()) {
        println("Downloading input")
        val text = httpClient.get("https://adventofcode.com/$year/day/$day/input").bodyAsText()
        inputPath.writeText(text)
        return text
    }
    return inputPath.readText().trimEnd('\n')
}

context(FunSpec)
private fun generateSubmissionTestCase(
    year: Int,
    day: Int,
    partNumber: Int,
    getInput: suspend () -> String,
    implementation: Implementation,
    answerCache: AnswerCache,
) = test("Submission") {
    val input = getInput()
    println("Input ${inputPathOf(year, day).absolute()}")
    val (rawAnswer, duration) = measureTimedValue { implementation(input.split("\n")) }
    val answer = rawAnswer.toString()
    println("Execution time $duration. Answer:\n${quoteStyle(answer)}")
    val matchingEntry = answerCache.entries.find { it.answer == answer }
    if (matchingEntry != null) {
        when (matchingEntry.result) {
            SubmissionResult.Correct -> {
                println("The answer is correct and was already submitted.")
                return@test
            }

            is SubmissionResult.Incorrect -> {
                println("This result is wrong and was already submitted.")
                println(matchingEntry.result.message)
                throw AssertionError(matchingEntry.result.trimmedMessage)
            }
        }
    }
    val lastEntry = answerCache.entries.lastOrNull()
    if (lastEntry != null) {
        val timeDiff = Clock.System.now() - lastEntry.timestamp
        if (timeDiff < 1.minutes) {
            val waitTime = 1.minutes - timeDiff
            println("Waiting for ${ceil(waitTime.toDouble(DurationUnit.SECONDS))}s to submit")
            delay(waitTime)
        }
    }
    val result = submitAnswer(year, day, partNumber, answer)
    answerCache.entries += AnswerRecord(
        Clock.System.now(), answer, result,
    )
    if (result is SubmissionResult.Incorrect) {
        throw AssertionError(result.trimmedMessage)
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class AnswerCache(year: Int, day: Int, partNumber: Int) {
    private val json = Json {
        prettyPrint = true
    }
    private val path = workingDir / "answers" / "y${year}d${day.toString().padStart(2, '0')}p$partNumber.json"
    private var _entries = if (path.exists()) {
        json.decodeFromStream<List<AnswerRecord>>(path.inputStream())
    } else {
        listOf()
    }

    var entries: List<AnswerRecord>
        get() = _entries
        set(value) {
            _entries = value
            save()
        }

    private fun save() {
        (workingDir / "answers").createDirectories()
        json.encodeToStream(entries, path.outputStream())
    }
}

private suspend fun submitAnswer(
    year: Int,
    day: Int,
    partNumber: Int,
    answer: String,
): SubmissionResult {
    val text = httpClient.post("https://adventofcode.com/$year/day/$day/answer") {
        setBody(
            parametersOf("level" to listOf(partNumber.toString()), "answer" to listOf(answer)).formUrlEncode(),
        )
        header("Content-Type", "application/x-www-form-urlencoded")
    }.bodyAsText()
    val mainText = Regex(
        """<article><p>(.*)</p></article>""",
        setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL),
    ).find(text)?.groups?.get(1)?.value?.replace(
        Regex("""<.*>"""),
        "",
    ) ?: throw IllegalArgumentException("Failed to extract main text from body:\n$text")
    println(mainText)
    return when {
        mainText.contains("That's the right answer") -> {
            SubmissionResult.Correct
        }

        mainText.contains("That's not the right answer") -> {
            SubmissionResult.Incorrect(mainText)
        }

        else -> throw IllegalArgumentException("Failed to parse response")
    }
}

fun quoteStyle(string: String) = string.split("\n").joinToString("\n") { "> $it" }

@Serializable
private sealed class SubmissionResult {
    @Serializable
    data object Correct : SubmissionResult()

    @Serializable
    data class Incorrect(val message: String) : SubmissionResult() {
        val trimmedMessage = message.take(message.indexOf("If you're stuck")).trim()
    }
}

@Serializable
private data class AnswerRecord(val timestamp: Instant, val answer: String, val result: SubmissionResult)

private fun readResourceFile(path: Path) = (Path("src") / path).readText()
