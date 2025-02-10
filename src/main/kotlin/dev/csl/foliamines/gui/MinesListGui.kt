package dev.csl.foliamines.gui

import dev.csl.foliamines.gui.items.MinesListButton
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.item.builder.ItemBuilder
import xyz.xenondevs.invui.item.impl.SimpleItem
import xyz.xenondevs.invui.window.Window

class MinesListGui {
    companion object {

        fun openMinesList(plr: CommandSender)  {
            if (plr is Player) {
                val window = Window.single()
                    .setViewer(plr)
                    .setTitle("Mines list.")
                    .setGui(minesListGui())
                    .build()
                window.open()
            }
        }

        private fun minesListGui(): Gui {
            return Gui.normal() // Creates the GuiBuilder for a normal GUI
                .setStructure(
                    "# # # # # # # # #",
                    "# ! . . . . . . #",
                    "# . . . . . . . #",
                    "# # # # # # # # #")
                .addIngredient('#', SimpleItem(ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)))
                .addIngredient('!', MinesListButton())
                .build()
        }
    }
}