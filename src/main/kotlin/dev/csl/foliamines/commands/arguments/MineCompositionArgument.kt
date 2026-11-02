package dev.csl.foliamines.commands.arguments

import dev.csl.foliamines.mines.composition.MineComposition
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.arguments.CustomArgument
import org.bukkit.Bukkit
import org.bukkit.Registry
import java.util.concurrent.CompletableFuture

// Inherit from CustomArgument.
// T = Final Object (MineComposition), B = Base Type (String)
class MineCompositionArgument(nodeName: String) : CustomArgument<MineComposition, String>(
	// 1. The Base Argument (Our wrapper that allows colons but stops at spaces)
	UnrestrictedArgument(nodeName),

	// 2. The Parsing Logic (Lambda)
	{ info ->
		val input = info.currentInput() // Already clean string
		val composition = MineComposition()

		val parts = input.split(",")
		for (rawPart in parts) {
			val part = rawPart.trim()
			if (part.isEmpty()) continue

			var percentage = 100.0
			var blockStr = part

			// Percent Logic
			if (part.contains("%")) {
				val segments = part.split("%")
				val f = segments[0].toDoubleOrNull()
				val l = segments.last().toDoubleOrNull()
				if (f != null) { percentage = f; blockStr = segments.drop(1).joinToString("%") }
				else if (l != null) { percentage = l; blockStr = segments.dropLast(1).joinToString("%") }
			}

			// Block Logic
			try {
				blockStr = blockStr.trim()
				val data = Bukkit.createBlockData(blockStr)
				composition.addBlock(data, percentage)
			} catch (_: Exception) {
				throw CommandAPI.failWithString("Invalid block: '$part'").exception
			}
		}

		// Return result
		composition
	}
) {
	// 3. The Suggestions Logic (Init Block)
	init {
		replaceSuggestions { info, builder ->
			return@replaceSuggestions CompletableFuture.supplyAsync {
				val current = info.currentArg
				val lastComma = current.lastIndexOf(',')
				val prefix = if (lastComma == -1) "" else current.take(lastComma + 1)
				val segment = if (lastComma == -1) current else current.substring(lastComma + 1)

				val pctIndex = segment.indexOf('%')
				val pctPrefix = if (pctIndex != -1) segment.take(pctIndex + 1) else ""
				val search = if (pctIndex != -1) segment.substring(pctIndex + 1).lowercase() else segment.lowercase()

				val fullPrefix = prefix + pctPrefix

				Registry.MATERIAL.forEach { mat ->
					if (mat.isBlock) {
						val k = mat.key
						val name = k.key
						val full = k.toString()
						if (name.startsWith(search) && name != search) builder.suggest(fullPrefix + full)
						if (full.startsWith(search) && name != search) builder.suggest(fullPrefix + full)
					}
				}
				return@supplyAsync builder.build()
			}
		}
	}
}