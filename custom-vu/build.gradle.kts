import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

val kotlinVersion = "1.2.70"
val seleniumVersion = "3.141.59"

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow").version("2.0.4")
}

dependencies {
    implementation("com.atlassian.performance.tools:virtual-users:[3.10.0,4.0.0)")
    implementation("com.atlassian.performance.tools:jira-actions:[3.11.0,4.0.0)")
    implementation("com.atlassian.performance.tools:jira-software-actions:[1.3.2,2.0.0)")
    implementation("org.seleniumhq.selenium:selenium-support:$seleniumVersion")
    implementation("org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion")
    implementation("org.glassfish:javax.json:1.1")
    implementation("com.google.code.gson:gson:2.8.0")
    implementation(kotlin("stdlib-jdk8"))

    testCompile("junit:junit:4.12")
    testCompile("org.assertj:assertj-core:3.11.1")
}

tasks.getByName("shadowJar", ShadowJar::class).apply {
    manifest.attributes["Main-Class"] = "com.atlassian.performance.tools.virtualusers.api.EntryPointKt"
}

configurations.all {
    resolutionStrategy {
        activateDependencyLocking()
        failOnVersionConflict()
        dependencySubstitution {
            substitute(module("org.apache.logging.log4j:log4j-slf4j-impl"))
                .with(module("org.apache.logging.log4j:log4j-slf4j18-impl:2.12.1"))
        }
        eachDependency {
            when (requested.module.toString()) {
                "commons-codec:commons-codec" -> useVersion("1.10")
                "com.google.code.gson:gson" -> useVersion("2.8.0")
                "com.google.guava:guava" -> useVersion("25.0-jre")
                "org.slf4j:slf4j-api" -> useVersion("1.8.0-alpha2")
            }
            when (requested.group) {
                "org.jetbrains.kotlin" -> useVersion(kotlinVersion)
                "org.apache.logging.log4j" -> useVersion("2.12.1")
                "org.seleniumhq.selenium" -> useVersion(seleniumVersion)
            }
        }
    }
}

repositories {
    mavenLocal()
    maven(url = URI("https://packages.atlassian.com/maven-external/"))
    mavenCentral()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
