package net.anmvc.foliamines.commands

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.LocalSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.entity.Player
import com.sk89q.worldedit.extension.platform.Actor
import com.sk89q.worldedit.regions.Region
import com.sk89q.worldedit.session.SessionManager
import com.sk89q.worldedit.util.formatting.text.TextComponent
import com.sk89q.worldedit.world.World
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
            .withArguments(StringArgument("name"))
            .withOptionalArguments(
                LocationArgument("location"),
                LocationArgument("location2").combineWith(
                    IntegerArgument("delay"),
                    BlockStateArgument("blockData")
                )
            )
            .withAliases("newmine")   // Command aliases
            .withPermission(CommandPermission.OP)           // Required permissions
            .executes(CommandExecutor { sender, args ->
                val name: String = args["name"] as String
                val location: Location? = args["location"] as Location?
                val location2: Location? = args["location2"] as Location?
                val blockData = args["blockData"] as BlockData
                val delay: Int = args["delay"] as Int

                if (sender !is Player) {

                }

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
                    return@CommandExecutor
                }

                region.boundingBox.pos1

                

                MinesCore.createMine(name, location!!, location2, delay, blockData.material.asBlockType()!!)
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