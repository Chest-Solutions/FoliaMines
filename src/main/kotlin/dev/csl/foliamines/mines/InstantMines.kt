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

object InstantMines {
    private val config: FileConfiguration = plugin.minesConfig
    private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

    fun registerInstantMines() {
        this.minesSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = this.minesSection.getConfigurationSection(key) ?: return

            if (mineSection.get("type") == "instant") {
                val loc: Location = mineSection.get("location") as Location
                val loc2: Location? = mineSection.get("location2") as Location?
                val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())

                setupListeners(loc, loc2, blockData)
            }
        }
    }

    fun createInstantMine(name: String, loc: Location, loc2: Location?, blockData: BlockData) {
        val mineSection: ConfigurationSection = this.minesSection.createSection(name) //Haven't Yet Made The Commands Work With It

        mineSection.set("type", "instant")
        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)

        setupListeners(loc, loc2, blockData)
    }

    private fun setupListeners(loc: Location, loc2: Location?, blockData: BlockData) {
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(object: Listener {
            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            fun onBlockBreakEvent(event: BlockBreakEvent) {
                if (loc2 == null) {
                    if (loc == event.block.location) {
                        setArea(event.block.location, null, blockData)
                    }
                    return
                }

                if (getArea(loc, loc2).contains(event.block.location)) {
                    setArea(event.block.location, null, blockData)
                }
            }
        }, plugin)
    }
}