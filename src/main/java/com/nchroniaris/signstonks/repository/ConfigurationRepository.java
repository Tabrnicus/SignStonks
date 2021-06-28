package com.nchroniaris.signstonks.repository;

/**
 * Represents a general contract for a repository class that interacts with a {@link org.bukkit.configuration.file.FileConfiguration}
 */
public interface ConfigurationRepository {

    /**
     * Reloads the configuration, overwriting any values that are currently unsaved
     */
    void reload();

    /**
     * Finds and loads the default configuration, saving it ONLY if a saved version does not exist
     */
    void saveDefaults();

    /**
     * Saves the current configuration, including any changes. A subsequent {@link #reload()} should include any changes made after this method
     */
    void save();

}
