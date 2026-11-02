package dev.csl.foliamines.gui.items

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class RenameMineButton(
	val mineName: String
) {
	// Rename $mineName

	/*override fun handleClick(clickType: ClickType, plr: Player, clickEvent: InventoryClickEvent) {
		if (clickType.isLeftClick) {
			currentInventory[plr.uniqueId] = null
			currentlyRenaming.add(plr.uniqueId)

			plr.closeInventory()

			plr.sendMessage("Input a mine name in chat.")
		}
	}

	 */
}