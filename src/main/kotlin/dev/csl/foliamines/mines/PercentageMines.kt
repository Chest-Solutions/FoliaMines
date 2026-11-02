package dev.csl.foliamines.mines

import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.gui.items.BaseMine
import dev.csl.foliamines.gui.items.MineManager
import dev.csl.foliamines.gui.items.MineType
import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.mines.composition.MineComposition
import dev.csl.foliamines.utils.MineRegion
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

object PercentageMines : MineManager {

	private val config: FileConfiguration = plugin.minesConfig
	private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

	class PercentageMineCache(
		name: String,
		region: MineRegion,
		composition: MineComposition,
		val minPercentage: Double
	) : BaseMine(name, region, composition) {

		override val type = MineType.PERCENTAGE

		val totalVolume: Long = region.volume
		val brokenBlocks = AtomicLong(0)
		val resetInProgress: AtomicBoolean = AtomicBoolean(false)

		fun resetCounter() { brokenBlocks.set(0) }

		override fun reset() {
			resetCounter()
			setArea(region, composition)
		}
	}

	internal val percentageMines = mutableListOf<PercentageMineCache>()

	// Hybrid Spatial Hashing
	private val chunkMap = HashMap<Long, MutableList<PercentageMineCache>>()
	private val globalMines = mutableListOf<PercentageMineCache>()
	private const val MAX_INDEX_SIZE = 4096

	fun getCandidateMines(chunkKey: Long): List<PercentageMineCache> {
		val local = chunkMap[chunkKey]
		return if (local == null) globalMines
		else if (globalMines.isEmpty()) local
		else local + globalMines
	}

	override fun register() {
		percentageMines.clear()
		chunkMap.clear()
		globalMines.clear()

		this.minesSection.getKeys(false).forEach { key ->
			val section = this.minesSection.getConfigurationSection(key) ?: return
			if (section.getString("type") == "percentage") {
				val region = MineRegion.fromConfig(section) ?: return
				val composition = MineComposition.fromConfig(section)
				val minPercent = section.getDouble("minimum percentage")

				cacheMine(key, region, minPercent, composition)
			}
		}
	}

	override fun delete(name: String): Boolean {
		if (percentageMines.none { it.name == name } && !minesSection.contains(name)) return false

		minesSection.set(name, null)

		val removed = percentageMines.removeIf { it.name == name }
		if (removed) rebuildIndex()
		return removed
	}

	fun createPercentageMine(name: String, region: MineRegion, minPercentage: Double, composition: MineComposition) {
		val section = this.minesSection.createSection(name)
		section.set("type", "percentage")

		region.saveToConfig(section)
		composition.saveToConfig(section)

		section.set("minimum percentage", minPercentage)

		cacheMine(name, region, minPercentage, composition)
		setArea(region, composition)
	}

	private fun cacheMine(name: String, region: MineRegion, minPercent: Double, composition: MineComposition) {
		val mine = PercentageMineCache(name, region, composition, minPercent)
		percentageMines.add(mine)
		indexMine(mine)
	}

	private fun indexMine(mine: PercentageMineCache) {
		val minCx = mine.region.minX shr 4
		val maxCx = mine.region.maxX shr 4
		val minCz = mine.region.minZ shr 4
		val maxCz = mine.region.maxZ shr 4

		val area = ((maxCx - minCx + 1).toLong() * (maxCz - minCz + 1).toLong())

		if (area > MAX_INDEX_SIZE) {
			globalMines.add(mine)
		} else {
			for (cx in minCx..maxCx) {
				for (cz in minCz..maxCz) {
					val key = (cx.toLong() shl 32) or (cz.toLong() and 0xFFFFFFFFL)
					chunkMap.computeIfAbsent(key) { ArrayList() }.add(mine)
				}
			}
		}
	}

	private fun rebuildIndex() {
		chunkMap.clear()
		globalMines.clear()
		percentageMines.forEach { indexMine(it) }
	}
}