package dev.csl.foliamines.commands.arguments

import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import org.apache.commons.lang.NullArgumentException

// Inherit from CustomArgument.
// We pass GreedyStringArgument as the base to allow all symbols.
class UnrestrictedArgument(nodeName: String) : CustomArgument<String, String>(
	GreedyStringArgument(nodeName),
	CustomArgumentInfoParser { info ->
		// info.currentInput() is the text starting from this argument
		val input = info.currentInput()

		// Read until whitespace (your logic)
		val token = input.takeWhile { !it.isWhitespace() }

		if (token.isEmpty()) {
			throw NullArgumentException("Argument cannot be empty")
		}

		token // This is the parsed String
	}
)