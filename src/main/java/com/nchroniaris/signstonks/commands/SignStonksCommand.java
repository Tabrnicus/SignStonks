package com.nchroniaris.signstonks.commands;

import com.nchroniaris.signstonks.command.SSTCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SignStonksCommand extends SSTCommand {

    public SignStonksCommand() {

        super(
                new RegisterCommand(),
                new UnregisterCommand()
        );

    }

    @Override
    public boolean execute(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull List<String> args) {

        return false;

    }

    @Override
    public @NotNull String getLabel() {

        return "signstonks";

    }

}
