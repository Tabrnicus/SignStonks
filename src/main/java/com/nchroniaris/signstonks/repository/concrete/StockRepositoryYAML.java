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

    // -- Functional Interfaces -- //

    @FunctionalInterface
    private interface LocationListSupplier {
        List<Location> get(UUID stockUUID);
    }

    @FunctionalInterface
    private interface LocationListSaver {
        void save(UUID stockUUID, List<Location> locationList);
    }

    // -- Constructors -- //

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

    // -- Utility Methods -- //

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
     * @param stockUUID    The {@link UUID} of the stock for which the list will be written to
     * @param locationList The list that will be written
     */
    private void saveTransactionList(UUID stockUUID, List<Location> locationList) {

        // Key: "<UUID>.transactionList"
        this.configuration.set(
                String.join(".", stockUUID.toString(), KEY_LIST_TRANSACTION),
                locationList
        );

    }

    /**
     * Utility method for overwriting the contents of the history list
     *
     * @param stockUUID    The {@link UUID} of the stock for which the list will be written to
     * @param locationList The list that will be written
     */
    private void saveHistoryList(UUID stockUUID, List<Location> locationList) {

        // Key: "<UUID>.historyList"
        this.configuration.set(
                String.join(".", stockUUID.toString(), KEY_LIST_HISTORY),
                locationList
        );

    }

    /**
     * General method that adds a given location to a list and saves it. Specifically, it implements the following steps:
     * <ol>
     *     <li>Gets an existing {@link List} of {@link Location}s</li>
     *     <li>Adds the given {@link Location} to the list</li>
     *     <li>Saves the modified list back to the storage</li>
     * </ol>
     * The algorithm stays the same no matter the source of the list or the save location of the list. Therefore, we can use functional interfaces to abstract the functionality.
     * <p>
     * Make sure to double check the method references as you can end up overwriting the wrong list by accident.
     *
     * @param stockUUID The {@link UUID} of the stock you want to target. The list should be accessible from this node.
     * @param location  The {@link Location} that you want to add
     * @param supplier  A {@link LocationListSupplier} that supplies the {@link List} you want to modify.
     * @param saver     A {@link LocationListSaver} that saves the resulting list to the storage.
     * @throws AlreadyExistsException Thrown when the sign already exists somewhere in the storage.
     */
    private void addSignToList(UUID stockUUID, Location location, LocationListSupplier supplier, LocationListSaver saver) throws AlreadyExistsException {

        Objects.requireNonNull(stockUUID, NPE_MESSAGE_UUID);
        StockRepositoryYAML.validateLocation(location);

        Objects.requireNonNull(supplier, "The supplier contract cannot be null!");
        Objects.requireNonNull(saver, "The saver contract cannot be null!");

        if (this.locationExists(location))
            throw new AlreadyExistsException(String.format("Location %d/%d/%d/%s is already bound to a stock!",
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    Objects.requireNonNull(location.getWorld()).getName()
            ));

        // There is no replace in-file method so we have to retrieve the list, modify it, and set it back
        List<Location> locationList = supplier.get(stockUUID);
        locationList.add(location);
        saver.save(stockUUID, locationList);

    }

    /**
     * General method that removes a given location from a list and saves it. Specifically, it implements the following steps:
     * <ol>
     *     <li>Gets an existing {@link List} of {@link Location}s</li>
     *     <li>Deletes the given {@link Location} from the list</li>
     *     <li>Saves the modified list back to the storage</li>
     * </ol>
     * The algorithm stays the same no matter the source of the list or the save location of the list. Therefore, we can use functional interfaces to abstract the functionality.
     * <p>
     * Make sure to double check the method references as you can end up overwriting the wrong list by accident.
     *
     * @param stockUUID The {@link UUID} of the stock you want to target. The list should be accessible from this node.
     * @param location  The {@link Location} that you want to delete
     * @param supplier  A {@link LocationListSupplier} that supplies the {@link List} you want to modify.
     * @param saver     A {@link LocationListSaver} that saves the resulting list to the storage.
     * @throws DoesNotExistException Thrown when the location does not exist within the source list.
     */
    private void deleteSignFromList(UUID stockUUID, Location location, LocationListSupplier supplier, LocationListSaver saver) throws DoesNotExistException {

        Objects.requireNonNull(stockUUID, NPE_MESSAGE_UUID);
        StockRepositoryYAML.validateLocation(location);

        Objects.requireNonNull(supplier, "The supplier contract cannot be null!");
        Objects.requireNonNull(saver, "The saver contract cannot be null!");

        List<Location> locationList = supplier.get(stockUUID);

        // List#remove(Object) returns false iff the object does not exist in the list, so we throw the appropriate exception if this is the case.
        if (!locationList.remove(location))
            throw new DoesNotExistException(String.format("Location %d/%d/%d/%s does not exist in transaction list for stock (%s)!",
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    Objects.requireNonNull(location.getWorld()).getName(),
                    stockUUID
            ));

        saver.save(stockUUID, locationList);

    }

    /**
     * General method that removes a given location from a list and saves it. Specifically, it implements the following steps:
     * <ol>
     *     <li>Gets an existing {@link List} of {@link Location}s</li>
     *     <li>Deletes the given {@link Location} from the list</li>
     *     <li>Saves the modified list back to the storage</li>
     *     <li>Repeats the above steps until all UUIDs in the storage have been exhausted.</li>
     * </ol>
     * This method is broader but slower to execute, as it needs to search through all locations saved. Useful if you don't have a UUID.
     * <p>
     * The algorithm stays the same no matter the source of the list or the save location of the list. Therefore, we can use functional interfaces to abstract the functionality.
     * <p>
     * Make sure to double check the method references as you can end up overwriting the wrong list by accident.
     *
     * @param location The {@link Location} that you want to delete
     * @param supplier A {@link LocationListSupplier} that supplies the {@link List} you want to modify.
     * @param saver    A {@link LocationListSaver} that saves the resulting list to the storage.
     * @return The UUID of the stock that the sign has been deleted from, for reference
     * @throws DoesNotExistException Thrown when the location does not exist within the entire storage.
     */
    private UUID deleteSignFromList(Location location, LocationListSupplier supplier, LocationListSaver saver) throws DoesNotExistException {

        DoesNotExistException dneException = new DoesNotExistException(String.format("Location %d/%d/%d/%s does not exist in any stock's transaction list!",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                Objects.requireNonNull(location.getWorld()).getName()
        ));

        for (UUID stockUUID : this.getStockEntries()) {

            try {

                // If the deletion succeeds for this UUID, no error will be thrown and the return path will be taken, thus not throwing an error
                this.deleteSignFromList(stockUUID, location, supplier, saver);
                return stockUUID;

            } catch (DoesNotExistException e) {

                dneException.addSuppressed(e);
                // We are ignoring this because it is possible that the element may exist under a different UUID's list, and we don't want to prematurely error out.

            }

        }

        // If execution arrives here, that means that every UUID's list has been exhausted and the location has not been found anywhere. To abide by the method contract, we throw a final error to indicate it wasn't found anywhere.
        throw dneException;

    }

    // -- Public Methods -- //

    @Override
    @NotNull
    public synchronized List<Location> getTransactionSigns(UUID stockUUID) {

        Objects.requireNonNull(stockUUID, NPE_MESSAGE_UUID);

        // Key: "<UUID>.transactionList"
        return this.getLocationList(
                this.configuration.getList(String.join(".", stockUUID.toString(), KEY_LIST_TRANSACTION))
        );

    }

    @Override
    @NotNull
    public synchronized List<Location> getHistorySigns(UUID stockUUID) {

        Objects.requireNonNull(stockUUID, NPE_MESSAGE_UUID);

        // Key: "<UUID>.historyList"
        return this.getLocationList(
                this.configuration.getList(String.join(".", stockUUID.toString(), KEY_LIST_HISTORY))
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
    public synchronized void addTransactionSign(UUID stockUUID, Location location) throws AlreadyExistsException {

        this.addSignToList(
                stockUUID,
                location,
                this::getTransactionSigns,
                this::saveTransactionList
        );

    }

    @Override
    public synchronized void addHistorySign(UUID stockUUID, Location location) throws AlreadyExistsException {

        this.addSignToList(
                stockUUID,
                location,
                this::getHistorySigns,
                this::saveHistoryList
        );

    }

    @Override
    public synchronized void deleteTransactionSign(UUID stockUUID, Location location) throws DoesNotExistException {

        this.deleteSignFromList(
                stockUUID,
                location,
                this::getTransactionSigns,
                this::saveTransactionList
        );

    }

    @Override
    public synchronized void deleteHistorySign(UUID stockUUID, Location location) throws DoesNotExistException {

        this.deleteSignFromList(
                stockUUID,
                location,
                this::getHistorySigns,
                this::saveHistoryList
        );

    }

    @Override
    public synchronized UUID deleteTransactionSign(Location location) throws DoesNotExistException {

        return this.deleteSignFromList(
                location,
                this::getTransactionSigns,
                this::saveTransactionList
        );

    }

    @Override
    public synchronized UUID deleteHistorySign(Location location) throws DoesNotExistException {

        return this.deleteSignFromList(
                location,
                this::getHistorySigns,
                this::saveHistoryList
        );

    }

    @Override
    public synchronized UUID deleteSign(Location location) throws DoesNotExistException {

        DoesNotExistException dneException = new DoesNotExistException(String.format("Location %d/%d/%d/%s does not exist in any stock's transaction list!",
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                Objects.requireNonNull(location.getWorld()).getName()
        ));

        // Not sure if there is a better way to do this but this method is coupled to the number of lists in the interface. If it is updated, this must also be updated.
        LocationDeleter[] locationDeleters = {
                this::deleteTransactionSign,
                this::deleteHistorySign,
        };

        for (LocationDeleter deleter : locationDeleters) {

            try {

                return deleter.delete(location);

            } catch (DoesNotExistException e) {

                dneException.addSuppressed(e);
                // ignore because we don't want to error out prematurely

            }

        }

        // If we get here that means we haven't returned yet so we throw a final DNEException
        throw dneException;

    }

    @Override
    public synchronized void deleteStockEntry(UUID stockUUID) throws DoesNotExistException {

        Objects.requireNonNull(stockUUID, NPE_MESSAGE_UUID);

        if (!this.configuration.contains(stockUUID.toString(), false))
            throw new DoesNotExistException(String.format("The UUID '%s' is not present in the storage!", stockUUID));

        // Clear entire ConfigurationSection
        this.configuration.set(stockUUID.toString(), null);

    }

}
