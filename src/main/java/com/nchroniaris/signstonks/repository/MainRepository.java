package com.nchroniaris.signstonks.repository;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a contract for a repository class for the main plugin's configuration file. Although the config file is available through {@link JavaPlugin#getConfig()} this abstracts the access methods for safety and convenience
 */
public interface MainRepository extends ConfigurationRepository {

}
