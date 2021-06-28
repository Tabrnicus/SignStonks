package com.nchroniaris.signstonks.repository.concrete;

import com.google.common.base.Charsets;
import com.nchroniaris.signstonks.repository.ConfigurationRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Base class for any YAML-based repository implementation. It handles saving and loading the changes to the internal {@link FileConfiguration} to and from disk.
 */
public abstract class ConfigurationRepositoryYAML implements ConfigurationRepository {

    private final Reference<JavaPlugin> plugin;

    protected final Path filePath;
    protected FileConfiguration configuration;

    /**
     * Main constructor for a given YAML based repository
     *
     * @param plugin   An instance of the main plugin
     * @param filePath A {@link Path} that refers to the config file. This path can be relative to the server root directory or absolute, whichever is preferable.
     */
    protected ConfigurationRepositoryYAML(@NotNull JavaPlugin plugin, @NotNull Path filePath) {

        // I am not 100% sure if this is the intended use of references, but I thought it might have been a good idea since there will be a cyclic relationship between the plugin and any repository. This way, when there are no hard references to the plugin elsewhere it will get unloaded
        this.plugin = new WeakReference<>(Objects.requireNonNull(plugin, "plugin cannot be null!"));
        this.filePath = Objects.requireNonNull(filePath, "filePath cannot be null!");

        // Upon instantiation, we (re)load the file and thusly populate the this.configuration field. Doing this makes the class usable right away.
        this.reload();

    }

    /**
     * Internal method for retrieving the plugin from the reference, erroring if it has already been unreferenced. This should not really happen but is here for safety.
     *
     * @return The plugin after a successful dereference
     */
    protected final JavaPlugin getPlugin() {

        return Objects.requireNonNull(
                this.plugin.get(),
                "The plugin has been unloaded! Repository methods will not work properly!"
        );

    }

    /**
     * This helper function basically takes the path to the user-facing config and removes the beginning part that specifies the data folder.
     * <p>
     * Essentially, the point is to preserve the directory structure of the path but strip out the reference to the plugin portion.
     * <p>
     * Example: {@code plugins/SignStonks/example/myconfig.yml} would be turned into {@code example/myconfig.yml}
     *
     * @return The relativized path.
     */
    protected Path getRelativePath() {

        try {

            // get the data folder, transform it to an absolute path, and then relativize it against the absolute path of the filePath
            // The point of the double absolute call is that it removes any ambiguity between the type of input the user provides (absolute, relative)
            return this.getPlugin()
                    .getDataFolder()
                    .toPath()
                    .toAbsolutePath()
                    .relativize(this.filePath.toAbsolutePath());

        } catch (IllegalArgumentException suppressed) {

            RuntimeException exception = new IllegalArgumentException("The path provided is not relative to the data folder of the plugin and cannot be relativized!");
            exception.addSuppressed(suppressed);

            throw exception;

        }

    }

    /**
     * Reloads the config from disk, discarding any unsaved internal changes.
     */
    @Override
    public void reload() {

        // Load the **user-facing** configuration file
        // As per the documentation, a blank config will be returned if the file DNE or is otherwise invalid. This avoid any NPEs on behalf of this.configuration.
        this.configuration = YamlConfiguration.loadConfiguration(filePath.toFile());

        // Get the **resource-version** of the configuration file
        final InputStream defaultsStream = this.getPlugin().getResource(this.getRelativePath().toString());

        // If the resource version is not found, skip the next section
        if (defaultsStream == null) {
            this.getPlugin().getLogger().warning(String.format("Could not find the resource-version for config file (%s)", this.filePath));
            return;
        }

        // If the resource version is found, then set the defaults to whatever that is. This is important because the FileConfiguration get*() methods will look at these defaults if, say, the current file has a certain k/v pair missing and was requested.
        this.configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultsStream, Charsets.UTF_8)));

    }

    /**
     * Saves the default configuration to disk, silently failing if the user-facing file already exists
     */
    @Override
    public void saveDefaults() {

        // If the user-facing file does NOT exist, then ask the plugin to copy the resource-version to the plugin's folder, preserving file hierarchies.
        if (!Files.exists(this.filePath))
            this.getPlugin().saveResource(this.getRelativePath().toString(), false);

    }

    /**
     * Saves the configuration, with any additional modifications to disk
     */
    @Override
    public void save() {

        try {

            this.configuration.save(this.filePath.toString());

        } catch (IOException e) {

            this.getPlugin().getLogger().log(Level.SEVERE, String.format("Could not save the configuration (%s) to disk!", this.filePath), e);

        }

    }

}
