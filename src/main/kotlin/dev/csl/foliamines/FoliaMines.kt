package dev.csl.foliamines

import dev.jorel.commandapi.CommandAPI
import dev.csl.foliamines.commands.Commands
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.PercentageMines
import org.bukkit.plugin.java.JavaPlugin
import xyz.xenondevs.invui.InvUI

class FoliaMines: JavaPlugin() {

    override fun onLoad() {
        this.saveDefaultConfig()
        plugin = this

        CommandAPI.onLoad(Commands.getCommandAPIConfig())
        Commands.register()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        DelayMines.registerDelayMines()
        InstantMines.registerInstantMines()
        PercentageMines.registerPercentageMines()
        InvUI.getInstance().setPlugin(this)
        //server.pluginManager.registerEvents(InstantMines, this)
        //server.pluginManager.registerEvents(PercentageMines, this)
    }

    override fun onDisable() {
        CommandAPI.onDisable()
        this.saveConfig()
        // Plugin shutdown logic
    }

    companion object {
        lateinit var plugin: FoliaMines

        fun isWorldeditInstalled(): Boolean {
            return plugin.server.pluginManager.getPlugin("WorldEdit") != null
        }
    }

}
