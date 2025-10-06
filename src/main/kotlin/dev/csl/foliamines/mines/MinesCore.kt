package dev.csl.foliamines.mines

import dev.csl.foliamines.FoliaMines.Companion.plugin

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.BlockState
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import java.lang.reflect.InvocationTargetException

import kotlin.math.max
import kotlin.math.min

object MinesCore: Listener {
	private val weClass by lazy { Class.forName("com.sk89q.worldedit.WorldEdit") }
	private val weInstanceMethod by lazy { weClass.getDeclaredMethod("getInstance") }
	private val sessionMgrMethod by lazy { weClass.getDeclaredMethod("getSessionManager") }

	private val bukkitAdapterClass by lazy { Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter") }
	private val adaptMethod by lazy { bukkitAdapterClass.getDeclaredMethod("adapt", CommandSender::class.java) }

	private val sessionOwnerClass by lazy { Class.forName("com.sk89q.worldedit.session.SessionOwner") }

	private val incompleteExClass by lazy { Class.forName("com.sk89q.worldedit.IncompleteRegionException") }

	private val cuboidRegionClass by lazy { Class.forName("com.sk89q.worldedit.regions.CuboidRegion") }
	private val localSessionClass by lazy { Class.forName("com.sk89q.worldedit.LocalSession") }
	private val getSelectionMethod by lazy { localSessionClass.getDeclaredMethod("getSelection", Class.forName("com.sk89q.worldedit.world.World")) }
	private val boundingBoxMethod by lazy { cuboidRegionClass.getDeclaredMethod("getBoundingBox") }

	private val sessionMgrClass by lazy { Class.forName("com.sk89q.worldedit.session.SessionManager") }
	private val getMethod by lazy { sessionMgrClass.getDeclaredMethod("get", sessionOwnerClass) }

	private val getSelectionWorldMethod by lazy { localSessionClass.getDeclaredMethod("getSelectionWorld") }

	private val boundingBoxClass by lazy { Class.forName("com.sk89q.worldedit.math.BlockVector3") }
	private val pos1Method by lazy { cuboidRegionClass.getDeclaredMethod("getPos1") }
	private val pos2Method by lazy { cuboidRegionClass.getDeclaredMethod("getPos2") }

	private val xMethod by lazy { boundingBoxClass.getDeclaredMethod("getX") }
	private val yMethod by lazy { boundingBoxClass.getDeclaredMethod("getY") }
	private val zMethod by lazy { boundingBoxClass.getDeclaredMethod("getZ") }

    private fun setBlock(loc: Location, blockState: BlockState) {
        Bukkit.getServer().regionScheduler.run(plugin, loc) {
            val chunk = loc.chunk

            if (chunk.isLoaded) {
                loc.block.blockData = blockState.blockData
            } else {
                loc.chunk.addPluginChunkTicket(plugin)
                loc.block.blockData = blockState.blockData
                loc.chunk.removePluginChunkTicket(plugin)
            }
        }
    }

    internal fun setArea(loc: Location, loc2: Location?, blockState: BlockState) {
        if (loc2 == null) {
            setBlock(loc, blockState)
            return
        }

        for (location: Location in getArea(loc, loc2)) { setBlock(location, blockState) }
    }

    internal fun getArea(loc: Location, loc2: Location): ArrayList<Location> {
        val locList: ArrayList<Location> = ArrayList()

        val minX = min(loc.blockX.toDouble(), loc2.blockX.toDouble()).toInt()
        val minY = min(loc.blockY.toDouble(), loc2.blockY.toDouble()).toInt()
        val minZ = min(loc.blockZ.toDouble(), loc2.blockZ.toDouble()).toInt()
        val maxX = max(loc.blockX.toDouble(), loc2.blockX.toDouble()).toInt()
        val maxY = max(loc.blockY.toDouble(), loc2.blockY.toDouble()).toInt()
        val maxZ = max(loc.blockZ.toDouble(), loc2.blockZ.toDouble()).toInt()

        // Iterate through the volume defined by the two locations
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val blockLoc = Location(loc.world, x.toDouble(), y.toDouble(), z.toDouble())
                    locList.add(blockLoc)
                }
            }
        }
        return locList
    }

	internal fun hookWorldEdit(sender: CommandSender): List<Location>? {
		val weInstance = weInstanceMethod.invoke(null)
		val sessionMgr = sessionMgrMethod.invoke(weInstance)
		val adapted = adaptMethod.invoke(null, sender)
		val actor = getMethod.invoke(sessionMgr, adapted)

		val selectionWorld = getSelectionWorldMethod.invoke(actor)

		val region = try {
			getSelectionMethod.invoke(actor, selectionWorld)
		} catch (e: InvocationTargetException) {
			if (incompleteExClass.isInstance(e.cause)) return null
			throw e
		}

		val bbox = boundingBoxMethod.invoke(region)
		val p1 = pos1Method.invoke(bbox)
		val p2 = pos2Method.invoke(bbox)

		fun coord(vec: Any, meth: java.lang.reflect.Method) = meth.invoke(vec) as Int

		val worldName = selectionWorld.javaClass
			.getMethod("getName")
			.invoke(selectionWorld) as String
		val bukkitWorld = Bukkit.getWorld(worldName) ?: return null

		val loc1 = Location(
			bukkitWorld,
			coord(p1, xMethod).toDouble(),
			coord(p1, yMethod).toDouble(),
			coord(p1, zMethod).toDouble()
		)
		val loc2 = Location(
			bukkitWorld,
			coord(p2, xMethod).toDouble(),
			coord(p2, yMethod).toDouble(),
			coord(p2, zMethod).toDouble()
		)
		return listOf(loc1, loc2)
	}
}
