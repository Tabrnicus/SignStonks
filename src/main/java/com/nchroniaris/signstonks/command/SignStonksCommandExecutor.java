package com.nchroniaris.signstonks.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SignStonksCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public SignStonksCommandExecutor(JavaPlugin plugin) {

        this.plugin = plugin;

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // TODO: 2021-08-12 Call command tree from {@link SignStonksCommand}

        sender.sendMessage("stub");
        return true;

    }

}
