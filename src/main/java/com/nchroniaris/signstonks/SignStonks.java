package com.nchroniaris.signstonks;

import com.nchroniaris.signstonks.command.RegisterCommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public final class SignStonks extends JavaPlugin {

    private final Path pathDataFolder;

    public SignStonks() {

        this.pathDataFolder = this.getDataFolder().toPath();

    }

    @Override
    public void onEnable() {

        this.getLogger().info("Initializing...");

        // Saves the default config if does not exist, failing silently if it does
        // Also ensures that the plugin's data folder exists
        this.saveDefaultConfig();

        this.registerCommands();
        this.registerListeners();

    }

    /**
     * Registers all the commands with the server
     */
    private void registerCommands() {

        PluginCommand registerCommand = this.getCommand("registerstock");

        if (registerCommand != null)
            registerCommand.setExecutor(new RegisterCommandExecutor(this));

    }

    /**
     * Registers all the listeners with the server
     */
    private void registerListeners() {

        this.getServer().getPluginManager().registerEvents(new TestListener(), this);

    }


}
