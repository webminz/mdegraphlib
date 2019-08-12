package no.hvl.past.graph;


import java.util.Objects;
import java.util.Set;

public class GraphError extends Exception {

    private final GraphError.ERROR_TYPE errorType;

    private final Set<Triple> affected;

    private static final String MESSAGE = "The given construction is formally ill defined, details: ";

    public static enum ERROR_TYPE {
        DANGLING_EDGE,
        UNKNOWN_MEMBER,
        HOMOMORPHISM_PROPERTY_VIOLATION,
        ILL_FORMED
    }

    GraphError(GraphError.ERROR_TYPE errorType, Set<Triple> affected) {
        super(MESSAGE + errorType.name() + '(' + affected.stream().map(Objects::toString).reduce("", (a,b) -> a + ';' + b) + ')');
        this.errorType = errorType;
        this.affected = affected;
    }

    public GraphError.ERROR_TYPE getErrorType() {
        return errorType;
    }

    public Set<Triple> getAffected() {
        return affected;
    }
}
