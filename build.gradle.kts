plugins {
    java
	id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("com.gradleup.shadow") version "9.2.2"
    id("xyz.jpenilla.run-paper") version "3.0.1"
    kotlin("jvm") version "2.2.20"
}

group = "dev.csl"
version = "1.0-SNAPSHOT"

val serverVersion = project.properties["serverVersion"] as String

repositories {
	mavenCentral()

    maven {
        name = "PaperMC"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "XeonDevs"
        url = uri("https://repo.xenondevs.xyz/releases")
    }
}

dependencies {
	paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-paper-shade:11.0.0")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("xyz.xenondevs.invui:invui-kotlin:1.47")
}

val targetJavaVersion = 21
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