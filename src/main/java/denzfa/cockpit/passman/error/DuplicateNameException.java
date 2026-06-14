package denzfa.cockpit.passman.error;

/** Thrown when a document name would violate the unique constraint. Maps to HTTP 409. */
public class DuplicateNameException extends RuntimeException {

    public DuplicateNameException(String message) {
        super(message);
    }
}
