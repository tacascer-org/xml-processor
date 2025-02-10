import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `java-library`
    id("com.adarshr.test-logger") version "4.0.0"
    id("com.vanniktech.maven.publish") version "0.30.0"
    id("dev.drewhamilton.poko") version "0.18.2"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("org.sonarqube") version "6.0.1.5171"
    kotlin("jvm") version "2.1.10"
}

group = "io.github.tacascer"
version = "0.7.4" // x-release-please-version

repositories {
    mavenCentral()
}

val kotestVersion = "5.9.1"
val jdomVersion = "2.0.6.1"
val jetbrainsAnnotationVersion = "26.0.2"
val jaxenVersion = "2.0.0"
val slf4jSimpleVersion = "2.0.16"
val kotlinLoggingVersion = "7.0.4"

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
            "${layout.buildDirectory.asFile.get()}/reports/kover/report.xml",
        )
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("$group", name, "$version")
    pom {
        name.set("XML Processor")
        description.set("A simple tool to flatten XML files by inlining included schemas.")
        url.set("https://github.com/tacascer-org/xml-processor")
        licenses {
            license {
                name = "GPL-3.0"
                url = "https://www.gnu.org/licenses/gpl-3.0.html"
            }
        }
        developers {
            developer {
                id = "tacascer"
                name = "Tim Tran"
                url = "https://github.com/tacascer"
            }
        }
        scm {
            url = "https://github.com/tacascer-org/xml-processor"
            connection = "scm:git:git://github.com/tacascer-org/xml-processor.git"
            developerConnection = "scm:git:ssh://git@github.com:tacascer-org/xml-processor.git"
        }
    }
}
