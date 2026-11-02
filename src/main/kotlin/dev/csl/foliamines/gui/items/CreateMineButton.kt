package dev.csl.foliamines.gui.items

import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.gui.MinesListGui
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.PercentageMines
import dev.csl.foliamines.utils.MineRegion

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent


class CreateMineButton {
    // Create mine

    /*override fun handleClick(clickType: ClickType, plr: Player, clickEvent: InventoryClickEvent) {
        if (clickType.isLeftClick) {
			val playerTypeLinker = typeLinker[plr.uniqueId]
	        val playerMineNameLinker = nameLinker[plr.uniqueId]
	        val playerSelectionLinker = isPlayerSelecting[plr.uniqueId]
			/*
			when (playerTypeLinker?.manager) {
				DelayMines -> DelayMines.createDelayMine(
					playerMineNameLinker!!,
					MineRegion(
						playerSelectionLinker.component1(),
						playerSelectionLinker.component2()
					)
				)
				InstantMines -> InstantMines.createInstantMine(
					playerMineNameLinker!!,
					MineRegion(
						playerSelectionLinker.component1(),
						playerSelectionLinker.component2()
					)
				)
				PercentageMines -> PercentageMines.createPercentageMine(
					playerMineNameLinker!!,
					MineRegion(
						playerSelectionLinker.component1(),
						playerSelectionLinker.component2()
					)
				)
			}

			 */

	        FoliaMines.plugin.componentLogger.info(typeLinker[plr.uniqueId]?.manager.toString())
	        FoliaMines.plugin.componentLogger.info(nameLinker[plr.uniqueId])
	        FoliaMines.plugin.componentLogger.info(isPlayerSelecting[plr.uniqueId].toString())
	        typeLinker.remove(plr.uniqueId)
	        nameLinker.remove(plr.uniqueId)
			isPlayerSelecting.remove(plr.uniqueId)
            MinesListGui.openMinesList(plr)
        }
    }

     */
}