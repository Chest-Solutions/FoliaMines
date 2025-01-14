package net.anmvc.foliamines.commands

import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandExecutor
import net.anmvc.foliamines.FoliaMines
import net.anmvc.foliamines.mines.MinesCore
import org.bukkit.Location
import org.bukkit.block.data.BlockData


object Commands {
    fun register() {
        // Create our command
        CommandAPICommand("createmine")
            .withArguments(StringArgument("name"), LocationArgument("location"), IntegerArgument("delay"), BlockStateArgument("blockData"))
            .withOptionalArguments(LocationArgument("location2"))
            .withAliases("newmine")   // Command aliases
            .withPermission(CommandPermission.OP)           // Required permissions
            .executes(CommandExecutor { sender, args ->
                val name: String = args["name"] as String
                val location: Location = args["location"] as Location
                val location2: Location? = args["location2"] as Location?
                val blockData = args["blockData"] as BlockData
                val delay: Int = args["delay"] as Int

                if (location2 != null) {
                    MinesCore.createMine(name, location, location2, delay, blockData.material.asBlockType()!!)
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