package net.anmvc.foliamines

import dev.jorel.commandapi.CommandAPI
import net.anmvc.foliamines.commands.Commands
import net.anmvc.foliamines.mines.MinesCore
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

class FoliaMines: JavaPlugin() {

    override fun onLoad() {
        this.saveDefaultConfig()
        plugin = this
        minesConfig = this.config

        CommandAPI.onLoad(Commands.getCommandAPIConfig())
        Commands.register()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        MinesCore.registerMines()
        // Plugin startup logic
    }

    override fun onDisable() {
        CommandAPI.onDisable()
        this.saveDefaultConfig()
        // Plugin shutdown logic
    }

    companion object {
        lateinit var plugin: FoliaMines
        lateinit var minesConfig: FileConfiguration
    }

}
