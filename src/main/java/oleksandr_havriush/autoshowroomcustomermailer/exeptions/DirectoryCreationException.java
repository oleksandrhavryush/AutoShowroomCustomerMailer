package oleksandr_havriush.autoshowroomcustomermailer.exeptions;

public class DirectoryCreationException extends RuntimeException {

    public DirectoryCreationException(String message) {
        super(message);
    }

    public DirectoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectoryCreationException(Throwable cause) {
        super(cause);
    }
}
