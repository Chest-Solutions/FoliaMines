package dev.csl.foliamines.gui

import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.util.Slot
import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.gui.items.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


class CreateMineGui {
    companion object : MineGui {
        internal fun openCreateMinesGui(plr: Player, setName: Boolean)  {
	       // val selected = GuiButton.entries[0]

	        if (setName) {
		       // typeLinker[plr.uniqueId] = selected
		       // FoliaMines.plugin.componentLogger.info("Player ${plr.name} selected ${selected.displayName}")

		        nameLinker[plr.uniqueId] = "Mine 1"
		        FoliaMines.plugin.componentLogger.info("Player ${plr.name} set their mine to name ${nameLinker[plr.uniqueId]}")
	        }

	        currentInventory[plr.uniqueId] = this

	        val gui = ChestGui(4, "Create ${nameLinker[plr.uniqueId]}")
	        gui.setOnClose {
		        if (currentInventory[plr.uniqueId] == this) {
			        MinesListGui.openMinesList(plr)
		        }
			}
	        gui.setOnGlobalClick { clickEvent -> clickEvent.isCancelled = true; FoliaMines.plugin.componentLogger.info(clickEvent.slot.toString()) }
	        val pane = StaticPane(9, 4)
	        val plainGlassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
	        val plainGlassPaneItemMeta = plainGlassPane.itemMeta
	        plainGlassPaneItemMeta.displayName(Component.text(""))
	        plainGlassPane.itemMeta = plainGlassPaneItemMeta
	        pane.addItem(GuiItem(plainGlassPane), 0)
	        pane.addItem(GuiItem(plainGlassPane), 1)
	        pane.addItem(GuiItem(plainGlassPane), 2)
	        pane.addItem(GuiItem(plainGlassPane), 3)
	        pane.addItem(GuiItem(plainGlassPane), 4)
	        pane.addItem(GuiItem(plainGlassPane), 5)
	        pane.addItem(GuiItem(plainGlassPane), 6)
	        pane.addItem(GuiItem(plainGlassPane), 7)
	        pane.addItem(GuiItem(plainGlassPane), 8)
	        pane.addItem(GuiItem(plainGlassPane), 9)
	        pane.addItem(GuiItem(plainGlassPane), 17)
	        pane.addItem(GuiItem(plainGlassPane), 18)
	        pane.addItem(GuiItem(plainGlassPane), 26)
	        pane.addItem(GuiItem(plainGlassPane), 27)
	        pane.addItem(GuiItem(plainGlassPane), 28)
	        pane.addItem(GuiItem(plainGlassPane), 29)
	        pane.addItem(GuiItem(plainGlassPane), 30)
	        pane.addItem(GuiItem(plainGlassPane), 31)
	        pane.addItem(GuiItem(plainGlassPane), 32)
	        pane.addItem(GuiItem(plainGlassPane), 33)
	        pane.addItem(GuiItem(plainGlassPane), 34)
	        pane.addItem(GuiItem(plainGlassPane), 35)
	        gui.addPane(Slot.fromXY(0, 0), pane)
	        gui.show(plr)
        }
    }
}

internal fun StaticPane.addItem(item: com.github.stefvanschie.inventoryframework.gui.GuiItem, slot: Int) {
	this.addItem(item, Slot.fromIndex(slot))
}

internal fun GuiItem(item: ItemStack): com.github.stefvanschie.inventoryframework.gui.GuiItem {
	return com.github.stefvanschie.inventoryframework.gui.GuiItem(item, FoliaMines.plugin)
}

internal fun ChestGui(rows: Int, title: String): com.github.stefvanschie.inventoryframework.gui.type.ChestGui {
	return com.github.stefvanschie.inventoryframework.gui.type.ChestGui(rows, title, FoliaMines.plugin)
}