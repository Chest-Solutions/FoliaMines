package dev.csl.foliamines.handlers

import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.MinesCore.setArea
import dev.csl.foliamines.mines.PercentageMines
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class MultibleBlockBreakHandler : Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onBlockBreakEvent(event: BlockBreakEvent) {
		val loc = event.block.location
		val chunkKey = (loc.blockX shr 4).toLong() shl 32 or ((loc.blockZ shr 4).toLong() and 0xFFFFFFFFL)

		// 1. INSTANT MINES
		val instantCandidates = InstantMines.getCandidateMines(chunkKey)
		if (instantCandidates.isNotEmpty()) {
			for (mine in instantCandidates) {
				if (mine.contains(loc)) {
					// Reset single block instantly
					setArea(loc, null, mine.composition)
					return
				}
			}
		}

		// 2. PERCENTAGE MINES
		val percentageCandidates = PercentageMines.getCandidateMines(chunkKey)
		if (percentageCandidates.isNotEmpty()) {
			for (mine in percentageCandidates) {
				if (mine.contains(loc)) {
					val brokenCount = mine.brokenBlocks.incrementAndGet()
					val remaining = mine.totalVolume - brokenCount
					val currentPercent = (remaining.toDouble() / mine.totalVolume.toDouble()) * 100.0

					if (currentPercent <= mine.minPercentage) {
						mine.resetCounter()
						setArea(mine.region.getLoc1(), mine.region.getLoc2(), mine.composition)
					}
					return
				}
			}
		}
	}
}