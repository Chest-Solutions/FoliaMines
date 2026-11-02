package dev.csl.foliamines.gui

import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.util.Slot
import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.gui.CreateMineGui.Companion.openCreateMinesGui
import dev.csl.foliamines.gui.items.MineGui
import dev.csl.foliamines.gui.items.currentInventory
import net.kyori.adventure.text.Component

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

class MinesListGui {
    companion object : MineGui {
        internal fun openMinesList(plr: CommandSender)  {
            if (plr is Player) {
                currentInventory[plr.uniqueId] = this

                val gui = ChestGui(4, "Mines list.")
                gui.setOnGlobalClick { clickEvent -> clickEvent.isCancelled = true; FoliaMines.plugin.componentLogger.info(clickEvent.slot.toString()) }
                val pane = StaticPane(9, 4)
                val plainGlassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
                val plainGlassPaneItemMeta = plainGlassPane.itemMeta
                plainGlassPaneItemMeta.displayName(Component.text(""))
                plainGlassPane.itemMeta = plainGlassPaneItemMeta
                
                val plainGlassPaneGuiItem = GuiItem(plainGlassPane)


                val startMineCreationProcessButton = ItemStack(Material.REDSTONE_BLOCK)
                val startMineCreationProcessItemMeta = startMineCreationProcessButton.itemMeta
                startMineCreationProcessItemMeta.displayName(Component.text("Create new mine"))
                startMineCreationProcessButton.itemMeta = startMineCreationProcessItemMeta
                
                val startMineCreationProcessGuiItem = GuiItem(startMineCreationProcessButton)
                startMineCreationProcessGuiItem.setAction { _ -> openCreateMinesGui(plr, true) }

                pane.addItem(plainGlassPaneGuiItem, 0)
                pane.addItem(plainGlassPaneGuiItem, 1)
                pane.addItem(plainGlassPaneGuiItem, 2)
                pane.addItem(plainGlassPaneGuiItem, 3)
                pane.addItem(plainGlassPaneGuiItem, 4)
                pane.addItem(plainGlassPaneGuiItem, 5)
                pane.addItem(plainGlassPaneGuiItem, 6)
                pane.addItem(plainGlassPaneGuiItem, 7)
                pane.addItem(plainGlassPaneGuiItem, 8)
                pane.addItem(plainGlassPaneGuiItem, 9)
                pane.addItem(startMineCreationProcessGuiItem, 10)
                pane.addItem(plainGlassPaneGuiItem, 17)
                pane.addItem(plainGlassPaneGuiItem, 18)
                pane.addItem(plainGlassPaneGuiItem, 26)
                pane.addItem(plainGlassPaneGuiItem, 27)
                pane.addItem(plainGlassPaneGuiItem, 28)
                pane.addItem(plainGlassPaneGuiItem, 29)
                pane.addItem(plainGlassPaneGuiItem, 30)
                pane.addItem(plainGlassPaneGuiItem, 31)
                pane.addItem(plainGlassPaneGuiItem, 32)
                pane.addItem(plainGlassPaneGuiItem, 33)
                pane.addItem(plainGlassPaneGuiItem, 34)
                pane.addItem(plainGlassPaneGuiItem, 35)
                gui.addPane(Slot.fromXY(0, 0), pane)
                gui.show(plr)
            }
        }
    }
}