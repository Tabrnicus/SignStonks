package com.nchroniaris.signstonks.repository.exception;

/**
 * A repository exception thrown when some element already exists
 */
public class AlreadyExistsException extends RepositoryException {

    public AlreadyExistsException() {
    }

    public AlreadyExistsException(String s) {
        super(s);
    }

}
