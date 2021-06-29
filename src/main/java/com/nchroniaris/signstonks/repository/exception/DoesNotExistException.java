package com.nchroniaris.signstonks.repository.exception;

/**
 * A repository exception thrown when some element does not exist
 */
public class DoesNotExistException extends RepositoryException {

    public DoesNotExistException() {
    }

    public DoesNotExistException(String s) {
        super(s);
    }

}
