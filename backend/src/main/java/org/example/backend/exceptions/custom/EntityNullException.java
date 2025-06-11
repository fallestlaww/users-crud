package org.example.backend.exceptions.custom;

/**
 * In case of fact, that NullPointerException is not such a good choice for business logic,
 * this custom exception thrown when an entity or its required fields are null.
 * This exception is used to indicate that a required entity or field is missing or null.
 */
public class EntityNullException extends RuntimeException {
    /**
     * Constructs a new EntityNullException with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityNullException(String message) {
        super(message);
    }
}
