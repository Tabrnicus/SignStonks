package com.nchroniaris.signstonks.repository.exception;

/**
 * Common superclass of exceptions thrown by Repository classes.
 */
public class RepositoryException extends Exception {

    public RepositoryException() {
    }

    public RepositoryException(String s) {
        super(s);
    }

}
