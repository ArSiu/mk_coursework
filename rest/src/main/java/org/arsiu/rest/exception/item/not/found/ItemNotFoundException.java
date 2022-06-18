package org.arsiu.rest.exception.item.not.found;

public class ItemNotFoundException extends RuntimeException{

    public ItemNotFoundException(final String message) {
        super(message);
    }

    public ItemNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
