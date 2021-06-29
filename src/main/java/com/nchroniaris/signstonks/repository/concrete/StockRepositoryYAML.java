package com.nchroniaris.signstonks.repository.concrete;

import com.nchroniaris.signstonks.repository.StockRepository;
import com.nchroniaris.signstonks.repository.exception.AlreadyExistsException;
import com.nchroniaris.signstonks.repository.exception.DoesNotExistException;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * YAML-based repository for storing {@link com.nchroniaris.mcstonks.stock.Stock} objects
 */
public class StockRepositoryYAML extends ConfigurationRepositoryYAML implements StockRepository {

    /**
     * Main constructor for the Stock repository
     *
     * @param plugin An instance of the main plugin
     */
    public StockRepositoryYAML(@NotNull JavaPlugin plugin) {

        super(
                plugin,
                Objects.requireNonNull(plugin, "plugin cannot be null!")
                        .getDataFolder().toPath().resolve("stocks.yml")
        );

    }

    @Override
    public List<Location> getTransactionSigns(UUID stock) {
        return null;
    }

    @Override
    public List<Location> getHistorySigns(UUID stock) {
        return null;
    }

    @Override
    public Set<UUID> getStockEntries() {
        return null;
    }

    @Override
    public void addTransactionSign(UUID stock, Location location) throws AlreadyExistsException {

    }

    @Override
    public void addHistorySign(UUID stock, Location location) throws AlreadyExistsException {

    }

    @Override
    public void deleteTransactionSign(UUID stock, Location location) throws DoesNotExistException {

    }

    @Override
    public void deleteHistorySign(UUID stock, Location location) throws DoesNotExistException {

    }

    @Override
    public void deleteTransactionSign(Location location) throws DoesNotExistException {

    }

    @Override
    public void deleteHistorySign(Location location) throws DoesNotExistException {

    }

    @Override
    public void deleteStockEntry(UUID stock) throws DoesNotExistException {

    }

}
