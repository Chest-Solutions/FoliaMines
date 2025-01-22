package net.anmvc.foliamines

import dev.jorel.commandapi.CommandAPI
import net.anmvc.foliamines.commands.Commands
import net.anmvc.foliamines.mines.DelayMines
import org.bukkit.plugin.java.JavaPlugin

class FoliaMines: JavaPlugin() {

    override fun onLoad() {
        this.saveDefaultConfig()
        plugin = this

        CommandAPI.onLoad(Commands.getCommandAPIConfig())
        Commands.register()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        DelayMines.registerMines()
        // Plugin startup logic
    }

    override fun onDisable() {
        CommandAPI.onDisable()
        this.saveConfig()
        // Plugin shutdown logic
    }

    companion object {
        lateinit var plugin: FoliaMines

        fun isWorldeditInstalled(): Boolean {
            try {
                Class.forName("com.sk89q.worldedit.WorldEdit")
                return true
            } catch (e: ClassNotFoundException) {
                return false
            }
        }
    }

}
