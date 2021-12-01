package no.hvl.past;

public class UnsupportedFeatureException extends Exception {

    private static final String DEFAULT_MESSAGE = "This feature is not supported!";

    private final String detailedMessage;

    public UnsupportedFeatureException() {
        super(DEFAULT_MESSAGE);
        this.detailedMessage = DEFAULT_MESSAGE;
    }

    public UnsupportedFeatureException(String detailedMessage) {
        super(detailedMessage);
        this.detailedMessage = detailedMessage;
    }

    public UnsupportedFeatureException(Throwable cause, String detailedMessage) {
        super(detailedMessage, cause);
        this.detailedMessage = detailedMessage;
    }

    public UnsupportedFeatureException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
        this.detailedMessage = DEFAULT_MESSAGE;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }
}
