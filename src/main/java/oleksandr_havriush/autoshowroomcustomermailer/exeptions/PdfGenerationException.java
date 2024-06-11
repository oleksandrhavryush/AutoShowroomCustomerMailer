package oleksandr_havriush.autoshowroomcustomermailer.exeptions;

public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message) {
        super(message);
    }

    public PdfGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfGenerationException(Throwable cause) {
        super(cause);
    }
}
