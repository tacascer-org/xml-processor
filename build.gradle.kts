plugins {
    `java-library`
    id("com.adarshr.test-logger") version "4.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
    id("org.sonarqube") version "5.0.0.4638"
    kotlin("jvm") version "1.9.23"
}

group = "io.github.tacascer"
version = "0.1-SNAPSHOT" // x-release-please-version

repositories {
    mavenCentral()
}

val kotestVersion = "5.8.1"

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
    jvmToolchain(17)
}

tasks.sonar {
    dependsOn(tasks.named("koverXmlReport"))
}

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "tacascer-org_xml-processor")
        property("sonar.organization", "tacascer-org")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.asFile.get()}/reports/kover/report.xml"
        )
    }
}