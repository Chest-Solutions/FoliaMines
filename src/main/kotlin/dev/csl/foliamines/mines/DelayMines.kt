package dev.csl.foliamines.mines

import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.FoliaMines.Companion.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockState
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration

import java.util.concurrent.TimeUnit

object DelayMines {
    private val tasks: HashMap<String, ScheduledTask> = HashMap()


	private val config: YamlConfiguration = plugin.minesConfig
    private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

    internal fun registerDelayMines() {
        this.minesSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = this.minesSection.getConfigurationSection(key) ?: return

            if (mineSection.get("type") == "delay") {
                val loc: Location = mineSection.get("location") as Location
                val loc2: Location? = mineSection.get("location2") as Location?
                val blockState: BlockState = Bukkit.getServer().createBlockData(mineSection.get("blockData").toString()).createBlockState()
                val delay: Int = mineSection.get("delay") as Int

                registerMine(loc, loc2, delay, blockState, mineSection.name)
            }
        }
    }

    fun createDelayMine(name: String, loc: Location, loc2: Location?, delay: Int, blockState: BlockState) {
        val mineSection: ConfigurationSection = this.minesSection.createSection(name)

        mineSection.set("type", "delay")
        mineSection.set("blockData", blockState.blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc)
        mineSection.set("delay", delay)

        registerMine(loc, loc2, delay, blockState, name)
    }

    fun deleteDelayMine(name: String) {
	    minesSection.set(name, null)
        tasks.get(name)?.cancel()
        tasks.remove(name)
    }

    private fun registerMine(loc: Location, loc2: Location?, delay: Int, blockState: BlockState, mineName: String) {
	    tasks[mineName] = Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
	        setArea(loc, loc2, blockState)
	    }, 0L, delay.toLong(), TimeUnit.MINUTES)
    }
}