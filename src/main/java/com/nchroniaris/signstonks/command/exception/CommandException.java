package com.nchroniaris.signstonks.command.exception;

/**
 * Common superclass of exceptions thrown by Command classes.
 */
public class CommandException extends Exception {

    public CommandException() {
    }

    public CommandException(String s) {
        super(s);
    }

}
