package com.nchroniaris.signstonks.command;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.wargamer2010.signshop.Seller;
import org.wargamer2010.signshop.configuration.Storage;

public class RegisterCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public RegisterCommandExecutor(JavaPlugin plugin) {

        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Create default vector for storing coordinates
        Vector vector = new Vector();

        // Try to extract each coordinate, one by one, catching the index/parse error if it happens
        try {

            // Since we're looking for an actual block on the grid, we parse integers instead of doubles.
            vector.setX(Integer.parseInt(args[0]));
            vector.setY(Integer.parseInt(args[1]));
            vector.setZ(Integer.parseInt(args[2]));

        } catch (IndexOutOfBoundsException e) {

            sender.sendMessage(ChatColor.RED + "Incomplete (expected 3 coordinates)");
            return false;

        } catch (NumberFormatException e) {

            sender.sendMessage(ChatColor.RED + "Expected integer in coordinates");
            return false;

        }

        World world;

        try {

            world = this.plugin.getServer().getWorld(args[3]);

            // org.bukkit.Server#getWorld(String) returns null if there is no world by that name.
            if (world == null) {

                sender.sendMessage(ChatColor.RED + String.format("The dimension '%s' does not exist!", args[3]));
                sender.sendMessage(ChatColor.UNDERLINE + "Valid world names:");

                for (World w : this.plugin.getServer().getWorlds())
                    sender.sendMessage("- " + ChatColor.YELLOW + w.getName());

                // newline
                sender.sendMessage("");

                return true;

            }

        } catch (IndexOutOfBoundsException e) {

            sender.sendMessage(ChatColor.RED + "Incomplete (expected dimension name)");
            return false;

        }

        // TODO: 2021-06-19 check for existence of registration

        // Use location to get the "Seller" object. If there is a valid SignShop sign at that location, this will return a valid object, null otherwise
        Seller seller = Storage.get().getSeller(vector.toLocation(world));

        if (seller == null) {
            sender.sendMessage(ChatColor.RED + "The coordinates specified has no valid SignShop sign! Please double check the location AND dimension.");
            return true;
        }

        // TODO: 2021-06-19 add to registry

        // Debug
        sender.sendMessage(String.format(ChatColor.ITALIC.toString() + ChatColor.GREEN + "SignShop sign found with seller %s, item %s", seller.getOwner().getName(), seller.getItems()[0].toString()));

        return true;

    }

}
