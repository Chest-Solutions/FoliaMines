package dev.csl.foliamines.mines

import dev.csl.foliamines.FoliaMines.Companion.plugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.Listener
import org.bukkit.block.data.BlockData

import kotlin.math.max
import kotlin.math.min

object MinesCore: Listener {
    private fun setBlock(loc: Location, blockData: BlockData) {
        Bukkit.getServer().regionScheduler.run(plugin, loc) {
            val chunk = loc.chunk

            if (chunk.isLoaded) {
                loc.block.blockData = blockData
            } else {
                loc.chunk.addPluginChunkTicket(plugin)
                loc.block.blockData = blockData
                loc.chunk.removePluginChunkTicket(plugin)
            }
        }
    }

    fun setArea(loc: Location, loc2: Location?, blockData: BlockData) {
        if (loc2 == null) {
            setBlock(loc, blockData)
            return
        }

        for (location: Location in getArea(loc, loc2)) { setBlock(location, blockData) }
    }

    fun getArea(loc: Location, loc2: Location): ArrayList<Location> {
        val locList: ArrayList<Location> = ArrayList()

        val minX = min(loc.blockX.toDouble(), loc2.blockX.toDouble()).toInt()
        val minY = min(loc.blockY.toDouble(), loc2.blockY.toDouble()).toInt()
        val minZ = min(loc.blockZ.toDouble(), loc2.blockZ.toDouble()).toInt()
        val maxX = max(loc.blockX.toDouble(), loc2.blockX.toDouble()).toInt()
        val maxY = max(loc.blockY.toDouble(), loc2.blockY.toDouble()).toInt()
        val maxZ = max(loc.blockZ.toDouble(), loc2.blockZ.toDouble()).toInt()

        // Iterate through the volume defined by the two locations
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val blockLoc = Location(loc.world, x.toDouble(), y.toDouble(), z.toDouble())
                    locList.add(blockLoc)
                }
            }
        }
        return locList
    }
}
