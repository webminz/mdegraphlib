package no.hvl.past.graph;



import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.util.StreamExt;

import java.util.*;
import java.util.stream.Collectors;

public class GraphError extends RuntimeException {

    public GraphError() {
    }

    public static abstract class GraphErrorReportDetails {
        private final List<GraphErrorReportDetails> dueTo;
        private final String message;

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
        private final Name name;

        public DuplicateName(Name name) {
            super("The following name is a duplicates: ");
            this.name = name;
        }

        public Name getName() {
            return name;
        }

        @Override
        protected void report(StringBuilder stringBuilder) {
            super.report(stringBuilder);
            stringBuilder.append(name);
            stringBuilder.append('\n');
        }
    }

    public static class DanglingEdge extends GraphErrorReportDetails {
        private final Triple edge;
        private final boolean srcUnknown;
        private final boolean trgUnknown;

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
            stringBuilder.append('\n');
        }


        public Triple getEdge() {
            return edge;
        }

        public boolean isSrcUnknown() {
            return srcUnknown;
        }

        public boolean isTrgUnknown() {
            return trgUnknown;
        }
    }

    public static class UnknownTargetMapping extends GraphErrorReportDetails {
        private final Tuple mapping;

        public UnknownTargetMapping(Tuple mapping) {
            super("The target of the following mapping is unknown: ");
            this.mapping = mapping;
        }

        @Override
        protected void report(StringBuilder stringBuilder) {
            super.report(stringBuilder);
            stringBuilder.append(mapping.getDomain().print(PrintingStrategy.DETAILED));
            stringBuilder.append(" |-> '");
            stringBuilder.append(mapping.getCodomain().print(PrintingStrategy.DETAILED));
            stringBuilder.append("' !?");
            stringBuilder.append('\n');
        }

        public Tuple getMapping() {
            return mapping;
        }
    }

    public static class MissingDomainAndOrCodomain extends GraphErrorReportDetails {
        private final Name name;
        private final boolean isCodomain;

        public MissingDomainAndOrCodomain(Name name, boolean isCodomain) {
            super("The morphism '" + name.print(PrintingStrategy.DETAILED) + "' is missing a " + (isCodomain ? "codomain" : "domain") + "!");
            this.name = name;
            this.isCodomain = isCodomain;
        }

        public MissingDomainAndOrCodomain(String message, Name name, boolean isCodomain) {
            super(message);
            this.name = name;
            this.isCodomain = isCodomain;
        }
    }

    public static class HomPropertypViolated extends GraphErrorReportDetails {

        private final Triple domainEdge;
        private final Triple codomainEdge;
        private final Tuple srcMapping;
        private final Tuple lblmapping;
        private final Tuple trgMapping;

        public HomPropertypViolated(Triple domainEdge, Triple codomainEdge, Tuple srcMapping, Tuple lblmapping, Tuple trgMapping) {
            super("The following elements violate the homomorphism property (node-edge incidence):\n");
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
            String topCenter = "[" + domainEdge.getLabel().print(PrintingStrategy.DETAILED) + "]";
            String topRight = "(" + domainEdge.getTarget().print(PrintingStrategy.DETAILED) + ")";
            String middleLeft = "«" + srcMapping.getDomain().print(PrintingStrategy.IGNORE_PREFIX) + "=>" + srcMapping.getCodomain().print(PrintingStrategy.IGNORE_PREFIX) + "»";
            String middleCenter = "«" + lblmapping.getDomain().print(PrintingStrategy.IGNORE_PREFIX) + "=>" + lblmapping.getCodomain().print(PrintingStrategy.IGNORE_PREFIX) + "»";
            String middleRight = "«" + trgMapping.getDomain().print(PrintingStrategy.IGNORE_PREFIX) + "=>" + trgMapping.getCodomain().print(PrintingStrategy.IGNORE_PREFIX) + "»";
            String bottomLeft = "(" + codomainEdge.getSource().print(PrintingStrategy.DETAILED) + ")";
            String bottomCenter = "[" + codomainEdge.getLabel().print(PrintingStrategy.DETAILED) + "]";
            String bottomRight = "(" + codomainEdge.getTarget().print(PrintingStrategy.DETAILED) + ")";
            int firstColumnOffset = Math.max(Math.max(topLeft.length(), bottomLeft.length()), middleLeft.length());
            int secondColumnOffest = Math.max(Math.max(topCenter.length(), bottomCenter.length()), middleCenter.length());

            int firstColumnCntr = Math.min(topLeft.length(), bottomLeft.length()) / 2;
            int secondColumnCntr = Math.min(topCenter.length(), bottomCenter.length()) / 2;
            int thirdColumnCntr = Math.min(topRight.length(), bottomRight.length()) / 2;

            int currentPos = 0;
            stringBuilder.append(topLeft);
            currentPos += topLeft.length();

            fillChars(stringBuilder, currentPos, firstColumnOffset, '-');
            stringBuilder.append("---");
            currentPos += 3;

            stringBuilder.append(topCenter);
            currentPos += topCenter.length();

            fillChars(stringBuilder, currentPos, firstColumnOffset + secondColumnOffest, '-');
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
            fillChars(stringBuilder, currentPos, firstColumnOffset + secondColumnOffest, '-');
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
            fillChars(stringBuilder, currentPos, firstColumnOffset + secondColumnOffest, '-');
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
                fillChars(stringBuilder, currentPos, firstColumnCntr, ' ');
                stringBuilder.append(c);
                currentPos++;
                fillChars(stringBuilder, currentPos, firstColumnOffset + secondColumnCntr + 3, ' ');
                stringBuilder.append(c);
                currentPos++;
                fillChars(stringBuilder, currentPos, secondColumnOffest + thirdColumnCntr + 3, ' ');
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

        public Triple getDomainEdge() {
            return domainEdge;
        }

        public Triple getCodomainEdge() {
            return codomainEdge;
        }

        public Tuple getSrcMapping() {
            return srcMapping;
        }

        public Tuple getLblmapping() {
            return lblmapping;
        }

        public Tuple getTrgMapping() {
            return trgMapping;
        }
    }


    public static class AmbiguouslyMapped extends GraphErrorReportDetails {
        private final Set<Tuple> conflictingMappings;


        public AmbiguouslyMapped(Tuple a, Tuple b) {
            super("The following elements are mapped ambiguously: \n" + createMessage(Sets.newHashSet(a, b)));
            this.conflictingMappings = Sets.newHashSet(a, b);
        }

        public AmbiguouslyMapped(Set<Tuple> conflictingMappings) {
            super("The following elements are mapped ambiguously: \n" + createMessage(conflictingMappings));
            this.conflictingMappings = conflictingMappings;
        }

        private static String createMessage(Set<Tuple> conflictingMappings) {
            StringBuilder sb = new StringBuilder();
            Iterator<Tuple> iterator = conflictingMappings.iterator();
            while (iterator.hasNext()) {
                Tuple next = iterator.next();
                sb.append(next.getDomain().print(PrintingStrategy.DETAILED));
                sb.append(" => ");
                sb.append(next.getCodomain().print(PrintingStrategy.DETAILED));
                if (iterator.hasNext()) {
                    sb.append('\n');
                }
            }
            return sb.toString();
        }

        public Set<Tuple> getConflictingMappings() {
            return conflictingMappings;
        }
    }

    public static class NotConstructed extends GraphErrorReportDetails {


        private final String elementName;

        public NotConstructed(String elementName) {
            super("An element of type '" + elementName + "' has not been constructed!");
            this.elementName = elementName;
        }

        public String getElementName() {
            return elementName;
        }
    }


    public static class DomainOrCodomainMismatch extends GraphErrorReportDetails {
        private final Name domainA;
        private final Name domainB;
        private final boolean isCodomain;

        public DomainOrCodomainMismatch(Name domainA, Name domainB, boolean isCodomain) {
            super((isCodomain ? "Codomains" : "Domains") + " mismatch: LHS was '" + domainA.print(PrintingStrategy.DETAILED) + "' while RHS was '" + domainB.print(PrintingStrategy.DETAILED)+"'!");
            this.domainA = domainA;
            this.domainB = domainB;
            this.isCodomain = isCodomain;
        }

        public Name getDomainA() {
            return domainA;
        }

        public Name getDomainB() {
            return domainB;
        }

        public boolean isCodomain() {
            return isCodomain;
        }
    }

    public static class UnknownReference extends GraphErrorReportDetails {
        private final Name elemntName;
        private final String elementTypeName;

        public UnknownReference(Name elemntName, String elementTypeName) {
            super("The referenced element '" + elemntName.print(PrintingStrategy.DETAILED) + "' of type " + elementTypeName + " is not known!");
            this.elemntName = elemntName;
            this.elementTypeName = elementTypeName;
        }

        public Name getElemntName() {
            return elemntName;
        }

        public String getElementTypeName() {
            return elementTypeName;
        }
    }

    private final List<GraphErrorReportDetails> errorReports = new ArrayList<>();

    private static final String MESSAGE = "The current construction is formally ill defined, see further details below: ";


    public GraphError addError(GraphErrorReportDetails details) {
        this.errorReports.add(details);
        return this;
    }


    @Override
    public String getMessage() {
        StringBuilder buffer = new StringBuilder();
        for (GraphErrorReportDetails e : this.errorReports) {
            e.report(buffer);
        }
        return buffer.toString();
    }


    public Collection<DanglingEdge> getDangling() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(DanglingEdge.class).collect(Collectors.toSet());
    }

    public Collection<UnknownTargetMapping> getUnknown() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(UnknownTargetMapping.class).collect(Collectors.toSet());
    }

    public Collection<DuplicateName> getDuplicates() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(DuplicateName.class).collect(Collectors.toSet());
    }

    public Collection<HomPropertypViolated> getHomomorphismViolations() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(HomPropertypViolated.class).collect(Collectors.toSet());
    }


    public Collection<NotConstructed> getNotConstructed() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(NotConstructed.class).collect(Collectors.toSet());
    }


    public Collection<AmbiguouslyMapped> getAmbigous() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(AmbiguouslyMapped.class).collect(Collectors.toSet());
    }


    public Collection<MissingDomainAndOrCodomain> getMissingDomain() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(MissingDomainAndOrCodomain.class).filter(miss -> !miss.isCodomain).collect(Collectors.toSet());
    }

    public Collection<MissingDomainAndOrCodomain> getMissingCodomain() {
        return StreamExt.stream(this.errorReports.stream()).filterByType(MissingDomainAndOrCodomain.class).filter(miss -> miss.isCodomain).collect(Collectors.toSet());
    }


}
