package dev.csl.foliamines.handlers

import dev.csl.foliamines.FoliaMines.Companion.plugin
import dev.csl.foliamines.gui.CreateMineGui
import dev.csl.foliamines.gui.items.currentlyRenaming
import dev.csl.foliamines.gui.items.nameLinker
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatRenameHandler : Listener {
	fun setup() {
		Bukkit.getPluginManager().registerEvents(this, plugin)
	}

	@EventHandler
	fun onAsyncChat(event: AsyncChatEvent) {
		if (event.player.uniqueId in currentlyRenaming) {
			event.isCancelled = true

			val message: String = PlainTextComponentSerializer.plainText().serialize(event.message())

			if (message != "cancel") {
				nameLinker[event.player.uniqueId] = message
			}

			currentlyRenaming.remove(event.player.uniqueId)
			event.player.scheduler.execute(plugin, { CreateMineGui.openCreateMinesGui(event.player, false) }, null, 0)
		}
	}
}