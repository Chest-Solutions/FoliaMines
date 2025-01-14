package net.anmvc.foliamines

import net.anmvc.foliamines.FoliaMines.Companion.plugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files;



object Configuration {

    private var minesConfig: YamlConfiguration? = null
    var minesConfigFile: File? = null

    fun getDataFolder(): String {
        return plugin.dataFolder.toPath().joinToString{"/"}
    }

    fun reloadMines(): YamlConfiguration {
        if (minesConfigFile == null) {
            createConfig("mines.yml")
            minesConfigFile = File(getDataFolder() + "mines.yml")
        }
        minesConfig = YamlConfiguration.loadConfiguration(minesConfigFile!!)
        return minesConfig!!
    }

    private fun createConfig(file: String) {
        try {
            val newFile = File(getDataFolder() + file)

            if (!newFile.exists()) {
                val parentFile = newFile.parentFile
                parentFile?.mkdirs()

                val inputStream: InputStream? = plugin.getResource(file)
                if (inputStream != null) {
                    Files.copy(inputStream, newFile.toPath())
                } else {
                    newFile.createNewFile()
                }
            }

        } catch (e: IOException) {
            plugin.logger.severe("Error to create $file")
        }
    }

}