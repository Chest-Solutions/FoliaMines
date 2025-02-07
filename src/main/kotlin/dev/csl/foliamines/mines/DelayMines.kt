package dev.csl.foliamines.mines

import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.mines.MinesCore.setArea
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.TimeUnit

object DelayMines {
    private val config: FileConfiguration = plugin.config
    private val mineNameSection: ConfigurationSection = config.getConfigurationSection("mines") ?: config.createSection("mines")

    fun registerDelayMines() {
        mineNameSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = config.getConfigurationSection(key) ?: return

            val loc: Location = mineSection.get("location") as Location
            val loc2: Location? = mineSection.get("location2") as Location?
            val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())
            val delay: Int = mineSection.get("delay") as Int

            registerMine(loc, loc2, delay, blockData)
        }
    }

    fun createDelayMine(name: String, loc: Location, loc2: Location?, delay: Int, blockData: BlockData) {
        val mineSection: ConfigurationSection = config.createSection(name)

        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineSection.set("delay", delay)
        mineNameSection.set(name, true)

        registerMine(loc, loc2, delay, blockData)
    }

    private fun registerMine(loc: Location, loc2: Location?, delay: Int, blockData: BlockData) {
        Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
            setArea(loc, loc2, blockData)
        }, 0L, delay.toLong(), TimeUnit.MINUTES)
    }
}