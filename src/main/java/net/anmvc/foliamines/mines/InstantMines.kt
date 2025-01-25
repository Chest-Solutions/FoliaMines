package net.anmvc.foliamines.mines

import net.anmvc.foliamines.FoliaMines.Companion.plugin
import net.anmvc.foliamines.mines.MinesCore.getArea
import net.anmvc.foliamines.mines.MinesCore.setArea
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object InstantMines: Listener {
    private val config: FileConfiguration = plugin.config
    private val mineNameSection: ConfigurationSection = config.getConfigurationSection("mines") ?: config.createSection("mines")

    fun registerInstantMines() {
        mineNameSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = config.getConfigurationSection(key) ?: return

            val loc: Location = mineSection.get("location") as Location
            val loc2: Location? = mineSection.get("location2") as Location?
            val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())

            registerInstantMine(loc, loc2, blockData)
        }
    }

    fun createInstantMine(name: String, loc: Location, loc2: Location?, blockData: BlockData) {
        val mineSection: ConfigurationSection = config.createSection(name) //Haven't Yet Made The Commands Work With It

        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineNameSection.set(name, true)

        registerInstantMine(loc, loc2, blockData)
    }

    private fun registerInstantMine(loc: Location, loc2: Location?, blockData: BlockData) {
        @EventHandler
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
    }
}