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

object InstantMines : MineManager {

	private val config: FileConfiguration = plugin.minesConfig
	private val minesSection: ConfigurationSection = this.config.getConfigurationSection("mines")!!

	class InstantCache(
		name: String,
		region: MineRegion,
		composition: MineComposition
	) : BaseMine(name, region, composition) {

		override val type = MineType.INSTANT

		override fun reset() {
			setArea(region, composition)
		}
	}

	internal val instantMines = mutableListOf<InstantCache>()

	// --- HYBRID SPATIAL HASHING ---
	private val chunkMap = HashMap<Long, MutableList<InstantCache>>()
	private val globalMines = mutableListOf<InstantCache>()
	private const val MAX_INDEX_SIZE = 4096

	fun getCandidateMines(chunkKey: Long): List<InstantCache> {
		val local = chunkMap[chunkKey]
		return if (local == null) globalMines
		else if (globalMines.isEmpty()) local
		else local + globalMines
	}

	override fun register() {
		instantMines.clear()
		chunkMap.clear()
		globalMines.clear()

		this.minesSection.getKeys(false).forEach { key ->
			val section = this.minesSection.getConfigurationSection(key) ?: return
			if (section.getString("type") == "instant") {
				// Load Region via Helper
				val region = MineRegion.fromConfig(section) ?: return
				val composition = MineComposition.fromConfig(section)

				cacheMine(key, region, composition)
			}
		}
	}

	override fun delete(name: String): Boolean {
		if (instantMines.none { it.name == name } && !minesSection.contains(name)) return false

		minesSection.set(name, null)

		val removed = instantMines.removeIf { it.name == name }
		if (removed) rebuildIndex()
		return removed
	}

	fun createInstantMine(name: String, region: MineRegion, composition: MineComposition) {
		val section = this.minesSection.createSection(name)
		section.set("type", "instant")

		region.saveToConfig(section)
		composition.saveToConfig(section)

		cacheMine(name, region, composition)
		setArea(region, composition)
	}

	private fun cacheMine(name: String, region: MineRegion, composition: MineComposition) {
		val mine = InstantCache(name, region, composition)
		instantMines.add(mine)
		indexMine(mine)
	}

	private fun indexMine(mine: InstantCache) {
		val minCx = mine.region.minX shr 4
		val maxCx = mine.region.maxX shr 4
		val minCz = mine.region.minZ shr 4
		val maxCz = mine.region.maxZ shr 4

		// Prevent OOM on massive mines
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
		instantMines.forEach { indexMine(it) }
	}
}