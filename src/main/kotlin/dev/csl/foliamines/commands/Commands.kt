package dev.csl.foliamines.commands

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter

import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.executors.CommandExecutor

import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.gui.MinesListGui
import dev.csl.foliamines.mines.DelayMines

import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.command.CommandSender


object Commands {
    fun register() {
        // Create our command
        CommandAPICommand("createmine")
            .withArguments(StringArgument("name"),IntegerArgument("delay"), BlockStateArgument("blockData"))
            .withOptionalArguments(LocationArgument("location"), LocationArgument("location2"))
            .withAliases("newmine")   // Command aliases
            .withPermission(CommandPermission.OP)           // Required permissions
            .executes(CommandExecutor { sender, args ->
                val name: String = args["name"] as String
                val location: Location? = args["location"] as Location?
                val location2: Location? = args["location2"] as Location?
                val blockData: BlockData = args["blockData"] as BlockData
                val delay: Int = args["delay"] as Int

                if (location != null) {
                    DelayMines.createDelayMine(name, location, location2, delay, blockData)
                } else if (!FoliaMines.worldEdiInstalled) {
                    sender.sendMessage(miniMessage().deserialize("Set a location in the command."))
                    MinesListGui.openMinesList(sender)
                } else {
                    val worldEditResponse = hookWorldEdit(sender)
                    when (worldEditResponse) {
                        null ->
                            sender.sendMessage(miniMessage().deserialize("Either: Set a location in the command, Or: Set a normal worldedit area"))
                        else -> {
                            val (loc, loc2) = worldEditResponse
                            DelayMines.createDelayMine(name, loc, loc2, delay, blockData)
                        }
                    }

                }
            })
            .register(FoliaMines.plugin)

        CommandAPICommand("deletemine")
            .withArguments(StringArgument("name"))
            .withPermission(CommandPermission.OP)
            .executes(CommandExecutor { sender, args ->
                DelayMines.deleteDelayMine(args["name"].toString())
                sender.sendMessage(miniMessage().deserialize("Deleted the mine %name%"))
            })
            .register(FoliaMines.plugin)

    }

    fun getCommandAPIConfig(): CommandAPIBukkitConfig {
        val commandAPIConfig = CommandAPIBukkitConfig(FoliaMines.plugin)

        commandAPIConfig.silentLogs(true)
        commandAPIConfig.useLatestNMSVersion(false)
        commandAPIConfig.beLenientForMinorVersions(true)
        commandAPIConfig.missingExecutorImplementationMessage("An error occurred, Please don't try again")
        commandAPIConfig.shouldHookPaperReload(false)
        commandAPIConfig.skipReloadDatapacks(true)
        return commandAPIConfig
    }

    private fun hookWorldEdit(sender: CommandSender): List<Location>? { 
        val actor = WorldEdit.getInstance().sessionManager.get(BukkitAdapter.adapt(sender))

        val selectionWorld = actor.selectionWorld
        if (selectionWorld == null) { return null }

        val boundingBox = actor.getSelection(selectionWorld).boundingBox

        return listOf(
            Location(
                Bukkit.getWorld(selectionWorld.name),
                boundingBox.pos1.x().toDouble(),
                boundingBox.pos1.y().toDouble(),
                boundingBox.pos1.z().toDouble()
            ),
            Location(
                Bukkit.getWorld(selectionWorld.name),
                boundingBox.pos2.x().toDouble(),
                boundingBox.pos2.y().toDouble(),
                boundingBox.pos2.z().toDouble()
            )
        )
    }
}