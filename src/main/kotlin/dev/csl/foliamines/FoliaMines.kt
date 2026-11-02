package dev.csl.foliamines

import dev.csl.foliamines.commands.Commands
import dev.csl.foliamines.handlers.ChatRenameHandler
import dev.csl.foliamines.handlers.MultipleBlockBreakHandler
import dev.csl.foliamines.mines.DelayMines
import dev.csl.foliamines.mines.InstantMines
import dev.csl.foliamines.mines.PercentageMines
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIPaperConfig
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.Delegates


class FoliaMines: JavaPlugin() {
    lateinit var minesConfig: YamlConfiguration

    override fun onLoad() {
        plugin = this

        try {
            Class.forName("com.sk89q.worldedit.WorldEdit")
            worldEdiInstalled = true
        } catch (_: ClassNotFoundException) {
            this.componentLogger.info("WorldEdit not installed, not hooking into worldedit")
            worldEdiInstalled = false
        }

        CommandAPI.onLoad(
	        CommandAPIPaperConfig(this)
		        .silentLogs(true)
		        .fallbackToLatestNMS(true)
		        .missingExecutorImplementationMessage("An error occurred, Please don't try again")
		)
        Commands.register()
    }

    override fun onEnable() {
        CommandAPI.onEnable()
        this.minesConfig = Configurations.createMinesConfig()
        DelayMines.register()
        InstantMines.register()
        PercentageMines.register()
        ChatRenameHandler().setup()
        MultipleBlockBreakHandler().setup()
    }

    override fun onDisable() {
	    //CommandAPI.getRegisteredCommands().forEach { command -> CommandAPI.unregister(command.commandName, true) } // reloads
        CommandAPI.onDisable()
        Configurations.saveMinesConfig(this.minesConfig)
    }

    companion object {
        lateinit var plugin: FoliaMines
        var worldEdiInstalled by Delegates.notNull<Boolean>()
    }
}
