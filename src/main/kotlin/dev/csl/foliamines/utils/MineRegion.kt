package dev.csl.foliamines.utils

import dev.csl.foliamines.FoliaMines
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import kotlin.math.max
import kotlin.math.min

class MineRegion(
	val world: World,
	x1: Int, y1: Int, z1: Int,
	x2: Int, y2: Int, z2: Int
) {
	var minX = min(x1, x2)
	var maxX = max(x1, x2)
	var minY = min(y1, y2)
	var maxY = max(y1, y2)
	var minZ = min(z1, z2)
	var maxZ = max(z1, z2)

	// Secondary constructor for Locations (Used by Commands/WorldEdit)
	constructor(p1: Location, p2: Location?) : this(
		p1.world,
		p1.blockX, p1.blockY, p1.blockZ,
		p2?.blockX ?: p1.blockX, p2?.blockY ?: p1.blockY, p2?.blockZ ?: p1.blockZ
	)

	init {
		// 2. Calculate Raw Values
		var rMinX = min(x1, x2); var rMaxX = max(x1, x2)
		var rMinY = min(y1, y2); var rMaxY = max(y1, y2)
		var rMinZ = min(z1, z2); var rMaxZ = max(z1, z2)

		// 3. SAFETY CLAMP (The "Soft" Fix)
		// Minecraft World Border is +/- 30,000,000 blocks.
		// If config has corrupted numbers (like 2 Billion), we clamp them.
		val border = 30_000_000
		val heightMin = -2032 // Safe generous buffer for custom worlds
		val heightMax = 2032

		if (rMinX < -border || rMaxX > border || rMinZ < -border || rMaxZ > border) {
			// Log warning so admin knows the mine is technically broken/moved
			FoliaMines.plugin.componentLogger.warn(
				"MineRegion coordinates out of bounds! Clamping to world border to prevent crash."
			)

			// Force values into safe range
			rMinX = rMinX.coerceIn(-border, border)
			rMaxX = rMaxX.coerceIn(-border, border)
			rMinZ = rMinZ.coerceIn(-border, border)
			rMaxZ = rMaxZ.coerceIn(-border, border)

			// Clamp Y just in case of extreme vertical corruption
			rMinY = rMinY.coerceIn(heightMin, heightMax)
			rMaxY = rMaxY.coerceIn(heightMin, heightMax)
		}

		// 4. Assign Final Values
		this.minX = rMinX
		this.maxX = rMaxX
		this.minY = rMinY
		this.maxY = rMaxY
		this.minZ = rMinZ
		this.maxZ = rMaxZ
	}

	fun saveToConfig(section: ConfigurationSection) {
		section.set("world", world.name)
		section.set("pos1", listOf(minX, minY, minZ))
		section.set("pos2", listOf(maxX, maxY, maxZ))
		// Cleanup old
		section.set("location", null); section.set("location2", null)
	}

	val volume: Long get() = ((maxX - minX + 1).toLong() * (maxY - minY + 1).toLong() * (maxZ - minZ + 1).toLong())

	fun contains(loc: Location): Boolean {
		if (loc.world?.name != world.name) return false
		return contains(loc.blockX, loc.blockY, loc.blockZ)
	}

	// Ensure minX/maxX/minY/maxY/minZ/maxZ are PRECOMPUTED fields, then:
	fun contains(x: Int, y: Int, z: Int): Boolean =
		x in minX..maxX && y in minY..maxY && z in minZ..maxZ

	// Converters for when we strictly need a Location (e.g. Teleporting)
	fun getCenter(): Location = Location(world, (minX + maxX) / 2.0 + 0.5, (minY + maxY) / 2.0, (minZ + maxZ) / 2.0 + 0.5)
	fun getLoc1(): Location = Location(world, minX.toDouble(), minY.toDouble(), minZ.toDouble())
	fun isSingleBlock(): Boolean = (minX == maxX && minY == maxY && minZ == maxZ)

	companion object {
		fun fromConfig(section: ConfigurationSection): MineRegion? {
			// New Format
			if (section.contains("pos1")) {
				val world = Bukkit.getWorld(section.getString("world") ?: "world") ?: return null
				val p1 = section.getIntegerList("pos1")
				val p2 = section.getIntegerList("pos2")
				if (p1.size < 3 || p2.size < 3) return null
				return MineRegion(world, p1[0], p1[1], p1[2], p2[0], p2[1], p2[2])
			}
			// Legacy Migration
			if (section.contains("location")) {
				val loc1 = section.getObject("location", Location::class.java) ?: return null
				val loc2 = section.getObject("location2", Location::class.java)
				val region = MineRegion(loc1, loc2)
				region.saveToConfig(section)
				return region
			}
			return null
		}
	}
}