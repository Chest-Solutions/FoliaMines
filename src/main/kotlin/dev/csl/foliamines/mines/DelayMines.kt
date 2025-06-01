package dev.csl.foliamines.mines

import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.FoliaMines.Companion.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

import java.util.concurrent.TimeUnit

object DelayMines {
    private val tasks: HashMap<String, ScheduledTask> = HashMap()


	private val config: YamlConfiguration = plugin.minesConfig
    private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

    fun registerDelayMines() {
        this.minesSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = this.minesSection.getConfigurationSection(key) ?: return

            if (mineSection.get("type") == "delay") {
                val loc: Location = mineSection.get("location") as Location
                val loc2: Location? = mineSection.get("location2") as Location?
                val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())
                val delay: Int = mineSection.get("delay") as Int

                registerMine(loc, loc2, delay, blockData, mineSection.name)
            }
        }
    }

    fun createDelayMine(name: String, loc: Location, loc2: Location?, delay: Int, blockData: BlockData) {
        val mineSection: ConfigurationSection = this.minesSection.createSection(name)

        mineSection.set("type", "delay")
        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc)
        mineSection.set("delay", delay)

        registerMine(loc, loc2, delay, blockData, name)
    }

    fun deleteDelayMine(name: String) {
	    minesSection.set(name, null)
        tasks.get(name)?.cancel()
        tasks.remove(name)
    }

    private fun registerMine(loc: Location, loc2: Location?, delay: Int, blockData: BlockData, mineName: String) {
        tasks.put(mineName, Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
            setArea(loc, loc2, blockData)
        }, 0L, delay.toLong(), TimeUnit.MINUTES))
    }
}