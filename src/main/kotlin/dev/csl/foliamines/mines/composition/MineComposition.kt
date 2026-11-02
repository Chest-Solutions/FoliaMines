package dev.csl.foliamines.mines.composition

import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Bukkit
import org.bukkit.block.data.BlockData
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.craftbukkit.block.data.CraftBlockData
import java.util.Arrays
import java.util.concurrent.ThreadLocalRandom

class MineComposition {
	private var singleBlock: BlockData? = null

	// NMS CACHE (The Secret Weapon)
	// We store the raw NMS state here so we don't have to cast/unwrap in the loop
	private var singleNmsBlock: BlockState? = null
	private var nmsBlocks = emptyArray<BlockState>()

	// Arrays are faster than Maps
	private var weights = DoubleArray(0)
	private var blocks = emptyArray<BlockData>()
	private var totalWeight = 0.0

	fun addBlock(data: BlockData, percentage: Double) {
		singleBlock = null
		singleNmsBlock = null
		totalWeight += percentage

		// Add to arrays
		weights += totalWeight
		blocks += data

		// Cache the NMS State immediately
		nmsBlocks += (data as CraftBlockData).state
	}

	fun setSingle(data: BlockData) {
		singleBlock = data
		singleNmsBlock = (data as CraftBlockData).state
	}

	/**
	 * STANDARD BUKKIT METHOD (For GUIs, etc)
	 */
	fun next(): BlockData {
		singleBlock?.let { return it }
		val index = getWeightedIndex()
		return blocks[index]
	}

	/**
	 * NMS HOT PATH (For MinesCore)
	 * Returns the raw NMS BlockState. Zero allocation. Zero casting.
	 */
	fun nextNMS(): BlockState {
		// 1. Single Block (Instant)
		singleNmsBlock?.let { return it }

		// 2. Weighted Random
		val index = getWeightedIndex()
		return nmsBlocks[index]
	}

	// Extracted logic to keep code DRY
	private fun getWeightedIndex(): Int {
		val value = ThreadLocalRandom.current().nextDouble() * totalWeight
		var index = Arrays.binarySearch(weights, value)
		if (index < 0) {
			index = -index - 1
		}
		if (index >= blocks.size) index = blocks.size - 1
		return index
	}

	fun saveToConfig(section: ConfigurationSection) {
		if (singleBlock != null) {
			section.set("composition-type", "single")
			section.set("block", singleBlock!!.asString)
		} else {
			section.set("composition-type", "random")
			val blocksSection = section.createSection("blocks")
			var previousWeight = 0.0

			for (i in weights.indices) {
				val cumulativeWeight = weights[i]
				val data = blocks[i]

				val actualPercentage = cumulativeWeight - previousWeight
				blocksSection.set(data.asString, actualPercentage)

				previousWeight = cumulativeWeight
			}
		}
	}

	companion object {
		fun fromConfig(section: ConfigurationSection): MineComposition {
			val comp = MineComposition()
			val type = section.getString("composition-type") ?: "single"

			if (type == "single") {
				val matString = section.getString("block") ?: section.getString("blockData")?.also {
					section.set("blockData", null)
				} ?: "stone"

				val data = Bukkit.createBlockData(matString)
				comp.setSingle(data)
				// Don't save on load, only save on modify/create
			} else {
				val blocksSection = section.getConfigurationSection("blocks")
				blocksSection?.getKeys(false)?.forEach { key ->
					val mat = Bukkit.createBlockData(key)
					val percent = blocksSection.getDouble(key)
					comp.addBlock(mat, percent)
				}
			}
			return comp
		}
	}
}