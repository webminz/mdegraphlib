package no.hvl.past.graph;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.util.Pair;

import javax.lang.model.type.ErrorType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphError extends Exception {

    private final Multimap<ERROR_TYPE, Name> errors = ArrayListMultimap.create();

    private static final String MESSAGE = "The given construction is formally ill defined, details: ";

    public GraphError(List<Pair<Name, ERROR_TYPE>> errors) {
        for (Pair<Name, ERROR_TYPE> e : errors) {
            this.errors.put(e.getSecond(), e.getFirst());
        }
    }

    GraphError(ERROR_TYPE errorType, Set<Triple> affected) {
        this.errors.putAll(errorType, affected.stream().map(Triple::getLabel).collect(Collectors.toSet()));
    }

    @Override
    public String getMessage() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(MESSAGE);
        buffer.append("\n\n");
        if (this.errors.containsKey(ERROR_TYPE.DUPLICATE_NAME)) {
            buffer.append("The following names appear as duplicates:\n");
            for (Name n : this.errors.get(ERROR_TYPE.DUPLICATE_NAME)) {
                buffer.append(n.print(PrintingStrategy.DETAILED));
                buffer.append("\n");
            }
        }
        buffer.append("\n");
        if (this.errors.containsKey(ERROR_TYPE.DANGLING_EDGE)) {
            buffer.append("The edges with the following names are dangling (source and/or target node is missing):\n");
            for (Name n : this.errors.get(ERROR_TYPE.DANGLING_EDGE)) {
                buffer.append(n.print(PrintingStrategy.DETAILED));
                buffer.append("\n");
            }
        }
        buffer.append("\n");
        if (this.errors.containsKey(ERROR_TYPE.UNKNOWN_MEMBER)) {
            buffer.append("The construction contains references to the following unkown elements:\n");
            for (Name n : this.errors.get(ERROR_TYPE.UNKNOWN_MEMBER)) {
                buffer.append(n.print(PrintingStrategy.DETAILED));
                buffer.append("\n");
            }
        }
        buffer.append("\n");
        if (this.errors.containsKey(ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION)) {
            buffer.append("The following elements violate the homomorphism property (node-edge incidence):\n");
            for (Name n : this.errors.get(ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION)) {
                buffer.append(n.print(PrintingStrategy.DETAILED));
                buffer.append("\n");
            }
        }
        // TODO treat the other errors as well.
        return buffer.toString();
    }

    public Collection<Name> getDetailsForErrorType(GraphError.ERROR_TYPE type) {
        return this.errors.get(type);
    }

    public Collection<Name> getDangling() {
        return this.errors.get(ERROR_TYPE.DANGLING_EDGE);
    }

    public Collection<Name> getUnknown() {
        return this.errors.get(ERROR_TYPE.UNKNOWN_MEMBER);
    }

    public Collection<Name> getDuplicates() {
        return this.errors.get(ERROR_TYPE.DUPLICATE_NAME);
    }

    public Collection<Name> getHomomorphismViolations() {
        return this.errors.get(ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION);
    }

    public Collection<Name> getAmbiguousMappings() {
        return this.errors.get(ERROR_TYPE.AMBIGUOS_MAPPING);
    }

    public Collection<Name> getIllFormed() {
        return this.errors.get(ERROR_TYPE.ILL_FORMED);
    }


    public boolean isNotConstructed() {
        return this.errors.containsKey(ERROR_TYPE.NOT_CONSTRUCTED);
    }



    public enum ERROR_TYPE {
        DUPLICATE_NAME,
        DANGLING_EDGE,
        UNKNOWN_MEMBER,
        HOMOMORPHISM_PROPERTY_VIOLATION,
        DOMAIN_MISMATCH,
        CODOMAIN_MISMATCH,
        ILL_FORMED,
        AMBIGUOS_MAPPING,
        NOT_CONSTRUCTED,
        LABEL_MISSING;
    }



}
