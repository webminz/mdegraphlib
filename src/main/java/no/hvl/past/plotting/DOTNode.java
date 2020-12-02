package no.hvl.past.plotting;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DOTNode extends AbstractDOTWriter{

    private final Name name;
    private final List<Triple> attributes;
    private final boolean ignoreAttributes;
    private final PrintingStrategy printingStrategy;

    public DOTNode(Name name, int indent, PrintingStrategy printingStrategy, boolean ignoreAttributes) {
        super(indent);
        this.name = name;
        this.printingStrategy = printingStrategy;
        this.ignoreAttributes = ignoreAttributes;
        this.attributes = new ArrayList<>();
    }

    public void serialize(BufferedWriter writer) throws IOException {
        writer.append('"');
        writer.append(name.print(printingStrategy));
        writer.append("\" [");

        if (ignoreAttributes || attributes.isEmpty()) {
            writer.append("label=");
            writer.append('"');
            writer.append(name.print(printingStrategy));
            writer.append("\" margin=0 shape=box");
            appendProperties(writer);
        } else {
            writer.append("margin=\"0\" shape=\"none\" label=<");
            newLine(writer);
            writer.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"2\" PORT=\"node\">");
            newLine(writer);
            writer.append("<TR><TD><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"1\">");
            newLine(writer);
            writer.append("<TR><TD ALIGN=\"center\" BALIGN=\"center\">");
            writer.append(name.print(printingStrategy));
            writer.append("</TD></TR>");
            newLine(writer);
            writer.append("</TABLE></TD></TR>");
            newLine(writer);
            writer.append("<TR><TD><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"1\">");
            for (Triple att : attributes) {
                writer.append("<TR><TD ALIGN=\"left\" BALIGN=\"left\"> ");
                writer.append(att.getLabel().print(printingStrategy));
                writer.append(" : ");
                writer.append(att.getTarget().print(printingStrategy));
                writer.append(" </TD></TR>");
                newLine(writer);
            }
            writer.append("</TABLE></TD></TR>");
            newLine(writer);
            writer.append("</TABLE>>");
        }

        writer.append("];");
    }

    public void addAttribute(Triple triple) {
        this.attributes.add(triple);
    }
}
