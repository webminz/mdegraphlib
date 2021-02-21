package no.hvl.past.techspace;

public class TechSpaceException extends Exception {

    private final TechSpace techSpace;

    public TechSpaceException(String message, TechSpace techSpace) {
        super("[" + techSpace.ID() + "]: " + message);
        this.techSpace = techSpace;
    }

    public TechSpaceException(String message, Throwable cause, TechSpace techSpace) {
        super("[" + techSpace.ID() + "]: " + message, cause);
        this.techSpace = techSpace;
    }

    public TechSpaceException(Throwable cause, TechSpace techSpace) {
        super("[" + techSpace.ID() + "]: Exception occurred. Further details below...", cause);
        this.techSpace = techSpace;
    }
}
