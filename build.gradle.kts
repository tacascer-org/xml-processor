plugins {
    `java-library`
    id("com.adarshr.test-logger") version "4.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
    id("org.sonarqube") version "5.0.0.4638"
    id("org.jetbrains.dokka") version "1.9.20"
    kotlin("jvm") version "1.9.23"
}

group = "io.github.tacascer"
version = "0.1.0" // x-release-please-version

repositories {
    mavenCentral()
}

val kotestVersion = "5.8.1"
val jdomVersion = "2.0.6.1"
val jetbrainsAnnotationVersion = "24.1.0"
val jaxenVersion = "1.2.0"
val slf4jSimpleVersion = "2.0.3"
val kotlinLoggingVersion = "5.1.0"

dependencies {
    compileOnly("org.jetbrains:annotations:$jetbrainsAnnotationVersion")
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("org.jdom:jdom2:$jdomVersion")
    runtimeOnly("jaxen:jaxen:$jaxenVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation(kotlin("test"))
    testRuntimeOnly("org.slf4j:slf4j-simple:$slf4jSimpleVersion")
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
