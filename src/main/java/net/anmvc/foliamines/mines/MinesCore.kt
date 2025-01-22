package net.anmvc.foliamines.mines

import net.anmvc.foliamines.FoliaMines.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.block.BlockType
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.Listener
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max
import kotlin.math.min


object MinesCore: Listener {
    val config: FileConfiguration = plugin.config
    val mineNameSection: ConfigurationSection = config.getConfigurationSection("mines") ?: config.createSection("mines")


    fun setBlock(loc: Location, blockType: BlockType) {
        val chunk = loc.chunk
        Bukkit.getServer().regionScheduler.run(plugin, loc, {
            if (chunk.isLoaded) {
                loc.block.setType(blockType.asMaterial()!!)
            } else {
                loc.chunk.addPluginChunkTicket(plugin)
                loc.block.setType(blockType.asMaterial()!!)
            }
        })
    }

    fun modifyBlockInUnloadedChunk(location: Location, blockType: BlockType) {
        /*
        if (location == null || location.world == null) {
            Bukkit.getLogger().warning("Invalid location provided.")
            return
        }

        val world = location.world
        val chunkX = location.blockX shr 4 // Faster than division
        val chunkZ = location.blockZ shr 4 // Faster than division

        val chunkFuture = world.getChunkAtAsync(chunkX, chunkZ)
        chunkFuture.thenAccept { chunk: Chunk? ->
            if (chunk == null) {
                Bukkit.getLogger().warning("Chunk not found at x:$chunkX z:$chunkZ")
                return@thenAccept
            }
            val block = location.block
            block.setBlockData(blockType.createBlockData(), false) // Set the block data
            block.state.update(true, true) // Update block and force re-rendering
            Bukkit.getLogger().info("Block modified at location: $location")
            world.unloadChunk(chunk)
        }
       */
    }

    fun setArea(loc: Location, loc2: Location, blockType: BlockType) {
        if (loc === loc2) {
            setBlock(loc, blockType)
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
                    setBlock(blockLoc, blockType)
                    amountOfblocks.set(amountOfblocks.get() + 1)
                }
            }
        }
    }

    fun createMine(name: String, loc: Location, loc2: Location?, delay: Int, blockType: BlockType) {
        val mineSection: ConfigurationSection = config.createSection(name)

        mineSection.set("blockType", blockType.key.value())
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineSection.set("delay", delay)
        mineNameSection.set(name, true)

        if (loc2 != null) {
            Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
                setArea(loc, loc2, blockType)
                plugin.componentLogger.info("eA")
            }, 0L, delay.toLong(), TimeUnit.MINUTES)
        } else {
            Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
                setBlock(loc, blockType)
            }, 0L, delay.toLong(), TimeUnit.MINUTES)
        }
    }

    fun registerMines() {
        mineNameSection.getKeys(false).forEach{ key ->
            val mineSection: ConfigurationSection = config.getConfigurationSection(key) ?: return

            val loc: Location = mineSection.get("location") as Location
            val loc2: Location? = mineSection.get("location2") as Location?
            val blockType: BlockType = Registry.BLOCK.getOrThrow(NamespacedKey.fromString(mineSection.get("blockType").toString())!!)
            val delay: Int = mineSection.get("delay") as Int

            if (loc2 != null) {
                Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
                    setArea(loc, loc2, blockType)
                    plugin.componentLogger.info("eA")
                }, 0L, delay.toLong(), TimeUnit.MINUTES)
            } else {
                Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
                    setBlock(loc, blockType)
                }, 0L, delay.toLong(), TimeUnit.MINUTES)
            }
        }
    }

}
