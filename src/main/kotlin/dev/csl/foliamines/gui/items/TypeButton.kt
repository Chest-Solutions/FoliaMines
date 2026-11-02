package dev.csl.foliamines.gui.items

import dev.csl.foliamines.FoliaMines
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.PercentageMines
import dev.csl.foliamines.mines.composition.MineComposition
import dev.csl.foliamines.utils.MineRegion
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.UUID

// --- DATA INTERFACES ---

interface Mine {
	val name: String
	val type: MineType
	val world: World
	val region: MineRegion // UPDATED: Uses your smart region class
	var teleportLocation: Location
	var icon: Material
	val composition: MineComposition

	fun reset()
	fun contains(loc: Location): Boolean
}

interface MineManager {
	fun register()
	fun delete(name: String): Boolean
}

abstract class BaseMine(
	override val name: String,
	override val region: MineRegion, // UPDATED: Pass region directly
	override val composition: MineComposition,
	override var icon: Material = Material.STONE
) : Mine {

	// Region knows the world
	override val world: World = region.world

	// Calculate center based on region
	override var teleportLocation: Location = region.getCenter().apply {
		y = region.maxY + 1.0
	}

	override fun contains(loc: Location): Boolean = region.contains(loc)
}

enum class MineType {
	DELAY,
	PERCENTAGE,
	INSTANT
}

// --- GUI INTERFACES ---

interface MineGui

/*
sealed interface MineGuiTypeButton {
	val provider: ItemProvider
	val displayName: String
	val manager: MineManager
}

enum class GuiButton(
	override val provider: ItemProvider,
	override val displayName: String,
	override val manager: MineManager
) : MineGuiTypeButton {
	INSTANT(
		ItemBuilder(Material.REDSTONE_BLOCK).setDisplayName(MiniMessage.miniMessage().deserialize("<red>Type: Instant")),
		"Instant",
		InstantMines
	),
	DELAY(
		ItemBuilder(Material.GOLD_BLOCK).setDisplayName(MiniMessage.miniMessage().deserialize("<gold>Type: Delay")),
		"Delay",
		DelayMines
	),
	PERCENTAGE(
		ItemBuilder(Material.DIAMOND_BLOCK).setDisplayName(MiniMessage.miniMessage().deserialize("<blue>Type: Percentage")),
		"Percentage",
		PercentageMines
	);
}


// --- GUI STATE & ITEMS ---

internal val typeLinker = mutableMapOf<UUID, MineGuiTypeButton>() // UUID key non-nullable
 */
internal val nameLinker = mutableMapOf<UUID, String>()
internal val isPlayerSelecting = mutableMapOf<UUID, Pair<Location, Location?>?>()
internal val currentInventory = mutableMapOf<UUID, MineGui?>()
internal val currentlyRenaming = mutableSetOf<UUID>()

/*class TypeButton : CycleItem(*GuiButton.entries.map { it.provider }.toTypedArray()) {
	override fun handleStateChange(player: Player?, state: Int) {
		if (player == null) return
		val selected = GuiButton.entries[state]
		typeLinker[player.uniqueId] = selected
		FoliaMines.plugin.componentLogger.info("Player ${player.name} selected ${selected.displayName}")
	}
}

 */