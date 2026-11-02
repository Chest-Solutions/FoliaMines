package dev.csl.foliamines.mines

import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.gui.items.BaseMine
import dev.csl.foliamines.gui.items.MineManager
import dev.csl.foliamines.gui.items.MineType
import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.mines.composition.MineComposition
import dev.csl.foliamines.utils.MineRegion
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.TimeUnit

object DelayMines : MineManager {

	private val config: FileConfiguration = plugin.minesConfig
	private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

	class DelayCache(
		name: String,
		region: MineRegion,
		val delayMinutes: Int,
		composition: MineComposition,
		var task: ScheduledTask?
	) : BaseMine(name, region, composition) {

		override val type = MineType.DELAY

		override fun reset() {
			setArea(region, composition)
		}
	}

	internal val delayMines = mutableListOf<DelayCache>()

	override fun register() {
		delayMines.forEach { it.task?.cancel() }
		delayMines.clear()

		this.minesSection.getKeys(false).forEach { key ->
			val section = this.minesSection.getConfigurationSection(key) ?: return

			if (section.getString("type") == "delay") {
				// 1. Load Region (Handles migration & validation)
				val region = MineRegion.fromConfig(section) ?: return

				val delay = section.getInt("delay")
				val composition = MineComposition.fromConfig(section)

				createAndCache(key, region, delay, composition)
			}
		}
	}

	override fun delete(name: String): Boolean {
		if (delayMines.none { it.name == name } && !minesSection.contains(name)) return false

		minesSection.set(name, null)

		return delayMines.removeIf { mine ->
			if (mine.name == name) {
				mine.task?.cancel()
				true
			} else {
				false
			}
		}
	}

	fun createDelayMine(name: String, region: MineRegion, delayMinutes: Int, composition: MineComposition) {
		val section = this.minesSection.createSection(name)
		section.set("type", "delay")

		// Use Region to save coords
		region.saveToConfig(section)
		composition.saveToConfig(section)

		section.set("delay", delayMinutes)

		createAndCache(name, region, delayMinutes, composition)

		// Initial Fill
		setArea(region, composition)
	}

	private fun createAndCache(name: String, region: MineRegion, delay: Int, composition: MineComposition) {
		// Remove existing if overwriting
		delayMines.find { it.name == name }?.task?.cancel()
		delayMines.removeIf { it.name == name }

		val cache = DelayCache(name, region, delay, composition, null)

		val task = org.bukkit.Bukkit.getServer().asyncScheduler.runAtFixedRate(plugin, {
			cache.reset()
		}, 0L, cache.delayMinutes.toLong(), TimeUnit.MINUTES)

		cache.task = task
		delayMines.add(cache)
	}
}