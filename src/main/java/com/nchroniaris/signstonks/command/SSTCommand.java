package com.nchroniaris.signstonks.command;

import com.nchroniaris.signstonks.command.exception.CommandDoesNotExist;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class SSTCommand {

    private final List<SSTCommand> subCommands;

    public SSTCommand() {

        this.subCommands = new ArrayList<>();
        Objects.requireNonNull(this.getLabel(), "You must set return non-null label in this class!");

    }

    public SSTCommand(List<SSTCommand> subCommands) {

        this();
        this.subCommands.addAll(subCommands);

    }

    public SSTCommand(SSTCommand... subCommands) {

        this();
        this.subCommands.addAll(Arrays.asList(subCommands));

    }

    protected final Optional<SSTCommand> findSubCommand(@NotNull String label) {

        // The stream will only have Commands that match the label in it, at time of #findFirst()
        return this.subCommands.stream()
                .filter(command -> command.getLabel().equals(label))
                .findFirst();

    }

    public abstract boolean execute(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull List<String> args) throws CommandDoesNotExist;

    public abstract @NotNull String getLabel();


}
