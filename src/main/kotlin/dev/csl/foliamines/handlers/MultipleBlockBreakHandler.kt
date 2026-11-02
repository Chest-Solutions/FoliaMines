package dev.csl.foliamines.handlers

import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.gui.CreateMineGui
import dev.csl.foliamines.gui.items.isPlayerSelecting
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.MinesCore
import dev.csl.foliamines.mines.PercentageMines
import dev.csl.foliamines.utils.MineRegion
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class MultipleBlockBreakHandler : Listener {

	fun setup() {
		Bukkit.getPluginManager().registerEvents(this, plugin)
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	fun onBlockBreakEvent(event: BlockBreakEvent) {
		val player = event.player
		val loc = event.block.location

		// --- 1. HANDLE SELECTION MODE (Priority) ---
		// Check this first. If selecting, do NOT trigger mines.
		if (isPlayerSelecting.containsKey(player.uniqueId)) {
			event.isCancelled = true // Prevent block break

			val current = isPlayerSelecting[player.uniqueId]

			if (current == null) {
				// First point
				isPlayerSelecting[player.uniqueId] = Pair(loc, null)
				player.sendMessage("§aPosition 1 set.")
			} else {
				// Second point -> Finish
				isPlayerSelecting[player.uniqueId] = Pair(current.first, loc)
				player.sendMessage("§aPosition 2 set. Opening menu...")
				CreateMineGui.openCreateMinesGui(player, false)
			}
			return
		}

		// --- 2. HANDLE MINES ---
		val chunkKey = (loc.blockX shr 4).toLong() shl 32 or ((loc.blockZ shr 4).toLong() and 0xFFFFFFFFL)

		// A. INSTANT MINES
		val instantCandidates = InstantMines.getCandidateMines(chunkKey)
		if (instantCandidates.isNotEmpty()) {
			for (mine in instantCandidates) {
				if (mine.contains(loc)) {
					// FIX: Reset ONLY the broken block, not the whole mine!
					// We create a temporary 1x1x1 region.
					val singleBlockRegion = MineRegion(loc, null)

					MinesCore.setArea(singleBlockRegion, mine.composition)
					return // Found mine, stop processing
				}
			}
		}

		// B. PERCENTAGE MINES
		val percentageCandidates = PercentageMines.getCandidateMines(chunkKey)
		if (percentageCandidates.isNotEmpty()) {
			for (mine in percentageCandidates) {
				if (mine.contains(loc)) {
					val brokenCount = mine.brokenBlocks.incrementAndGet()
					val remaining = mine.totalVolume - brokenCount
					val currentPercent = (remaining.toDouble() / mine.totalVolume.toDouble()) * 100.0

					if (currentPercent <= mine.minPercentage) {
						// On REAL multi-region Folia, two threads could both pass this check.
						// Guard the reset so exactly one break triggers it:
						if (mine.resetInProgress.compareAndSet(false, true)) {
							mine.resetCounter()
							MinesCore.setArea(mine.region, mine.composition)
							mine.resetInProgress.set(false)
						}
					}
					return
				}
			}
		}
	}
}