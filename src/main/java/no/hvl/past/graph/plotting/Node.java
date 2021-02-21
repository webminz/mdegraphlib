package no.hvl.past.graph.plotting;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Node extends AbstractPlotter {

    private final Name name;
    private final Map<String,String> attributes;
    private final boolean ignoreAttributes;
    private boolean isInlined;
    private String stereotype;

    Node(Name name, int indent, PrintingStrategy printingStrategy, boolean ignoreAttributes) {
        super(indent, printingStrategy);
        this.name = name;
        this.ignoreAttributes = ignoreAttributes;
        this.attributes = new HashMap<>();
        this.isInlined = false;
    }

    public void serialize(BufferedWriter writer) throws IOException {
        if (!isInlined) {
            writer.append('"');
            writer.append(name.print(getStrategy()));
            writer.append("\" [");

            if (ignoreAttributes || attributes.isEmpty()) {
                writer.append("label=");
                writer.append('"');
                writer.append(name.print(getStrategy()));
                writer.append("\" margin=0 shape=box");
            } else {
                writer.append("margin=\"0\" shape=\"none\" label=<");
                newLine(writer);
                writer.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"2\" PORT=\"node\">");
                newLine(writer);
                writer.append("<TR><TD><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"1\">");
                newLine(writer);
                if (this.stereotype != null && !this.stereotype.isEmpty()) {
                    writer.append("<TR><TD ALIGN=\"center\" BALIGN=\"center\">");
                    writer.append(" &#171;");
                    writer.append(stereotype);
                    writer.append("e&#187; ");
                    writer.append("</TD></TR>");
                    newLine(writer);
                }
                writer.append("<TR><TD ALIGN=\"center\" BALIGN=\"center\">");
                writer.append(name.print(getStrategy()));
                writer.append("</TD></TR>");
                newLine(writer);
                writer.append("</TABLE></TD></TR>");
                newLine(writer);
                writer.append("<TR><TD><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"1\">");
                for (String att : attributes.keySet()) {
                    writer.append("<TR><TD ALIGN=\"left\" BALIGN=\"left\"> ");
                    writer.append(att);
                    writer.append(" : ");
                    writer.append(this.attributes.get(att));
                    writer.append(" </TD></TR>");
                    newLine(writer);
                }
                writer.append("</TABLE></TD></TR>");
                newLine(writer);
                writer.append("</TABLE>>");
            }
            writer.append("];");
        }
    }

    public void setStereotype(String stereotype) {
        this.stereotype = stereotype;
    }

    public void setInlined(boolean inlined) {
        isInlined = inlined;
    }


    void addAttribute(String attrbiute, String target) {
        this.attributes.put(attrbiute, target);
    }
}
