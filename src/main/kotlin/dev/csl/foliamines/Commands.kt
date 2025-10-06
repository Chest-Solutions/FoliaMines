package dev.csl.foliamines

import dev.csl.foliamines.gui.MinesListGui
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.MinesCore
import dev.csl.foliamines.mines.PercentageMines
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.*
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.block.BlockState
import org.bukkit.command.CommandSender

object Commands {
    fun register() {
	    CommandTree("createmine")
		    .withAliases("newmine")
		    .withPermission(CommandPermission.OP)
		    .thenNested(
				StringArgument("name"),
				LiteralArgument("delay"),
			    IntegerArgument("delayTime"),
			    BlockStateArgument("block"),
			    LocationArgument("location").setOptional(true),
			    LocationArgument("location2").setOptional(true)
					.executes(CommandExecutor { sender, args -> createMineCode(sender, args, "delay") })
			)
		    .thenNested(
				StringArgument("name"),
				LiteralArgument("instant"),
			    BlockStateArgument("block"),
			    LocationArgument("location").setOptional(true),
			    LocationArgument("location2").setOptional(true)
					.executes(CommandExecutor { sender, args -> createMineCode(sender, args, "instant") })
			)
		    .thenNested(
				StringArgument("name"),
				LiteralArgument("percentage"),
			    IntegerArgument("percentageAmount"),
				BlockStateArgument("block"),
				LocationArgument("location").setOptional(true),
				LocationArgument("location2").setOptional(true)
					.executes(CommandExecutor { sender, args -> createMineCode(sender, args, "percentage") })
			)
		    .register(FoliaMines.plugin)

	    CommandTree("deletemine")
		    .withAliases("removemine")
		    .withPermission(CommandPermission.OP)
		    .thenNested(
			    StringArgument("name"),
			    LiteralArgument("delay")
				    .executes(CommandExecutor { sender, args -> deleteMineCode(sender, args, "delay") })
		    )
		    .thenNested(
			    StringArgument("name"),
			    LiteralArgument("instant")
				    .executes(CommandExecutor { sender, args -> deleteMineCode(sender, args, "instant") })
		    )
		    .thenNested(
			    StringArgument("name"),
			    LiteralArgument("percentage")
				    .executes(CommandExecutor { sender, args -> deleteMineCode(sender, args, "percentage") })
		    )
		    .register(FoliaMines.plugin)

	    CommandAPICommand("openPreAlphaGUI")
		    .executes(CommandExecutor { sender, _ ->
			    MinesListGui.openMinesList(sender)
		    })
		    .register(FoliaMines.plugin)
    }

	fun deleteMineCode (sender: CommandSender, args: CommandArguments, type: String) {
		when (type) {
			"delay" -> DelayMines.deleteDelayMine(args["name"].toString())
			"instant" -> InstantMines.deleteInstantMine(args["name"].toString()) ?: run {sender.sendMessage(miniMessage().deserialize("The mine <name> does not exist.", Placeholder.unparsed("name", args["name"].toString())));return}
			"percentage" -> PercentageMines.deletePercentageMine(args["name"].toString()) ?: run {sender.sendMessage(miniMessage().deserialize("The mine <name> does not exist.", Placeholder.unparsed("name", args["name"].toString())));return}
		}
		sender.sendMessage(miniMessage().deserialize("Deleted the mine <name>.", Placeholder.unparsed("name", args["name"].toString())))
	}

	fun createMineCode(sender: CommandSender, args: CommandArguments, type: String) {
		val location: Location? = args["location"] as Location?
		val location2: Location? = args["location2"] as Location?
		val blockState: BlockState = args["block"] as BlockState
		val name: String = args["name"] as String
		val delay: Int? = args["delayTime"] as Int?
		val percentageAmount: Int? = args["percentageAmount"] as Int?

		if (location != null) {
			when (type) {
				"delay" -> DelayMines.createDelayMine(name, location, location2, delay!!, blockState)
				"instant" -> InstantMines.createInstantMine(name, location, location2, blockState)
				"percentage" -> {
					if (location2 == null) {sender.sendMessage(miniMessage().deserialize("Set both locations.")); return}
					PercentageMines.createPercentageMine(name, location, location2, percentageAmount!!, blockState)
				}
			}
			sender.sendMessage(miniMessage().deserialize("Created the mine <name>.", Placeholder.unparsed("name", args["name"].toString())))
		} else if (!FoliaMines.worldEdiInstalled) {
			sender.sendMessage(miniMessage().deserialize("Set a location in the command."))
		} else {
			when (val worldEditResponse = MinesCore.hookWorldEdit(sender)) {
				null ->
					sender.sendMessage(miniMessage().deserialize("Set a location in the command, Or select a worldedit area"))
				else -> {
					val (loc, loc2) = worldEditResponse
					when (type) {
						"delay" -> DelayMines.createDelayMine(name, loc, loc2, delay!!, blockState)
						"instant" -> InstantMines.createInstantMine(name, loc, loc2, blockState)
						"percentage" -> PercentageMines.createPercentageMine(name, loc, loc2, percentageAmount!!, blockState)
					}
				}
			}

		}
	}
}