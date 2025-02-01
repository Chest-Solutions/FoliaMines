plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.11"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    kotlin("jvm") version "2.1.10"
}

group = "net.anmvc"
version = "1.0-SNAPSHOT"

val serverVersion = project.properties["serverVersion"] as String

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        name = "enginehub"
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    paperweight.paperDevBundle("$serverVersion-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:9.7.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.10-SNAPSHOT")
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
    relocate("dev.jorel.commandapi", "net.anmvc.relocated")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
