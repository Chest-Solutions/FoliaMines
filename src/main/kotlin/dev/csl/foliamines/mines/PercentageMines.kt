package dev.csl.foliamines.mines

import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.mines.MinesCore.getArea
import dev.csl.foliamines.FoliaMines.Companion.plugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.block.data.BlockData
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

object PercentageMines {
    private val config: FileConfiguration = plugin.minesConfig
    private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

    private fun getPercentage(area: ArrayList<Location>): Double {
        var totalBlocks = 0
        var solidBlocks = 0

        for (location: Location in area) {
            totalBlocks++
            if (!location.block.type.isAir) {
                solidBlocks++
            }
        }

        return if (totalBlocks > 0) {
            (solidBlocks.toDouble() / totalBlocks.toDouble()) * 100.0
        } else {
            0.0
        }
    }

    fun registerPercentageMines() {
        this.minesSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = this.minesSection.getConfigurationSection(key) ?: return

            if (mineSection.get("type") == "percentage") {
                val loc: Location = mineSection.get("location") as Location
                val loc2: Location = mineSection.get("location2") as Location
                val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())
                val minPercentage: Double = mineSection.get("minimum percentage") as Double

                setupListeners(loc, loc2, minPercentage, blockData)
            }
        }
    }

    fun createPercentageMine(name: String, loc: Location, loc2: Location, minPercentage: Double, blockData: BlockData) {
        val mineSection: ConfigurationSection = this.minesSection.createSection(name) //Haven't Yet Made The Commands Work With It

        mineSection.set("type", "percentage")
        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineSection.set("minimum percentage", minPercentage)

        setupListeners(loc, loc2, minPercentage, blockData)
    }

    private fun setupListeners(loc: Location, loc2: Location, minPercentage: Double, blockData: BlockData) {
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(object: Listener {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            fun onBlockBreakEvent(event: BlockBreakEvent) {
                val area: ArrayList<Location> = getArea(loc, loc2)

                if (area.contains(event.block.location)) {
                    if (getPercentage(area) < minPercentage) {
                        setArea(loc, loc2, blockData)
                    }
                }
            }
        }, plugin)
    }
}