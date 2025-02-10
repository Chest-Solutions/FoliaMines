package dev.csl.foliamines.gui.items

import dev.csl.foliamines.gui.CreateMineGui
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.AbstractItem

class MinesListButton: AbstractItem() {
    override fun getItemProvider(): ItemProvider {
        return ItemBuilder(Material.REDSTONE_BLOCK).setDisplayName("Create new mine")
    }

    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
        if (clickType.isLeftClick) {
            player.closeInventory()
            CreateMineGui.openCreateMinesGui(player)
        }
    }
}