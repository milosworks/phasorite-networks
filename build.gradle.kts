import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("java-library")
	id("idea")
	id("maven-publish")

	id("net.neoforged.moddev") version "2.0.32-beta"

	kotlin("jvm") version "2.0.0"
}

val modVersion: String by project
version = modVersion

val modId: String by project
base.archivesName = modId

val javaVersion = JavaVersion.VERSION_21
val neoVersion: String by project
//val minecraftVersion: String by project
val parchmentMappingsVersion: String by project
val majorMinecraftVersion: String by project

val kotlinNeoVersion: String by project
val owoVersion: String by project

repositories {
	mavenLocal()

	maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
	maven("https://thedarkcolour.github.io/KotlinForForge/") {
		name = "Kotlin for Forge"
		content {
			includeGroup("thedarkcolour")
		}
	}
	maven("https://maven.wispforest.io") { name = "owolib" }
	maven("https://maven.su5ed.dev/releases") { name = "Syntra Forgified Fabric Api" }
}

dependencies {
	implementation("thedarkcolour", "kotlinforforge-neoforge", kotlinNeoVersion)
	implementation("io.wispforest", "owo-lib-neoforge", "${owoVersion}+${majorMinecraftVersion}")
}

neoForge {
	version = neoVersion

	parchment {
		mappingsVersion = parchmentMappingsVersion
		minecraftVersion = majorMinecraftVersion
	}

	runs {
		configureEach {
			systemProperty("forge.logging.markers", "REGISTRIES")
			systemProperty("neoforge.enabledGameTestNamespaces", modId)
			logLevel = org.slf4j.event.Level.DEBUG
		}

		create("client") {
			client()

			programArguments.addAll("--username", "Vyrek_", "--quickPlaySingleplayer", "Test")
		}

		create("server") {
			server()

			programArgument("--nogui")
		}

		create("gameTestServer") {
			type = "gameTestServer"
		}

		create("data") {
			data()

			programArguments.addAll(
				"--mod",
				modId,
				"--all",
				"--output",
				file("src/generated/resources/").absolutePath,
				"--existing",
				file("src/main/resources/").absolutePath
			)
		}
	}

	mods {
		create(modId) {
			sourceSet(sourceSets["main"])
		}
	}
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.toString()
		targetCompatibility = javaVersion.toString()
		options.release.set(javaVersion.toString().toInt())
	}

	withType<KotlinCompile> {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}

	jar {
		from("LICENSE")
	}

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
		}

		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion

		withSourcesJar()
	}

	processResources {
		inputs.property("mod_version", version)

		filesMatching("META-INF/neoforge.mods.toml") {
			expand(
				mutableMapOf(
					"loader_version_range" to project.properties["loaderVersionRange"],
					"mod_license" to project.properties["modLicense"],
					"mod_id" to project.properties["modId"],
					"mod_version" to version,
					"mod_name" to project.properties["modName"],
					"mod_authors" to project.properties["modAuthors"],
					"mod_description" to project.properties["modDescription"],
					"neo_version_range" to project.properties["neoVersionRange"],
					"minecraft_version_range" to project.properties["minecraftVersionRange"],
				)
			)
		}
	}
}

sourceSets["main"].resources.srcDir("src/generated/resources")

//publishing {
//	publications {
//		register('mavenJava', MavenPublication) {
//			from components.java
//		}
//	}
//	repositories {
//		maven {
//			url "file://${project.projectDir}/repo"
//		}
//	}
//}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}