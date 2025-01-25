package net.anmvc.foliamines.gui

import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class CreateMineGui {
    companion object: InventoryHolder {
        private var inventory: Inventory? = null

        fun openMineGui(plr: CommandSender)  {
            if (inventory == null) {
                setupMinesGui()
            }
            if (plr is Player) {
                plr.openInventory(inventory!!)
            }
        }

        override fun getInventory(): Inventory {
            return inventory!!
        }

        private fun setupMinesGui() {
            inventory = Bukkit.createInventory(this, 6 * 9, miniMessage().deserialize(""))
            val idk1: ItemStack = ItemStack.of(Material.AMETHYST_BLOCK, 1)
            idk1.editMeta { meta: ItemMeta ->
                meta.displayName(miniMessage().deserialize("e"))
            }

            inventory!!.setItem(5, idk1)
        }
    }
}