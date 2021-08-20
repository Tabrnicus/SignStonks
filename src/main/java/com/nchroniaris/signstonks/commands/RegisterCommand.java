package com.nchroniaris.signstonks.commands;

import com.nchroniaris.signstonks.SignStonks;
import com.nchroniaris.signstonks.command.SSTCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class RegisterCommand extends SSTCommand {

    public RegisterCommand() {

        super();

    }

    @Override
    public boolean execute(@NotNull SignStonks plugin, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {

        return false;

    }

    @Override
    public @NotNull String getLabel() {

        return "register";

    }

    @Override
    public void sendHelp(Consumer<String> recipient) {

        recipient.accept(this.getClass().getCanonicalName() + ": test help message");

    }

    private void sendAvailableUUIDs(@NotNull SignStonks plugin, @NotNull CommandSender sender, @NotNull String label, String target, Location location) {

        sender.sendMessage("You must specify a valid stock UUID:");

        for (UUID uuid : plugin.getStockConfig().getStockEntries()) {

            // Unfortunately, this click event is coupled to the command implementation since we want to suggest the command and not run it directly. So if the syntax changes, this must also be updated.
            sender.spigot().sendMessage(new ComponentBuilder("  - ")
                    .color(ChatColor.YELLOW.asBungee())
                    .append(uuid.toString())
                    .event(new ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            String.format(
                                    "/%s register %s %d %d %d %s %s",
                                    label,
                                    target,
                                    location.getBlockX(),
                                    location.getBlockY(),
                                    location.getBlockZ(),
                                    Objects.requireNonNull(location.getWorld()).getName(),
                                    uuid
                            )
                    ))
                    .event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(new TextComponent("click to fill command"))
                                    .italic(true)
                                    .create()
                    ))
                    .create()
            );

        }

        sender.sendMessage(ChatColor.ITALIC + "(click on the uuids to autofill the command)");

    }

}
