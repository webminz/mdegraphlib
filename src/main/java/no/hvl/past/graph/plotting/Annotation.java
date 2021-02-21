package no.hvl.past.graph.plotting;

import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Annotation extends AbstractPlotter {

    interface SpecialAction {

        void execute(Map<Name, Name> mapping);

    }

    private Set<Name> refersTo;
    private String title;

    public Annotation(int indent, PrintingStrategy strategy, String title) {
        super(indent, strategy);
        this.title = title;
        this.refersTo = new HashSet<>();
    }

    public void addRefersTo(Name ref) {
        this.refersTo.add(ref);
    }

    @Override
    public void serialize(BufferedWriter writer) throws IOException {
        writer.append('"');
        writer.append(title);
        writer.append("\" [label=\"");
        writer.append(title);
        writer.append("\", shape=\"note\", color=\"grey\"];");
        newLine(writer);
        for (Name ref : refersTo) {
            writer.append('"');
            writer.append(title);
            writer.append("\" -> \"");
            writer.append(ref.print(getStrategy()));
            writer.append("\" [style=\"dotted\", arrowhead=\"dot\", color=\"grey\", fontcolor=\"grey\"];");
            newLine(writer);
        }
    }
}
