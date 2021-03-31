package no.hvl.past.graph;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.util.Pair;

import javax.lang.model.type.ErrorType;
import java.util.*;
import java.util.stream.Collectors;

public class GraphError extends Exception {

    public static abstract class GraphErrorReportDetails {
        private List<GraphErrorReportDetails> dueTo;
        private String message;

        public GraphErrorReportDetails(String message) {
            this.message = message;
            this.dueTo = new ArrayList<>();
        }

        public void addCause(GraphErrorReportDetails errorReportDetails) {
            this.dueTo.add(errorReportDetails);
        }

        protected void report(StringBuilder stringBuilder) {
            stringBuilder.append("[GRAPH_ERROR] ");
            stringBuilder.append(message);
        }
    }

    public static class DuplicateName extends GraphErrorReportDetails {
        private Name name;

        public DuplicateName(Name name) {
            super("The following name is a duplicates: ");
            this.name = name;
        }
    }

    public static class DanglingEdge extends GraphErrorReportDetails {
        private final Triple edge;
        private boolean srcUnknown;
        private boolean trgUnknown;

        public DanglingEdge(Triple edge, boolean srcUnknown, boolean trgUnknown) {
            super("This edge is dangling (source and/or target node is missing): ");
            this.edge = edge;
            this.srcUnknown = srcUnknown;
            this.trgUnknown = trgUnknown;
        }

        @Override
        protected void report(StringBuilder stringBuilder) {
            super.report(stringBuilder);
            if (srcUnknown) {
                stringBuilder.append('?');
            } else {
                stringBuilder.append(edge.getSource().print(PrintingStrategy.DETAILED));
            }
            stringBuilder.append("---");
            stringBuilder.append(edge.getLabel().print(PrintingStrategy.DETAILED));
            stringBuilder.append("-->");
            if (trgUnknown) {
                stringBuilder.append('?');
            } else {
                stringBuilder.append(edge.getTarget().print(PrintingStrategy.DETAILED));
            }
        }
    }

    public static class UnknownTargetMapping extends GraphErrorReportDetails {
        private Tuple mapping;

        public UnknownTargetMapping(Tuple mapping) {
            super("The target of the following mapping is unknown: ");
            this.mapping = mapping;
        }

        @Override
        protected void report(StringBuilder stringBuilder) {
            super.report(stringBuilder);
            stringBuilder.append(mapping.getDomain().print(PrintingStrategy.DETAILED));
            stringBuilder.append(" |-> ?! ");
            stringBuilder.append(mapping.getCodomain().print(PrintingStrategy.DETAILED));
            stringBuilder.append(" !?");
        }
    }

    public static class HomPropertypViolated extends GraphErrorReportDetails {

        private Triple domainEdge;
        private Triple codomainEdge;
        private Tuple srcMapping;
        private Tuple lblmapping;
        private Tuple trgMapping;

        public HomPropertypViolated(Triple domainEdge, Triple codomainEdge, Tuple srcMapping, Tuple lblmapping, Tuple trgMapping) {
            super("The following elements violate the homomorphism property (node-edge incidence): ...\n");
            this.domainEdge = domainEdge;
            this.codomainEdge = codomainEdge;
            this.srcMapping = srcMapping;
            this.lblmapping = lblmapping;
            this.trgMapping = trgMapping;
        }

