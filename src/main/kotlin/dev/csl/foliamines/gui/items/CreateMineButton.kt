package dev.csl.foliamines.gui.items

import dev.csl.foliamines.gui.MinesListGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class CreateMineButton: AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        return ItemBuilder(Material.REDSTONE_BLOCK).setDisplayName("Create mine")
    }

    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
        if (clickType.isLeftClick) {
            player.closeInventory()
            MinesListGui.openMinesList(player)
        }
    }
}