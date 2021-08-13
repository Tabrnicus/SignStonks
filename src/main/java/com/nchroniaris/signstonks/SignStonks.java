package com.nchroniaris.signstonks;

import com.nchroniaris.signstonks.command.SignStonksCommandExecutor;
import com.nchroniaris.signstonks.repository.MainRepository;
import com.nchroniaris.signstonks.repository.StockRepository;
import com.nchroniaris.signstonks.repository.concrete.MainRepositoryYAML;
import com.nchroniaris.signstonks.repository.concrete.StockRepositoryYAML;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SignStonks extends JavaPlugin {

    private MainRepository mainRepository;
    private StockRepository stockRepository;

    @Override
    public void onEnable() {

        this.getLogger().info("Initializing...");

        // Saves the default config if does not exist, failing silently if it does
        // Also ensures that the plugin's data folder exists
        this.saveDefaultConfig();

        this.initializeRepositories();

        this.registerCommands();
        this.registerListeners();

    }

    /**
     * Initializes the plugin's required repositories
     */
    private void initializeRepositories() {

        this.mainRepository = new MainRepositoryYAML(this);
        this.stockRepository = new StockRepositoryYAML(this);

    }

    /**
     * Registers all the commands with the server
     */
    private void registerCommands() {

        PluginCommand registerCommand = this.getCommand("signstonks");

        if (registerCommand != null)
            registerCommand.setExecutor(new SignStonksCommandExecutor(this));

    }

    /**
     * Registers all the listeners with the server
     */
    private void registerListeners() {

//        this.getServer().getPluginManager().registerEvents(new TestListener(), this);

    }

    /**
     * Gets the repository associated with the main plugin's configuration
     * <p>
     * Use this class to read/modify this configuration as needed.
     *
     * @return An implementing class of {@link MainRepository}
     */
    public MainRepository getMainConfig() {

        if (this.mainRepository == null)
            throw new IllegalStateException("The main repository does not exist yet!");

        return this.mainRepository;

    }

    /**
     * Gets the repository associated with the {@link com.nchroniaris.mcstonks.stock.Stock} configuration
     * <p>
     * Use this class to read/modify this configuration as needed.
     *
     * @return An implementing class of {@link StockRepository}
     */
    public StockRepository getStockConfig() {

        if (this.stockRepository == null)
            throw new IllegalStateException("The stock repository does not exist yet!");

        return this.stockRepository;

    }

}