        @Override
        protected void report(StringBuilder stringBuilder) {
            super.report(stringBuilder);
            String topLeft = "(" + domainEdge.getSource().print(PrintingStrategy.DETAILED) + ")";
            String topCenter =  "[" +domainEdge.getLabel().print(PrintingStrategy.DETAILED) + "]";
            String topRight =  "(" +domainEdge.getTarget().print(PrintingStrategy.DETAILED) + ")";
            String middleLeft = "«" + srcMapping.getDomain().print(PrintingStrategy.IGNORE_PREFIX) + "=>" + srcMapping.getCodomain().print(PrintingStrategy.IGNORE_PREFIX) + "»";
            String middleCenter = "«" + lblmapping.getDomain().print(PrintingStrategy.IGNORE_PREFIX) + "=>" + lblmapping.getCodomain().print(PrintingStrategy.IGNORE_PREFIX) + "»";
            String middleRight = "«" + trgMapping.getDomain().print(PrintingStrategy.IGNORE_PREFIX) + "=>" + trgMapping.getCodomain().print(PrintingStrategy.IGNORE_PREFIX) + "»";
            String bottomLeft = "(" + codomainEdge.getSource().print(PrintingStrategy.DETAILED) + ")";
            String bottomCenter =  "[" +codomainEdge.getLabel().print(PrintingStrategy.DETAILED) + "]";
            String bottomRight =  "(" +codomainEdge.getTarget().print(PrintingStrategy.DETAILED) + ")";
            int firstColumnOffset = Math.max(Math.max(topLeft.length(), bottomLeft.length()), middleLeft.length());
            int secondColumnOffest = Math.max(Math.max(topCenter.length(), bottomCenter.length()), middleCenter.length());

            int firstColumnCntr = Math.min(topLeft.length(), bottomLeft.length()) /2;
            int secondColumnCntr = Math.min(topCenter.length(), bottomCenter.length()) /2;
            int thirdColumnCntr = Math.min(topRight.length(), bottomRight.length()) /2;

            int currentPos = 0;
            stringBuilder.append(topLeft);
            currentPos += topLeft.length();

            fillChars(stringBuilder, currentPos, firstColumnOffset, '-');
            stringBuilder.append("---");
            currentPos += 3;

            stringBuilder.append(topCenter);
            currentPos += topCenter.length();

            fillChars(stringBuilder,currentPos, firstColumnOffset + secondColumnOffest, '-');
            stringBuilder.append("-->");
            stringBuilder.append(topRight);
            stringBuilder.append('\n');

            mkVerticalRows(stringBuilder, firstColumnOffset, secondColumnOffest, firstColumnCntr, secondColumnCntr, thirdColumnCntr, 2, '|');

            currentPos = 0;

            stringBuilder.append(middleLeft);
            currentPos += middleLeft.length();
            fillChars(stringBuilder, currentPos, firstColumnOffset, '-');
            stringBuilder.append("  ");
            currentPos += 3;
            stringBuilder.append(middleCenter);
            currentPos += middleCenter.length();
            fillChars(stringBuilder,currentPos, firstColumnOffset + secondColumnOffest, '-');
            stringBuilder.append("  ");
            stringBuilder.append(middleRight);
            stringBuilder.append('\n');

            mkVerticalRows(stringBuilder, firstColumnOffset, secondColumnOffest, firstColumnCntr, secondColumnCntr, thirdColumnCntr, 2, '|');
            mkVerticalRows(stringBuilder, firstColumnOffset, secondColumnOffest, firstColumnCntr, secondColumnCntr, thirdColumnCntr, 1, 'V');

            currentPos = 0;

            stringBuilder.append(bottomLeft);
            currentPos += bottomLeft.length();
            fillChars(stringBuilder, currentPos, firstColumnOffset, '-');
            stringBuilder.append("---");
            currentPos += 3;
            stringBuilder.append(bottomCenter);
            currentPos += bottomCenter.length();
            fillChars(stringBuilder,currentPos, firstColumnOffset + secondColumnOffest, '-');
            stringBuilder.append("-->");
            stringBuilder.append(bottomRight);
            stringBuilder.append('\n');
        }

        private void mkVerticalRows(
                StringBuilder stringBuilder,
                int firstColumnOffset,
                int secondColumnOffest,
                int firstColumnCntr,
                int secondColumnCntr,
                int thirdColumnCntr,
                int count,
                char c) {
            for (int i = 0; i < count; i++) {
                int currentPos = 0;
                fillChars(stringBuilder, currentPos, firstColumnCntr,' ');
                stringBuilder.append(c);
                currentPos++;
                fillChars(stringBuilder,currentPos, firstColumnOffset + secondColumnCntr + 3, ' ');
                stringBuilder.append(c);
                currentPos++;
                fillChars(stringBuilder,currentPos,  secondColumnOffest + thirdColumnCntr + 3, ' ');
                stringBuilder.append(c);
                stringBuilder.append('\n');
            }
        }

        private void fillChars(StringBuilder stringBuilder, int currentPos, int firstColumnOffset, char c) {
            while (currentPos < firstColumnOffset) {
                stringBuilder.append(c);
                currentPos++;
            }
        }
    }

    private final Multimap<ERROR_TYPE, Name> errors = ArrayListMultimap.create();
    private List<GraphErrorReportDetails> errorReports = new ArrayList<>();

    private static final String MESSAGE = "The given construction is formally ill defined, details: ";

    public GraphError(List<Pair<Name, ERROR_TYPE>> errors) {
        for (Pair<Name, ERROR_TYPE> e : errors) {
            this.errors.put(e.getSecond(), e.getFirst());
        }
    }

    public void addError(GraphErrorReportDetails details) { // TODO make private and add a builder
        this.errorReports.add(details);
    }

    GraphError(ERROR_TYPE errorType, Set<Triple> affected) {
        this.errors.putAll(errorType, affected.stream().map(Triple::getLabel).collect(Collectors.toSet()));
    }

    @Override
    public String getMessage() {
        if (!errorReports.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            for (GraphErrorReportDetails e : this.errorReports) {
                e.report(buffer);
            }
            return buffer.toString();
        } else {
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
