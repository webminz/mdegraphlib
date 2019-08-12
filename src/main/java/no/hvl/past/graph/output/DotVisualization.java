package no.hvl.past.graph.output;

import no.hvl.past.graph.Triple;
import no.hvl.past.graph.Tuple;
import no.hvl.past.graph.names.Name;
import no.hvl.past.graph.names.PrintingStrategy;
import no.hvl.past.graph.OutputPort;
import no.hvl.past.util.Pair;

import java.util.*;

public class DotVisualization implements OutputPort<String> {


    private static class Node {
        private final int id;
        private final String label;
        private final List<Pair<String, String>> comppartments;

        private Node(int id, String label, List<Pair<String, String>> comppartments) {
            this.id = id;
            this.label = label;
            this.comppartments = comppartments;
        }

        private String build() {
            if (comppartments.isEmpty()) {
                return sanitizeId(id) + " [label=\"" + this.label + "\"];\n";
            } else {
                return sanitizeId(id) + " [shape=\"plaintext\" label=<" + buildHTLMTable() + ">];\n";
                // TODO
            }
        }


        private String buildHTLMTable() {
            return "<table title=\"" + this.label + "\" border=\"0\" cellborder=\"1\" cellspacing=\"0\" cellpadding=\"2\" port=\"p\">\n" +
                    "<tr>" +
                    "<td>" +
                    "<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">" +
                    "<tr>" +
                    "<td align=\"center\" balign=\"center\">" + this.label + "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td>" +
                    "<table border=\"0\" cellspacing=\"0\" cellpadding=\"1\">" +
                    this.comppartments.stream().map(pair -> "<tr><td align=\"left\" balign=\"left\"> " + pair.getFirst() + " : " + pair.getSecond() + " </td></tr>").reduce("", String::concat) +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>";

        }

        private void addCompartment(Pair<String, String> stringStringPair) {
            this.comppartments.add(stringStringPair);
        }
    }

    private static String sanitizeId(int id) {
        if (id < 0) {
            return "nn" + Integer.toString(id * -1);
        }
        return "n" + Integer.toString(id);
    }

    private static class Edge {
        private final Node src;
        private final Node tgt;
        private final String label;

        private Edge(Node src, Node tgt, String label) {
            this.src = src;
            this.tgt = tgt;
            this.label = label;
        }
    }

    private final Set<Name> baseTypes;

    private final PrintingStrategy strategy;

    private final Map<Name, Node> nodes;

    private final List<Edge> egdes;

    private final StringBuilder builder = new StringBuilder();

    public DotVisualization(Set<Name> baseTypes, PrintingStrategy strategy) {
        this.baseTypes = baseTypes;
        this.strategy = strategy;
        this.nodes = new HashMap<>();
        this.egdes = new ArrayList<>();
    }

    @Override
    public void beginMultispan(Name spanName, List<Name> diagramMorphismNames, List<Name> diagramNodeNames) {
        // Not yet implemented
    }

    @Override
    public void endMultispan() {
        // Not yet implemented
    }

    @Override
    public void beginMorphism(Name morphismName, Name domainName, Name codomainName) {
        // Not yet implemented
    }

    @Override
    public void endMorphism() {
        // Not yet implemented
    }

    @Override
    public void beginGraph(Name graphName) {
        this.builder.append("digraph");
        this.builder.append(' ');
        this.builder.append(graphName.print(strategy));
        this.builder.append(' ');
        this.builder.append('{');
        this.builder.append('\n');
        this.builder.append("edge [fontname=\"Helvetica\",fontsize=10,labelfontname=\"Helvetica\",labelfontsize=10];\n");
        this.builder.append("node [fontname=\"Helvetica\",fontsize=10];\n");
        this.builder.append("nodesep=0.25;\n");
        this.builder.append("ranksep=0.5;\n");
        // Not yet implemented
    }

    @Override
    public void endGraph() {
        for (DotVisualization.Node n : this.nodes.values()) {
            this.builder.append(n.build());
        }
        for (DotVisualization.Edge e : this.egdes) {
            this.builder.append(sanitizeId(e.src.id) +
                    ":p -> " + sanitizeId(e.tgt.id) +
                    ":p [taillabel=\"\", label=\"" +
                    e.label +
                    "\", headlabel=\"\", fontname=\"Helvetica\", fontcolor=\"black\", fontsize=10.0, color=\"black\", arrowhead=vee, arrowtail=none, dir=both];\n");
        }
        this.builder.append('}');
        // Not yet implemented
    }

    @Override
    public void handleTriple(Triple triple) {
        if (this.baseTypes.contains(triple.getLabel())) {
            return;
        }
        if (!this.nodes.containsKey(triple.getSource())) {
            this.nodes.put(triple.getSource(), new Node(triple.getSource().hashCode(), triple.getSource().print(strategy), new ArrayList<>()));
        }
        if (this.baseTypes.contains(triple.getTarget())) {
            this.nodes.get(triple.getSource()).addCompartment(new Pair<>(triple.getLabel().print(strategy), triple.getTarget().print(strategy)));
        } else if (!triple.isNode()) {
            if (!this.nodes.containsKey(triple.getTarget())) {
                this.nodes.put(triple.getTarget(), new Node(triple.getTarget().hashCode(), triple.getTarget().print(strategy), new ArrayList<>()));
            }
            this.egdes.add(new Edge(this.nodes.get(triple.getSource()), this.nodes.get(triple.getTarget()), triple.getLabel().print(strategy)));
        }
    }

    @Override
    public void handleTuple(Tuple tuple) {
        // Not yet implemented
    }

    @Override
    public boolean isOutputReady() {
        return false;
    }

    @Override
    public String getOutput() {
        return builder.toString();
    }
}
