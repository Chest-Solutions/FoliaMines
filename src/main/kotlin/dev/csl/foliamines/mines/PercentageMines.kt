package dev.csl.foliamines.mines

import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.mines.MinesCore.getArea
import dev.csl.foliamines.FoliaMines.Companion.plugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockState
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration

object PercentageMines: Listener {
	data class Quadruple<A, B, C, D>(
		val first: A,
		val second: B,
		val third: C,
		val fourth: D
	)
	
    private val config: FileConfiguration = plugin.minesConfig
    private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!
	private val mines = mutableMapOf<String, Quadruple<Location, Location, Int, BlockState>>()

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

    internal fun registerPercentageMines() {
	    Bukkit.getPluginManager().registerEvents(this, plugin)

        this.minesSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = this.minesSection.getConfigurationSection(key) ?: return

            if (mineSection.get("type") == "percentage") {
                val loc: Location = mineSection.get("location") as Location
                val loc2: Location = mineSection.get("location2") as Location
                val blockState: BlockState = Bukkit.getServer().createBlockData(mineSection.get("blockData").toString()).createBlockState()
                val minPercentage: Int = mineSection.get("minimum percentage") as Int

                setupListeners(mineSection.name, loc, loc2, minPercentage, blockState)
            }
        }
    }

    fun createPercentageMine(name: String, loc: Location, loc2: Location, minPercentage: Int, blockState: BlockState) {
        val mineSection: ConfigurationSection = this.minesSection.createSection(name)

        mineSection.set("type", "percentage")
        mineSection.set("blockData", blockState.blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)
        mineSection.set("minimum percentage", minPercentage)

        setupListeners(name, loc, loc2, minPercentage, blockState)
    }

	fun deletePercentageMine(name: String): PercentageMines? {
		minesSection.set(name, null)
		if (mines.containsKey(name)) {
			mines.remove(name)
		} else {
			return null
		}
		return PercentageMines
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onBlockBreakEvent(event: BlockBreakEvent) {
		for ((_, triple) in mines) {
			val (loc, loc2, minPercentage, blockState) = triple

			val area: ArrayList<Location> = getArea(loc, loc2)

			if (area.contains(event.block.location)) {
				if (getPercentage(area) < minPercentage) {
					setArea(loc, loc2, blockState)
				}
			}
		}
	}

    private fun setupListeners(name: String, loc: Location, loc2: Location, minPercentage: Int, blockState: BlockState) {
	    mines[name] = Quadruple(loc, loc2, minPercentage, blockState)
    }
}