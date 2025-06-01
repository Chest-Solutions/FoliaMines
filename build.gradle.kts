plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    kotlin("jvm") version "2.1.10"
}

group = "dev.csl"
version = "1.0-SNAPSHOT"

val serverVersion = project.properties["serverVersion"] as String

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "enginehub"
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        name = "Minecraft"
        url = uri("https://libraries.minecraft.net/")
    }

    maven {
        name = "XeonDevs"
        url = uri("https://repo.xenondevs.xyz/releases")
    }

    maven {
        name = "sonatype-oss-snapshots"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    paperweight.paperDevBundle("$serverVersion-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-bukkit-shade:10.0.1")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("xyz.xenondevs.invui:invui-kotlin:1.45")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.1")

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
    dependsOn(tasks.reobfJar)
}


