package com.nchroniaris.signstonks.configuration;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class StockRepository extends ConfigurationRepository {

    /**
     * Main constructor for the Stock repository
     *
     * @param plugin An instance of the main plugin
     */
    public StockRepository(@NotNull JavaPlugin plugin) {

        super(plugin, plugin.getDataFolder().toPath().resolve("stocks.yml"));

    }

}
