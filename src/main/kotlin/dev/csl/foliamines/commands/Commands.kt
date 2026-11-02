package dev.csl.foliamines.commands

import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.commands.arguments.MineCompositionArgument
import dev.csl.foliamines.gui.MinesListGui
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.MinesCore
import dev.csl.foliamines.mines.PercentageMines
import dev.csl.foliamines.mines.composition.MineComposition
import dev.csl.foliamines.utils.MineRegion
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.DoubleArgument
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.LocationArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.CommandExecutor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Location
import org.bukkit.command.CommandSender

object Commands {
	fun register() {
		CommandTree("createmine")
			.withAliases("newmine")
			.withPermission(CommandPermission.fromString("foliamines.create"))

			// DELAY
			.thenNested(
				StringArgument("name"),
				LiteralArgument("delay"),
				IntegerArgument("delayTime"),
				MineCompositionArgument("block"),
				LocationArgument("location").setOptional(true),
				LocationArgument("location2").setOptional(true)
					.executes(CommandExecutor { sender, args -> createMineCode(sender, args, "delay") })
			)
			// INSTANT
			.thenNested(
				StringArgument("name"),
				LiteralArgument("instant"),
				MineCompositionArgument("block"),
				LocationArgument("location").setOptional(true),
				LocationArgument("location2").setOptional(true)
					.executes(CommandExecutor { sender, args -> createMineCode(sender, args, "instant") })
			)
			// PERCENTAGE
			.thenNested(
				StringArgument("name"),
				LiteralArgument("percentage"),
				DoubleArgument("percentageAmount"),
				MineCompositionArgument("block"),
				LocationArgument("location").setOptional(true),
				LocationArgument("location2").setOptional(true)
					.executes(CommandExecutor { sender, args -> createMineCode(sender, args, "percentage") })
			)
			.register(FoliaMines.plugin)

		CommandAPICommand("deletemine")
			.withAliases("removemine")
			.withPermission(CommandPermission.fromString("foliamines.delete"))
			.withArguments(StringArgument("name"))
			.executes(CommandExecutor { sender, args ->
				val name = args["name"].toString()

				// Helper to find which manager owns this mine
				// Checks each manager to see if it can delete it
				val deleted = DelayMines.delete(name) || InstantMines.delete(name) || PercentageMines.delete(name)

				if (deleted) {
					sender.sendMessage(
						MiniMessage.miniMessage()
							.deserialize("Deleted the mine <name>.", Placeholder.unparsed("name", name))
					)
				} else {
					sender.sendMessage(
						MiniMessage.miniMessage().deserialize(
							"The mine <name> does not exist.",
							Placeholder.unparsed("name", name)
						)
					)
				}
			})
			.register(FoliaMines.plugin)

		CommandAPICommand("openPreAlphaGUI")
			.withPermission(CommandPermission.fromString("foliamines.gui"))
			.executes(CommandExecutor { sender, _ ->
				MinesListGui.openMinesList(sender)
			})
			.register(FoliaMines.plugin)
	}

	private fun createMineCode(sender: CommandSender, args: CommandArguments, type: String) {
		val name = args["name"] as String
		val composition = args["block"] as MineComposition

		val locArg1 = args["location"] as Location?
		val locArg2 = args["location2"] as Location?

		val maxCoord = 30_000_00

		fun Location.isValid(): Boolean =
			x >= -maxCoord && x <= maxCoord &&
			y >= -maxCoord && y <= maxCoord &&
			z >= -maxCoord && z <= maxCoord

		// 1. DETERMINE REGION
		val region: MineRegion? = try {
			if (locArg1 != null) {
				// Use Command Args
				if (!locArg1.isValid()) throw IllegalArgumentException("Location invalid.")
				if (locArg2 != null && !locArg2.isValid()) throw IllegalArgumentException("Location invalid.")

				if (locArg1.world == null) throw IllegalArgumentException("Location world is null")
				MineRegion(locArg1, locArg2)
			} else {
				// Use WorldEdit
				if (!FoliaMines.worldEdiInstalled) {
					sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No location provided & WorldEdit missing."))
					null
				} else {
					val weResult = MinesCore.hookWorldEdit(sender)
					if (weResult == null || weResult.size < 2) {
						sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Select a region with WorldEdit or provide coordinates."))
						null
					} else {
						// Create region from WE result
						MineRegion(weResult[0], weResult[1])
					}
				}
			}
		} catch (e: Exception) {
			sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error creating region: ${e.message}"))
			null
		}

		if (region == null) return

		// 2. DELEGATE TO MANAGER
		when (type) {
			"delay" -> {
				val delay = args["delayTime"] as Int
				DelayMines.createDelayMine(name, region, delay, composition)
			}
			"instant" -> {
				InstantMines.createInstantMine(name, region, composition)
			}
			"percentage" -> {
				val percent = args["percentageAmount"] as Double
				PercentageMines.createPercentageMine(name, region, percent, composition)
			}
		}

		sender.sendMessage(
			MiniMessage.miniMessage().deserialize(
				"<green>Created $type mine <name>!",
				Placeholder.unparsed("name", name)
			)
		)
	}
}