plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    application

    id("com.diffplug.spotless") version "6.23.2"
}

group = "aco2023"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.kotest:kotest-assertions-core-jvm:5.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.3.6")
    implementation("org.slf4j:slf4j-simple:2.0.9")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Prevent :build from running tests
    onlyIf { ":test" in gradle.startParameter.taskNames }
}

sourceSets {
    test {
        kotlin {
            setSrcDirs(listOf("src"))
        }
        resources {
            setSrcDirs(listOf("src"))
        }
    }
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}
tasks.register<Copy>("Copy Git Hooks") {
    from("scripts/pre-commit")
    into(".git/hooks")
}
tasks.getByName("build") {
    finalizedBy("Copy Git Hooks")
}
configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_standard_filename" to "disabled",
                "ktlint_standard_class-naming" to "disabled",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}
