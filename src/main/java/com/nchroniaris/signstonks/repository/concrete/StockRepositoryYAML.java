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

    private static final String KEY_LIST_TRANSACTION = "transactionList";
    private static final String KEY_LIST_HISTORY = "historyList";

    private static final String NPE_MESSAGE_UUID = "The UUID field cannot be null!";
    private static final String NPE_MESSAGE_LOCATION = "The location field cannot be null!";
    private static final String NPE_MESSAGE_LOCATION_INVALID = "The location's world parameter cannot be null! Please construct one with a valid world.";

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

    /**
     * Utility method that verifies that the given location is non-null and that the world parameter is non-null.
     *
     * @param location The {@link Location} you want to verify
     * @throws NullPointerException Thrown if the given {@link Location} fails validation. The error message should have more information.
     */
    private static void validateLocation(Location location) throws NullPointerException {

        Objects.requireNonNull(location, NPE_MESSAGE_LOCATION);
        Objects.requireNonNull(location.getWorld(), NPE_MESSAGE_LOCATION_INVALID);

    }

    /**
     * Common method for creating a list of locations from a wildcard list
     *
     * @param candidateList A List that contains {@link Location}s. Each item will be checked, skipping any non-conforming objects.
     * @return A List of {@link Location}s from the candidate list
     */
    private List<Location> getLocationList(List<?> candidateList) {

        List<Location> locationList = new ArrayList<>();

        if (candidateList != null) {

            for (Object object : candidateList) {

                if (object instanceof Location)
                    locationList.add((Location) object);
                else
                    this.getPlugin().getLogger().warning("Object could not be deserialized into a Location! Please check the config.\n\t" + object);

            }

        }

        return locationList;

    }

    /**
     * Utility method that checks if a given {@link Location} is used anywhere in the storage (any UUID, in any location list)
     *
     * @param location The {@link Location} that you want to check existence of
     * @return {@code true} if found somewhere, {@code false} otherwise.
     */
    private boolean locationExists(Location location) {

        Set<UUID> uuids = this.getStockEntries();

        // A Set (over a List) seems like the more appropriate structure for this, as we would have to combine collections together anyway
        Set<Location> locationSet = new HashSet<>();

        for (UUID uuid : uuids) {

            locationSet.addAll(this.getTransactionSigns(uuid));
            locationSet.addAll(this.getHistorySigns(uuid));

        }

        return locationSet.contains(location);

    }

    /**
     * Utility method for overwriting the contents of the transaction list
     *
     * @param stock        The {@link UUID} of the stock for which the list will be written to
     * @param locationList The list that will be written
     */
    private void saveTransactionList(UUID stock, List<Location> locationList) {

        // Key: "<UUID>.transactionList"
        this.configuration.set(
                String.join(".", stock.toString(), KEY_LIST_TRANSACTION),
                locationList
        );

    }

    /**
     * Utility method for overwriting the contents of the history list
     *
     * @param stock        The {@link UUID} of the stock for which the list will be written to
     * @param locationList The list that will be written
     */
    private void saveHistoryList(UUID stock, List<Location> locationList) {

        // Key: "<UUID>.historyList"
        this.configuration.set(
                String.join(".", stock.toString(), KEY_LIST_HISTORY),
                locationList
        );

    }

    @Override
    @NotNull
    public synchronized List<Location> getTransactionSigns(UUID stock) {

        Objects.requireNonNull(stock, NPE_MESSAGE_UUID);

        // Key: "<UUID>.transactionList"
        return this.getLocationList(
                this.configuration.getList(String.join(".", stock.toString(), KEY_LIST_TRANSACTION))
        );

    }

    @Override
    @NotNull
    public synchronized List<Location> getHistorySigns(UUID stock) {

        Objects.requireNonNull(stock, NPE_MESSAGE_UUID);

        // Key: "<UUID>.historyList"
        return this.getLocationList(
                this.configuration.getList(String.join(".", stock.toString(), KEY_LIST_HISTORY))
        );

    }

    @Override
    @NotNull
    public synchronized Set<UUID> getStockEntries() {

        // Gets keys at the root only, since `deep` is false.
        Set<String> candidateKeys = this.configuration.getKeys(false);
        Set<UUID> stockEntries = new HashSet<>();

        for (String string : candidateKeys) {

            try {

                // This may produce a false positive considering the tolerances in format that UUID#fromString() has. i.e. A UUID with a slightly different format may result in an incorrect parse of the UUID. This could only happen with outside interference to the YAML file, though.
                stockEntries.add(UUID.fromString(string));

            } catch (IllegalArgumentException ignored) {

                // Ignore because if there are any stray keys we don't want to include them or further parse them at all

            }

        }

        return stockEntries;

    }

    @Override
    public synchronized void addTransactionSign(UUID stock, Location location) throws AlreadyExistsException {

        Objects.requireNonNull(stock, NPE_MESSAGE_UUID);
        StockRepositoryYAML.validateLocation(location);

        if (this.locationExists(location))
            throw new AlreadyExistsException(String.format("Location %d/%d/%d/%s is already bound to a stock!", location.getBlockX(), location.getBlockY(), location.getBlockZ(), Objects.requireNonNull(location.getWorld())));

        // There is no replace in-file method so we have to retrieve the list, modify it, and set it back
        List<Location> locationList = this.getTransactionSigns(stock);
        locationList.add(location);
        this.saveTransactionList(stock, locationList);

    }

    @Override
    public synchronized void addHistorySign(UUID stock, Location location) throws AlreadyExistsException {

        Objects.requireNonNull(stock, NPE_MESSAGE_UUID);
        StockRepositoryYAML.validateLocation(location);

        if (this.locationExists(location))
            throw new AlreadyExistsException(String.format("Location %d/%d/%d/%s is already bound to a stock!", location.getBlockX(), location.getBlockY(), location.getBlockZ(), Objects.requireNonNull(location.getWorld())));

        // There is no replace in-file method so we have to retrieve the list, modify it, and set it back
        List<Location> locationList = this.getHistorySigns(stock);
        locationList.add(location);
        this.saveHistoryList(stock, locationList);

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
