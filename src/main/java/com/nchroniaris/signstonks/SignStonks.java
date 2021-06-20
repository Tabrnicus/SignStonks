package com.nchroniaris.signstonks;

import com.nchroniaris.signstonks.command.RegisterCommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public final class SignStonks extends JavaPlugin {

    private final Path pathDataFolder;

    public SignStonks() {

        this.pathDataFolder = this.getDataFolder().toPath();

    }

    @Override
    public void onEnable() {

        super.getLogger().info("Initializing...");

        // Makes sure the data folder exists
        this.validatePluginFolder();

        this.registerCommands();
        this.registerListeners();

//        super.getServer().getPluginManager().registerEvents(new TestListener(), this);

    }

    /**
     * This makes sure the plugin's data folder exists and is a folder. If it does not exist then it creates it. However, if it's anything but a directory, the plugin will fail to load to begin with and this code will not be run.
     */
    private void validatePluginFolder() throws IllegalStateException {

        if (!Files.exists(this.pathDataFolder)) {

            try {

                Files.createDirectory(this.pathDataFolder);

            } catch (FileAlreadyExistsException ignored) {
            } catch (IOException e) {

                RuntimeException pluginException = new IllegalStateException("Could not create data folder!");
                pluginException.addSuppressed(e);

                throw pluginException;

            }

        }

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

    }


}
