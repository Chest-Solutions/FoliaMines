package net.anmvc.foliamines.mines

import net.anmvc.foliamines.FoliaMines.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.event.Listener
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max
import kotlin.math.min

@SuppressWarnings("deprecation")
object MinesCore: Listener {
    @SuppressWarnings("deprecation","removal")
    fun setBlock(loc: Location, blockData: BlockData) {
        val chunk = loc.chunk
        Bukkit.getServer().regionScheduler.run(plugin, loc, {
            if (chunk.isLoaded) {
                loc.block.blockData = blockData
            } else {
                loc.chunk.addPluginChunkTicket(plugin)
                loc.block.blockData = blockData
                loc.chunk.removePluginChunkTicket(plugin)
            }
        })
    }

    fun setArea(loc: Location, loc2: Location?, blockData: BlockData) {
        if (loc2 == null) {
            setBlock(loc, blockData)
            return
        }

        val amountOfblocks = AtomicReference(0)

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
                    setBlock(blockLoc, blockData)
                    amountOfblocks.set(amountOfblocks.get() + 1)
                }
            }
        }
    }
}
