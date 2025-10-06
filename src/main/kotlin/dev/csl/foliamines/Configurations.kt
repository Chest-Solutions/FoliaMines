package dev.csl.foliamines

import java.io.File

import dev.csl.foliamines.FoliaMines.Companion.plugin

import org.bukkit.configuration.file.YamlConfiguration

object Configurations {
    val minesConfigFile = File(plugin.dataFolder.toString() + File.separator + "mines.yml")

    internal fun createMinesConfig(): YamlConfiguration {
		if (!minesConfigFile.exists()) plugin.saveResource("mines.yml", false)
        return YamlConfiguration.loadConfiguration(this.minesConfigFile) // much slimed down
    }

    internal fun saveMinesConfig(configuration: YamlConfiguration) {
        configuration.save(this.minesConfigFile)
    }
}