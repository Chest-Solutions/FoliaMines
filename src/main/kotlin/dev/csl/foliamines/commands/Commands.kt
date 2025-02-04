package dev.csl.foliamines.commands

import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandExecutor
import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.WorldEditHook
import dev.csl.foliamines.gui.CreateMineGui
import dev.csl.foliamines.mines.DelayMines
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage


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
                } else if (!FoliaMines.isWorldeditInstalled()) {
                    sender.sendMessage(miniMessage().deserialize("Set a location in the command."))
                    CreateMineGui.openMineGui(sender)
                } else {
                    WorldEditHook.hookWorldedit(sender, name, blockData, delay)
                }
            })
            .register()

    }

    fun getCommandAPIConfig(): CommandAPIBukkitConfig {
        val commandAPIConfig = CommandAPIBukkitConfig(FoliaMines.plugin)

        commandAPIConfig.silentLogs(true)
        commandAPIConfig.useLatestNMSVersion(false)
        commandAPIConfig.beLenientForMinorVersions(true)
        commandAPIConfig.missingExecutorImplementationMessage("Ask the staff to tell the devs to setup the Executor Implmentation for the command you ran, No the command you ran will not work")
        commandAPIConfig.shouldHookPaperReload(true)
        commandAPIConfig.skipReloadDatapacks(false)
        commandAPIConfig.usePluginNamespace()
        return commandAPIConfig
    }
}