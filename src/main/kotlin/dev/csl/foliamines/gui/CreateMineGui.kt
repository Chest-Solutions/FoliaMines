package dev.csl.foliamines.gui

import dev.csl.foliamines.gui.items.CreateMineButton

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender

import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.item.builder.ItemBuilder

class CreateMineGui {
    companion object {

        fun openCreateMinesGui(plr: CommandSender)  {
            if (plr is Player) {
                val window = Window.single()
                    .setViewer(plr)
                    .setTitle("Create a new mine.")
                    .setGui(minesCreateGui())
                    .build()
                window.open()
            }
        }

        private fun minesCreateGui(): Gui {
            return Gui.normal() // Creates the GuiBuilder for a normal GUI
                .setStructure(
                    "# # # # # # # # #",
                    "# . . . . . . . #",
                    "# . . . . . . ! #",
                    "# # # # # # # # #")
                .addIngredient('#', SimpleItem(ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("")))
                .addIngredient('!', CreateMineButton())
                .build()
        }
    }
}