import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.awt.HeadlessException
import java.io.*
import java.nio.file.*
import java.util.concurrent.*

var commitHash: String by extra
commitHash = run {
	val process: Process = Runtime.getRuntime().exec("git rev-parse --short HEAD")
	process.waitFor()
	val output = process.inputStream.use {
		it.bufferedReader().use(BufferedReader::readText)
	}
	process.destroy()
	output.trim()
}

var isCI: Boolean by extra
isCI = !System.getenv("CI").isNullOrBlank()
var kotlinStable: String by extra
kotlinStable = "1.2.31"
val kotlinEAP = "1.2.40-eap-62"
var kotlinVersion: String by extra
kotlinVersion = if (isCI) kotlinEAP else kotlinStable

plugins {
	base
	kotlin("jvm") version "1.2.31" apply false
}

allprojects {
	val shortVersion = "v1.3-SNAPSHOT"
	val packageName = "org.ice1000.devkt"

	group = packageName
	version = if (isCI) "$shortVersion-$commitHash" else shortVersion

	repositories {
		mavenCentral()
		jcenter()
		maven("https://jitpack.io")
		maven("https://dl.bintray.com/kotlin/kotlin-dev")
	}

	apply {
		plugin("java")
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			jvmTarget = "1.8"
		}
	}

	tasks.withType<JavaCompile> {
		sourceCompatibility = "1.8"
		targetCompatibility = "1.8"
		options.apply {
			isDeprecation = true
			isWarnings = true
			isDebug = !isCI
			compilerArgs.add("-Xlint:unchecked")
		}
	}
}

