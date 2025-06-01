package dev.csl.foliamines

import dev.jorel.commandapi.CommandAPI
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.commands.Commands
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.PercentageMines

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.configuration.file.YamlConfiguration

import xyz.xenondevs.invui.InvUI

class FoliaMines: JavaPlugin() {
    lateinit var minesConfig: YamlConfiguration

    override fun onLoad() {
        plugin = this

        try {
            Class.forName("com.sk89q.worldedit.WorldEdit")
            worldEdiInstalled = true
        } catch (_: ClassNotFoundException) {
            this.componentLogger.info("WorldEdit not installed, not hooking into worldedit")
        }

        CommandAPI.onLoad(Commands.getCommandAPIConfig())
        Commands.register()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        this.minesConfig = Configurations.createMinesConfig()
        DelayMines.registerDelayMines()
        InstantMines.registerInstantMines()
        PercentageMines.registerPercentageMines()
        InvUI.getInstance().setPlugin(this)
    }

    override fun onDisable() {
        CommandAPI.onDisable()
        Configurations.saveMinesConfig(this.minesConfig)
    }

    companion object {
        lateinit var plugin: FoliaMines
        var worldEdiInstalled = false
    }

}
