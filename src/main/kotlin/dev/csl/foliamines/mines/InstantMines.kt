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

object InstantMines: Listener {
    private val config: FileConfiguration = plugin.minesConfig
    private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!
	private val mines = mutableMapOf<String, Triple<Location, Location?, BlockState>>()

    internal fun registerInstantMines() {
	    Bukkit.getPluginManager().registerEvents(this, plugin)

	    this.minesSection.getKeys(false).forEach { key ->
            val mineSection: ConfigurationSection = this.minesSection.getConfigurationSection(key) ?: return

            if (mineSection.get("type") == "instant") {
                val loc: Location = mineSection.get("location") as Location
                val loc2: Location? = mineSection.get("location2") as Location?
                val blockState: BlockState = Bukkit.getServer().createBlockData(mineSection.get("blockData").toString()).createBlockState()

                setupListeners(mineSection.name, loc, loc2, blockState)
            }
        }
    }

    fun createInstantMine(name: String, loc: Location, loc2: Location?, blockState: BlockState) {
        val mineSection: ConfigurationSection = this.minesSection.createSection(name)

        mineSection.set("type", "instant")
        mineSection.set("blockData", blockState.blockData.asString)
        mineSection.set("location", loc)
        mineSection.set("location2", loc2)

        setupListeners(name, loc, loc2, blockState)
    }

	fun deleteInstantMine(name: String): InstantMines? {
		minesSection.set(name, null)
		if (mines.containsKey(name)) {
			mines.remove(name)
		} else {
			return null
		}
		return InstantMines
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onBlockBreakEvent(event: BlockBreakEvent) {
		for ((_, triple) in mines) {
			val (loc, loc2, blockState) = triple

			if (loc2 == null) {
				if (loc == event.block.location) {
					setArea(event.block.location, null, blockState)
				}
				return
			}

			if (getArea(loc, loc2).contains(event.block.location)) {
				setArea(event.block.location, null, blockState)
			}
		}
	}

    private fun setupListeners(name: String, loc: Location, loc2: Location?, blockState: BlockState) {
	    mines[name] = Triple(loc, loc2, blockState)
    }
}