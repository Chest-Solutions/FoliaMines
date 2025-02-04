package dev.csl.foliamines.mines

import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.mines.MinesCore.getArea
import dev.csl.foliamines.mines.MinesCore.setArea
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object InstantMines {
    private val config: FileConfiguration = plugin.config
    private val mineNameSection: ConfigurationSection = config.getConfigurationSection("mines") ?: config.createSection("mines")

    fun registerInstantMines() {
        mineNameSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = config.getConfigurationSection(key) ?: return

            val loc: Location = mineSection.get("location") as Location
            val loc2: Location? = mineSection.get("location2") as Location?
            val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())

            setupListeners(loc, loc2, blockData)
        }
    }

    fun createInstantMine(name: String, loc: Location, loc2: Location?, blockData: BlockData) {
        val mineSection: ConfigurationSection = config.createSection(name) //Haven't Yet Made The Commands Work With It

        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineNameSection.set(name, true)

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