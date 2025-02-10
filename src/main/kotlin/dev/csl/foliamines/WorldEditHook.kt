package dev.csl.foliamines

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.LocalSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extension.platform.Actor
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.SessionManager
import com.sk89q.worldedit.util.formatting.text.TextComponent
import com.sk89q.worldedit.world.World
import dev.csl.foliamines.gui.MinesListGui
import dev.csl.foliamines.mines.DelayMines
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.command.CommandSender

object WorldEditHook {
    fun hookWorldedit(sender: CommandSender, name: String, blockData: BlockData, delay: Int) {
        try {
            val actor: Actor = BukkitAdapter.adapt(sender) // WorldEdit's native Player class extends Actor
            val manager: SessionManager = WorldEdit.getInstance().sessionManager
            val localSession: LocalSession = manager.get(actor)
            val selectionWorld: World? = localSession.selectionWorld

            val region: Region

            try {
                if (selectionWorld == null) throw IncompleteRegionException()
                region = localSession.getSelection(selectionWorld)
            } catch (ex: IncompleteRegionException) {
                actor.printError(TextComponent.of("Error. $ex"))
                return
            }

            if (region.boundingBox != null) {
                DelayMines.createDelayMine(
                    name,
                    Location(
                        Bukkit.getWorld(selectionWorld.name),
                        region.boundingBox.pos1.x().toDouble(),
                        region.boundingBox.pos1.y().toDouble(),
                        region.boundingBox.pos1.z().toDouble()
                    ),
                    Location(
                        Bukkit.getWorld(selectionWorld.name),
                        region.boundingBox.pos2.x().toDouble(),
                        region.boundingBox.pos2.y().toDouble(),
                        region.boundingBox.pos2.z().toDouble()
                    ),
                    delay,
                    blockData
                )
            } else {
                sender.sendMessage(miniMessage().deserialize("Either: Set a location in the command, Or: Set a normal worldedit area"))
                MinesListGui.openMinesList(sender)
            }
        } catch (_: NoClassDefFoundError) {} catch (_: ClassNotFoundException) {}
    }
}