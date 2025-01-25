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

object PercentageMines: Listener {
    private val config: FileConfiguration = plugin.config
    private val mineNameSection: ConfigurationSection = config.getConfigurationSection("mines") ?: config.createSection("mines")

    private fun registerMine(loc: Location, loc2: Location, blockData: BlockData, minPercentage: Double) {
        @EventHandler
        fun onBlockBreakEvent(event: BlockBreakEvent) {
            val area: ArrayList<Location> = getArea(loc, loc2)

            if (area.contains(event.block.location)) {
                if (getPercentage(area) < minPercentage) {
                    setArea(loc, loc2, blockData)
                }
            }
        }
    }

    private fun getPercentage(area: ArrayList<Location>): Double {
        /*
        val minX = minOf(loc1.blockX, loc2.blockX)
        val maxX = maxOf(loc1.blockX, loc2.blockX)
        val minY = minOf(loc1.blockY, loc2.blockY)
        val maxY = maxOf(loc1.blockY, loc2.blockY)
        val minZ = minOf(loc1.blockZ, loc2.blockZ)
        val maxZ = maxOf(loc1.blockZ, loc2.blockZ)

        var totalBlocks = 0
        var solidBlocks = 0

        for (x in minX  ..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val block = loc.world?.getBlockAt(x, y, z)
                    totalBlocks++
                    if ((block != null) and (!block.type.isAir)) {
                        solidBlocks++
                    }
                }
            }
        }

        return if (totalBlocks > 0) {
            (solidBlocks.toDouble() / totalBlocks.toDouble()) * 100.0
        } else {
            0.0
        }

       */


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
        mineNameSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = config.getConfigurationSection(key) ?: return

            val loc: Location = mineSection.get("location") as Location
            val loc2: Location = mineSection.get("location2") as Location // why teh ? after location // it's required for other mines
            val blockData: BlockData = Bukkit.getServer().createBlockData(mineSection.get("blockType").toString())
            val minPercentage: Double = mineSection.get("minimum percentage") as Double

//            Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
//                if (getPercentage(loc,loc2) < minPercentage) {
//                    setArea(loc, loc2, blockData)
//                }
//            }, 0L, 5, TimeUnit.MILLISECONDS)

            registerMine(loc, loc2, blockData, minPercentage)
        }
    }

    fun createPercentageMine(name: String, loc: Location, loc2: Location, minPercentage: Double, blockData: BlockData) {
        val mineSection: ConfigurationSection = config.createSection(name) //Haven't Yet Made The Commands Work With It

        mineSection.set("blockType", blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineSection.set("minimum percentage", minPercentage)
        mineNameSection.set(name, true)

        registerMine(loc, loc2, blockData, minPercentage)
    }
}