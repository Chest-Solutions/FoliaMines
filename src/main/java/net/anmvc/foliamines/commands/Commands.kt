package net.anmvc.foliamines.commands

import com.sk89q.worldedit.IncompleteRegionException
import com.sk89q.worldedit.LocalSession
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
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
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage


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

                if (location != null) {
                    MinesCore.createMine(name, location, location2, delay, blockData.material.asBlockType()!!)
                } else if (!FoliaMines.isWorldeditInstalled()) {
                    sender.sendMessage(miniMessage().deserialize("Set a location in the command."))
                } else {

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

                    if (region.boundingBox != null) {
                        MinesCore.createMine(
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
                            blockData.material.asBlockType()!!
                        )
                    } else {
                        sender.sendMessage(miniMessage().deserialize("Either: Set a location in the command, Or: Set a normal worldedit area"))
                    }
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