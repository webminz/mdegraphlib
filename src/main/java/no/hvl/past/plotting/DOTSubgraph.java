package no.hvl.past.plotting;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOTSubgraph extends AbstractDOTWriter {

    private final int number;
    private final Name name;
    private final Map<Name, DOTNode> nodes;
    private final List<Triple> edges;
    private final List<Tuple> specialEdges;
    private final PrintingStrategy printingStrategy;
    private final boolean ignoreAttributes;
    private boolean skipContext;

    public DOTSubgraph(int number, Name name, int indent, PrintingStrategy printingStrategy, boolean ignoreAttributes) {
        super(indent);
        this.number = number;
        this.name = name;
        this.printingStrategy = printingStrategy;
        this.ignoreAttributes = ignoreAttributes;
        this.skipContext = false;
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
        this.specialEdges = new ArrayList<>();
    }

    public void skipContext() {
        this.skipContext = true;
    }

    public void serialize(BufferedWriter sink) throws IOException {
        if (!skipContext) {
            sink.append("subgraph cluster_");
            sink.append(Integer.toString(number));
            sink.append(" {");
            newLine(sink);
            sink.append("label=\"");
            sink.append(this.name.print(printingStrategy));
            sink.append("\";");
            newLine(sink);
            sink.append("style=\"dotted\";");
            newLine(sink);
        }

        newLine(sink);

        for (Name n : nodes.keySet()) {
            nodes.get(n).serialize(sink);
            newLine(sink);
        }

        newLine(sink);

        for (Triple edge : edges) {
            sink.append('"');
            sink.append(edge.getSource().print(printingStrategy));
            sink.append("\" -> \"");
            sink.append(edge.getTarget().print(printingStrategy));
            sink.append("\" [xlabel=\" ");
            sink.append(edge.getLabel().print(printingStrategy));
            sink.append(" \" arrowhead=\"vee\"];");
            newLine(sink);
        }

        for (Tuple specialEdge : specialEdges) {
            sink.append('"');
            sink.append(specialEdge.getDomain().print(printingStrategy));
            sink.append("\" -> \"");
            sink.append(specialEdge.getCodomain().print(printingStrategy));
            sink.append("\" [arrowhead=\"onormal\"];");
            newLine(sink);
        }

        if (!skipContext) {
            sink.append("};");
        }
    }

    public void handleTriple(Triple triple) {
        if (triple.isNode() && !triple.getLabel().isValue()) {
            if (!this.nodes.containsKey(triple.getLabel())) {
                this.nodes.put(triple.getSource(), new DOTNode(triple.getSource(), getIndent() + 1, printingStrategy, ignoreAttributes ));
            }
        } else if (triple.isEddge()) {
            if (!this.nodes.containsKey(triple.getSource())) {
                this.nodes.put(triple.getSource(), new DOTNode(triple.getSource(), getIndent() + 1, printingStrategy, ignoreAttributes ));
            }
            if (triple.isAttribute()) {
                this.nodes.get(triple.getSource()).addAttribute(triple);
            } else {
                this.edges.add(triple);
            }
        }
    }
}
