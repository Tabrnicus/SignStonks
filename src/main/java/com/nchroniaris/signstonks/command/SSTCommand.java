package com.nchroniaris.signstonks.command;

import com.nchroniaris.signstonks.SignStonks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * Abstract class representing a command within the SignStonks plugin. These should normally be called from a {@link org.bukkit.command.CommandExecutor}.
 * <p>
 * General Responsibilities:
 * <ul>
 *     <li>Each command should be able to perform something with respect to the game, invoked by a command</li>
 *     <li>Each command should have 0 or more "sub commands" that they are able to delegate responsibilities to</li>
 *     <li>Each command should have verbose help text for the syntax of commands/subcommands</li>
 * </ul>
 */
public abstract class SSTCommand {

    // List of subcommands. Accessible by #execute(String, SignStonks, CommandSender, Command, String, List) and #findSubCommand(String)
    private final List<SSTCommand> subCommands;

    /**
     * General constructor. Call when you have no subcommands to assign to this command.
     */
    public SSTCommand() {

        this.subCommands = new ArrayList<>();
        Objects.requireNonNull(this.getLabel(), "You must set return non-null label in this class!");

    }

    /**
     * Specialized constructor. Call when you have subcommands in the form of a list.
     */
    public SSTCommand(List<SSTCommand> subCommands) {

        this();
        this.subCommands.addAll(subCommands);

    }

    /**
     * Specialized constructor. Call when you want to inline subcommands.
     */
    public SSTCommand(SSTCommand... subCommands) {

        this();
        this.subCommands.addAll(Arrays.asList(subCommands));

    }

    /**
     * Represents the string name of the command. This string will be used during execution, mostly for determining subcommands. In other words, if an implementer of this class is a subcommand to another, this label will be used by the parent to find and execute the child.
     *
     * @return The command's label.
     */
    public abstract @NotNull String getLabel();

    /**
     * Sends this command's help text to the recipient.
     *
     * @param recipient A consumer of strings representing the recipient of the help text.
     */
    public abstract void sendHelp(Consumer<String> recipient);

    /**
     * This method should contain the main logic of the command.
     *
     * @param plugin  The SignStonks plugin, potentially of use during execution.
     * @param sender  The sender of the command.
     * @param command The {@link Command} object tied to the parent of this {@link SSTCommand} tree.
     * @param label   The string representation of the top-level command. Lightly coupled to the command parameter.
     * @param args    The remaining arguments of the command, at this point. The execution should modify this list before passing it to any subcommands.
     * @return {@code true} if the command successfully <b>parsed</b>, {@code false} otherwise. Semantic errors should return true. In other words, you would want to return false if you want to show the help text.
     */
    public abstract boolean execute(@NotNull SignStonks plugin, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args);

    /**
     * This method delegates execution of this command to the subcommand pointed to by the subCommand parameter.
     *
     * @param subCommand A string representation of the sub-command to delegate to.
     * @param plugin     The SignStonks plugin, potentially of use during execution.
     * @param sender     The sender of the command.
     * @param command    The {@link Command} object tied to the parent of this {@link SSTCommand} tree.
     * @param label      The string representation of the top-level command. Lightly coupled to the command parameter.
     * @param args       The remaining arguments of the command, at this point. The execution should modify this list before passing it to any subcommands.
     * @return {@code true} if the command successfully <b>parsed</b>, {@code false} otherwise. Semantic errors should return true. In other words, you would want to return false if you want to show the help text.
     */
    protected final boolean execute(@NotNull String subCommand, @NotNull SignStonks plugin, @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull List<String> args) {

        try {

            // Note that we are passing a lowercase string to the method
            return this.findSubCommand(subCommand.toLowerCase())
                    .orElseThrow(NoSuchElementException::new)
                    .execute(plugin, sender, command, label, args);

        } catch (NoSuchElementException e) {

            // If no such subcommand is found (indicated by NoSuchElementException), display errors to the user and return false (since this is a syntax error).
            sender.sendMessage(ChatColor.RED + String.format("sub-command '%s' not found! Please check the syntax of the command.", subCommand));
            this.sendHelp(sender::sendMessage);

            return false;

        }

    }

    /**
     * Finds a subcommand whose label matches the one passed as a parameter
     *
     * @param label The label used for searching
     * @return An {@link Optional} representing the potentially found command.
     */
    protected final Optional<SSTCommand> findSubCommand(@NotNull String label) {

        // The stream will only have Commands that match the label in it, at time of #findFirst()
        return this.subCommands.stream()
                .filter(command -> command.getLabel().equals(label))
                .findFirst();

    }

}
