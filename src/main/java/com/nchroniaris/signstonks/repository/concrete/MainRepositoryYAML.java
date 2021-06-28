package com.nchroniaris.signstonks.repository.concrete;

import com.nchroniaris.signstonks.repository.MainRepository;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * YAML-based repository for the main plugin's configuration file
 */
public class MainRepositoryYAML extends ConfigurationRepositoryYAML implements MainRepository {

    /**
     * Main constructor for the main plugin's config repository
     *
     * @param plugin An instance of the main plugin
     */
    public MainRepositoryYAML(@NotNull JavaPlugin plugin) {

        super(
                plugin,
                Objects.requireNonNull(plugin, "plugin cannot be null!")
                        .getDataFolder().toPath().resolve("config.yml")
        );

    }

}
