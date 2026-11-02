package dev.csl.foliamines.mines

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.mines.composition.MineComposition
import dev.csl.foliamines.utils.MineRegion
import net.minecraft.core.BlockPos
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.LevelChunkSection
import net.minecraft.world.level.chunk.status.ChunkStatus
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftChunk
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.event.Listener
import java.util.*

object MinesCore: Listener {
	fun setArea(
		region: MineRegion,
		composition: MineComposition,
		exclusions: List<MineRegion> = emptyList()   // ← default empty = backward compatible, zero cost
	) {
		val plugin = plugin
		val world = region.world

		// 1. Single Block Optimization
		if (region.isSingleBlock()) {
			val loc = region.getLoc1()
			// ⚠️ NEW: respect exclusions even for single block
			if (exclusions.any { it.contains(loc.blockX, loc.blockY, loc.blockZ) }) return
			Bukkit.getServer().regionScheduler.execute(plugin, loc) {
				loc.block.setBlockData(composition.next(), false)
			}
			return
		}

		// 2. Setup NMS
		val nmsLevel = (world as CraftWorld).handle
		val lightEngine = nmsLevel.lightEngine

		// 3. Chunk Ranges
		val minCx = region.minX shr 4
		val maxCx = region.maxX shr 4
		val minCz = region.minZ shr 4
		val maxCz = region.maxZ shr 4

		for (cx in minCx..maxCx) {
			for (cz in minCz..maxCz) {

				val chunkLoc = Location(world, (cx shl 4).toDouble() + 8.0, 0.0, (cz shl 4).toDouble() + 8.0)

				Bukkit.getServer().regionScheduler.execute(plugin, chunkLoc) {
					val bukkitChunk = world.getChunkAt(cx, cz)
					val nmsChunk = (bukkitChunk as CraftChunk).getHandle(ChunkStatus.FULL)

					val mutablePos = BlockPos.MutableBlockPos()
					val touchedSections = BitSet(nmsChunk.sectionsCount)   // ⚠️ FIX: was BitSet(32) hardcoded
					var blockChanged = false

					// Calculate Global Bounds for this specific chunk
					val xStart = if ((cx shl 4) >= region.minX) (cx shl 4) else region.minX
					val xEnd = if ((cx shl 4) + 15 <= region.maxX) (cx shl 4) + 15 else region.maxX
					val zStart = if ((cz shl 4) >= region.minZ) (cz shl 4) else region.minZ
					val zEnd = if ((cz shl 4) + 15 <= region.maxZ) (cz shl 4) + 15 else region.maxZ

					// --- PRE-FILTER exclusions for THIS chunk (once, not per-block) ---
					val chunkMinX = cx shl 4; val chunkMaxX = chunkMinX + 15
					val chunkMinZ = cz shl 4; val chunkMaxZ = chunkMinZ + 15
					val relevantExclusions = exclusions.filter { ex ->
						ex.maxX >= chunkMinX && ex.minX <= chunkMaxX &&
								ex.maxZ >= chunkMinZ && ex.minZ <= chunkMaxZ
					}
					val hasExclusions = relevantExclusions.isNotEmpty()

					var currentSectionIndex = -1
					var currentSection: LevelChunkSection? = null

					// LOOP (Global Coords)
					for (x in xStart..xEnd) {
						for (z in zStart..zEnd) {
							for (y in region.minY..region.maxY) {

								// --- EXCLUSION CHECK (only if this chunk has overlapping exclusions) ---
								if (hasExclusions) {
									var excluded = false
									for (ex in relevantExclusions) {
										if (ex.contains(x, y, z)) { excluded = true; break }
									}
									if (excluded) continue
								}

								val sectionIndex = nmsChunk.getSectionIndex(y)
								if (sectionIndex != currentSectionIndex) {
									currentSection = nmsChunk.getSection(sectionIndex)
									currentSectionIndex = sectionIndex
								}
								val nmsState = composition.nextNMS()

								currentSection!!.setBlockState(x and 15, y and 15, z and 15, nmsState, false)
								touchedSections.set(sectionIndex)
								blockChanged = true
							}
						}
					}

					if (blockChanged) {
						// Lighting
						var i = touchedSections.nextSetBit(0)
						while (i >= 0) {
							val sectionY = nmsLevel.getSectionYFromSectionIndex(i) shl 4
							lightEngine.checkBlock(mutablePos.set((cx shl 4) + 8, sectionY + 8, (cz shl 4) + 8))
							i = touchedSections.nextSetBit(i + 1)
						}
						world.refreshChunk(bukkitChunk.x, bukkitChunk.z)
					}
				}
			}
		}
	}

	internal fun hookWorldEdit(sender: CommandSender): List<Location>? {
		val actor = BukkitAdapter.adapt(sender)
		val session = WorldEdit.getInstance().sessionManager.get(actor)

		val selectionWorld = session.selectionWorld ?: return null

		// 2. Get Region (Throws exception if incomplete)
		val region = try {
			session.getSelection(selectionWorld)
		} catch (e: Exception) {
			if (e is IncompleteRegionException) return null
			throw e
		}

		// 3. Convert Coords
		val world = Bukkit.getWorld(selectionWorld.name) ?: return null
		val min = region.minimumPoint
		val max = region.maximumPoint

		val loc1 = Location(world, min.x().toDouble(), min.y().toDouble(), min.z().toDouble())
		val loc2 = Location(world, max.x().toDouble(), max.y().toDouble(), max.z().toDouble())

		return listOf(loc1, loc2)
	}

	// --- VERSION WRAPPER (REQUIRED) ---
	// Keeps 1.20 and 1.21 happy
	private val setterHandle: (Any, Any, Any) -> Unit = run {
		val lookup = java.lang.invoke.MethodHandles.lookup()
		val chunk = ChunkAccess::class.java
		val pos = BlockPos::class.java
		val state = net.minecraft.world.level.block.state.BlockState::class.java
		try {
			val mh = lookup.findVirtual(chunk, "setBlockState", java.lang.invoke.MethodType.methodType(state, pos, state))
			return@run { c: Any, p: Any, s: Any -> mh.invoke(c, p, s); }
		} catch (_: NoSuchMethodException) {
			val mh = lookup.findVirtual(chunk, "setBlockState", java.lang.invoke.MethodType.methodType(state, pos, state, Boolean::class.javaPrimitiveType))
			return@run { c: Any, p: Any, s: Any -> mh.invoke(c, p, s, false); }
		}
	}

	private fun ChunkAccess.setBlockFast(
		pos: BlockPos,
		state: net.minecraft.world.level.block.state.BlockState
	) {
		setterHandle(this, pos, state)
	}
}
