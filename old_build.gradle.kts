plugins {
	java

	id("java-library")
	id("idea")
	id("maven-publish")

//	id("dev.architectury.loom") version "1.7.410"
	kotlin("jvm") version "2.0.20"
}

//base.archivesName = project.properties["mod_id"] as String
//
//allprojects {
//	apply(plugin = "java")
//	apply(plugin = "kotlin")
//	apply(plugin = "dev.architectury.loom")
//	apply(plugin = "maven-publish")
//
//	version = project.properties["mod_version"] as String
//	group = project.properties["mod_group_id"] as String
//
//	val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")
//
//	configure<LoomGradleExtensionAPI> {
//		silentMojangMappingsLicense()
//
//		runs {
//			named("client") {
//				client()
//				programArgs.addAll(mutableListOf("--username", "Vyrek_", "--quickPlaySingleplayer", "Test"))
//			}
//			named("server") {
//				programArgs.add("--nogui")
//			}
//		}
//	}
//
//	repositories {
//		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
//		maven("https://maven.neoforged.net/releases/") { name = "NeoForge" }
//		maven("https://maven.wispforest.io") { name = "owolib" }
//		maven("https://maven.su5ed.dev/releases") { name = "Syntra Forgified Fabric Api" }
//		maven("https://thedarkcolour.github.io/KotlinForForge/") {
//			name = "Kotlin for Forge"
//			content {
//				includeGroup("thedarkcolour")
//			}
//		}
//	}
//
//	@Suppress("UnstableApiUsage")
//	dependencies {
//		neoForge("net.neoforged:neoforge:21.1.66")
//
//		"minecraft"("com.mojang:minecraft:1.21.1")
//		"mappings"(loom.layered {
//			officialMojangMappings()
//			parchment("org.parchmentmc.data:parchment-1.21.1:2024.07.28@zip")
//		})
//
////		modImplementation("thedarkcolour", "kotlinforforge-neoforge", kotlinNeoVersion)
////		modImplementation("io.wispforest", "owo-lib-neoforge", "${owoVersion}+${majorMinecraftVersion}")
////
////		compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
////		compileOnly("org.jetbrains.kotlin:kotlin-reflect")
//	}
//
//	tasks {
//		withType<JavaCompile> {
//			options.encoding = "UTF-8"
//			sourceCompatibility = JavaVersion.VERSION_21.toString()
//			targetCompatibility = JavaVersion.VERSION_21.toString()
//			options.release.set(JavaVersion.VERSION_21.toString().toInt())
//		}
//
//		withType<KotlinCompile> {
//			compilerOptions {
//				jvmTarget.set(JvmTarget.JVM_21)
//			}
//		}
//
////		jar {
////			from("LICENSE")
////		}
//
//		processResources {
//			inputs.property("mod_version", version)
//
//			filesMatching("META-INF/neoforge.mods.toml") {
//				expand(
//					mutableMapOf(
//						"loader_version_range" to project.properties["loaderVersionRange"],
//						"mod_license" to project.properties["modLicense"],
//						"mod_id" to project.properties["modId"],
//						"mod_version" to version,
//						"mod_name" to project.properties["modName"],
//						"mod_authors" to project.properties["modAuthors"],
//						"mod_description" to project.properties["modDescription"],
//						"neo_version_range" to project.properties["neoVersionRange"],
//						"minecraft_version_range" to project.properties["minecraftVersionRange"],
//					)
//				)
//			}
//		}
//	}
//
//	java {
//		toolchain {
//			languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_21.toString()))
//		}
//
//		sourceCompatibility = JavaVersion.VERSION_21
//		targetCompatibility = JavaVersion.VERSION_21
//
//		withSourcesJar()
//	}
//}
//
//sourceSets {
//	main {
//		resources {
//			srcDir("src/generated/resources")
//		}
//	}
//}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
	}
}