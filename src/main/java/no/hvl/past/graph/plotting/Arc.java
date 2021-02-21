package no.hvl.past.graph.plotting;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.BufferedWriter;
import java.io.IOException;

class Arc extends AbstractPlotter {

    enum ArrowType {
        REFERENCE,
        BIREFERENCE,
        INHERITANCE,
        AGGREGATION,
        COMPOSITION,
        DEPENDENCY
    }

    private final Name from;
    private final Name to;
    private final String label;
    private String srcLabel;
    private String trgLabel;
    private boolean isInlined;
    private ArrowType arrowType;

    public Arc(int indent, PrintingStrategy strategy, Name from, Name to, String label) {
        super(indent, strategy);
        this.from = from;
        this.to = to;
        this.label = label;
        this.arrowType = ArrowType.REFERENCE;
    }

    public Name getFrom() {
        return from;
    }

    public Name getTo() {
        return to;
    }

    public String getLabel() {
        return label;
    }

    public void setSrcLabel(String srcLabel) {
        this.srcLabel = srcLabel;
    }

    public void setTrgLabel(String trgLabel) {
        this.trgLabel = trgLabel;
    }

    public void setInlined(boolean inlined) {
        isInlined = inlined;
    }

    public void setArrowType(ArrowType arrowType) {
        this.arrowType = arrowType;
    }

    public String getSrcLabel() {
        return srcLabel;
    }

    public String getTrgLabel() {
        return trgLabel;
    }

    @Override
    public void serialize(BufferedWriter writer) throws IOException {
        if (!isInlined) {
            writer.append('"');
            writer.append(from.print(getStrategy()));
            writer.append("\" -> \"");
            writer.append(to.print(getStrategy()));
            writer.append("\" [ ");
            if (!this.label.isEmpty()) {
                writer.append("label=\"");
                writer.append(label);
                writer.append("\", ");
            }
            if (this.srcLabel != null && !this.srcLabel.isEmpty()) {
                writer.append("taillabel=\"");
                writer.append(srcLabel);
                writer.append("\",");
            }
            if (this.trgLabel != null && !this.trgLabel.isEmpty()) {
                writer.append("headlabel=\"");
                writer.append(trgLabel);
                writer.append("\",");
            }
            switch (arrowType) {
                case REFERENCE:
                    writer.append("arrowhead=\"open\"");
                    break;
                case DEPENDENCY:
                    writer.append("arrowhead=\"open\",style=\"dashed\" ");
                    break;
                case AGGREGATION:
                    writer.append("arrowtail=\"odiamond\"");
                    break;
                case COMPOSITION:
                    writer.append("arrowtail=\"diamond\"");
                    break;
                case INHERITANCE:
                    writer.append("arrowhead=\"empty\"");
                default:
                case BIREFERENCE:
                    break;
            }

            writer.append("];");
            newLine(writer);
        }

    }
}
