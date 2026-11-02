plugins {
    java
	id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.gradleup.shadow") version "9.2.2"
    id("xyz.jpenilla.run-paper") version "3.0.1"
    kotlin("jvm") version "2.4.21"
}

group = "dev.csl"
version = "1.0-SNAPSHOT"

val serverVersion = project.properties["serverVersion"] as String

repositories {
	mavenCentral()

	maven {
		name = "EngineHub"
		url = uri("https://maven.enginehub.org/repo/")
	}

    maven {
        name = "PaperMC"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
	compileOnly("com.github.stefvanschie.inventoryframework:IF:0.12.0")
	paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-paper-shade:12.1.0")
	compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.9")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

val targetJavaVersion = 25
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.runServer {
    minecraftVersion(serverVersion)
}

tasks.shadowJar {
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}