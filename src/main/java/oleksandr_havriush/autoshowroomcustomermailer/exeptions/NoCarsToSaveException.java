package oleksandr_havriush.autoshowroomcustomermailer.exeptions;

public class NoCarsToSaveException extends RuntimeException {
    public NoCarsToSaveException(String message) {
        super(message);
    }
}