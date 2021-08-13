package com.nchroniaris.signstonks.commands;

import com.nchroniaris.signstonks.command.SSTCommand;
import com.nchroniaris.signstonks.command.exception.CommandDoesNotExist;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class RegisterCommand extends SSTCommand {

    public RegisterCommand() {

        super();

    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) throws CommandDoesNotExist {

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

}
