package com.nchroniaris.signstonks.repository;

import org.bukkit.Location;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a contract for a repository class that implements methods to interact with a storage for {@link com.nchroniaris.mcstonks.stock.Stock}s
 */
public interface StockRepository extends ConfigurationRepository {

    /**
     * Gets all "transaction signs" for a specific stock.
     * <p>
     * A "transaction sign" is a sign that is responsible for buying and selling {@link com.nchroniaris.mcstonks.stock.Stock}s to the player.
     *
     * @param stock The {@link UUID} of the {@link com.nchroniaris.mcstonks.stock.Stock} that you want to get the list for
     * @return A {@link List} of {@link Location}s, each location referencing a (hopefully still) valid SignShop sign. This will return an empty list if there are no locations in the storage OR the UUID does not exist.
     */
    List<Location> getTransactionSigns(UUID stock);

    /**
     * Gets all "history signs" for a specific stock.
     * <p>
     * A "history sign" is a sign that is responsible for displaying a number of historical prices of a specific {@link com.nchroniaris.mcstonks.stock.Stock}.
     *
     * @param stock The {@link UUID} of the {@link com.nchroniaris.mcstonks.stock.Stock} that you want to get the list for
     * @return A {@link List} of {@link Location}s, each location referencing a sign (the signs pointed to by these locations are nothing special). This will return an empty list if there are no locations in the storage OR the UUID does not exist.
     */
    List<Location> getHistorySigns(UUID stock);

    /**
     * Gets all the {@link com.nchroniaris.mcstonks.stock.Stock} {@link UUID}s present in the storage.
     *
     * @return A {@link Set} of {@link UUID}s, each representing a {@link com.nchroniaris.mcstonks.stock.Stock}. This will return an empty list if there are no locations in the storage OR the UUID does not exist.
     */
    Set<UUID> getStockEntries();

    /**
     * Adds a transaction sign to the list for the {@link com.nchroniaris.mcstonks.stock.Stock} specified by the {@link UUID}.
     *
     * @param stock    The {@link UUID} of the {@link com.nchroniaris.mcstonks.stock.Stock} that you want to add to
     * @param location The {@link Location} of the Sign that you want to add. There are no checks for if this location has a valid sign.
     */
    void addTransactionSign(UUID stock, Location location);

    /**
     * Adds a history sign to the list for the {@link com.nchroniaris.mcstonks.stock.Stock} specified by the {@link UUID}.
     *
     * @param stock    The {@link UUID} of the {@link com.nchroniaris.mcstonks.stock.Stock} that you want to add to
     * @param location The {@link Location} of the Sign that you want to add. There are no checks for if this location has a valid sign.
     */
    void addHistorySign(UUID stock, Location location);

    /**
     * Deletes a transaction sign from the list for the {@link com.nchroniaris.mcstonks.stock.Stock} specified by the {@link UUID}.
     * <p>
     * This will delete the first occurrence only (if exists), so duplicate entries (which are not technically valid) must be deleted with subsequent calls.
     *
     * @param stock    The {@link UUID} of the {@link com.nchroniaris.mcstonks.stock.Stock} that you want to remove from
     * @param location The {@link Location} of the Sign that you want to remove.
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteTransactionSign(UUID stock, Location location);

    /**
     * Deletes a history sign from the list for the {@link com.nchroniaris.mcstonks.stock.Stock} specified by the {@link UUID}.
     * <p>
     * This will delete the first occurrence only (if exists), so duplicate entries (which are not technically valid) must be deleted with subsequent calls.
     *
     * @param stock    The {@link UUID} of the {@link com.nchroniaris.mcstonks.stock.Stock} that you want to remove from
     * @param location The {@link Location} of the Sign that you want to remove.
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteHistorySign(UUID stock, Location location);

    /**
     * Deletes a transaction sign from the list for the {@link com.nchroniaris.mcstonks.stock.Stock} specified by its {@link UUID}.
     * <p>
     * This will delete the first occurrence only (if exists), so duplicate entries (which are not technically valid) must be deleted with subsequent calls.
     * <p>
     * This method is broader but slower to execute, as it needs to search through all locations saved. Useful if you don't have a UUID.
     *
     * @param location The {@link Location} of the Sign that you want to remove.
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteTransactionSign(Location location);

    /**
     * Deletes a history sign from the list for the {@link com.nchroniaris.mcstonks.stock.Stock} specified by its {@link UUID}.
     * <p>
     * This will delete the first occurrence only (if exists), so duplicate entries (which are not technically valid) must be deleted with subsequent calls.
     * <p>
     * This method is broader but slower to execute, as it needs to search through all locations saved. Useful if you don't have a UUID.
     *
     * @param location The {@link Location} of the Sign that you want to remove.
     * @return {@code true} if the deletion was successful, {@code false} otherwise
     */
    boolean deleteHistorySign(Location location);

    /**
     * Delete the entry specified by the parameter from the storage. This is designed to be used to clean up unused entries in the configuration.
     * <p>
     * <b>This will delete all associated transaction and history signs!</b>
     *
     * @param stock The UUID of the entry you want to remove
     */
    boolean deleteStockEntry(UUID stock);


}
