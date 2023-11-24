package no.hvl.past.graph.plotting;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

class Cluster extends AbstractPlotter {

    private final int number;
    private final Name name;
    private final Map<Name, Node> nodes;
    private final List<Arc> edges;
    private final List<Annotation> annotations;

    private final boolean ignoreAttributes;
    private boolean skipContext;

    Cluster(
            int number,
            Name name, int indent,
            PrintingStrategy printingStrategy,
            boolean ignoreAttributes) {
        super(indent,printingStrategy);
        this.number = number;
        this.name = name;
        this.ignoreAttributes = ignoreAttributes;
        this.skipContext = false;
        this.nodes = new HashMap<>();
        this.edges = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    void skipContext() {
        this.skipContext = true;
    }

    public void serialize(BufferedWriter sink) throws IOException {
        if (!skipContext) {
            sink.append("subgraph cluster_");
            sink.append(Integer.toString(number));
            sink.append(" {");
            newLine(sink);
            sink.append("label=\"");
            sink.append(this.name.print(getStrategy()));
            sink.append("\";");
            newLine(sink);
            sink.append("style=\"dotted\";");
            newLine(sink);
        }

        newLine(sink);

        Iterator<Name> i = this.nodes.keySet().iterator();
        while (i.hasNext()) {
            Node current = this.nodes.get(i.next());
            current.serialize(sink);
            if (i.hasNext()) {
                current.newLine(sink);
            }
        }

        this.newLine(sink);

        Iterator<Arc> arcIterator = this.edges.iterator();
        while (arcIterator.hasNext()) {
            Arc current = arcIterator.next();
            current.serialize(sink);
            if (arcIterator.hasNext()) {
                current.newLine(sink);
            }
        }

        for (Annotation annotation : this.annotations) {
            annotation.serialize(sink);
            annotation.newLine(sink);

        }

        if (!skipContext) {
            sink.append("};");
        }
    }

    void addNode(Name node) {
        if (!node.isValue()) {
            this.nodes.put(node, new Node(node, getIndent() + 1, getStrategy(), this.ignoreAttributes));
        }
    }

    void addEdge(Triple triple) {
        if (triple.isAttribute()) {
            this.nodes.get(triple.getSource()).addAttribute(triple.getLabel().print(getStrategy()), triple.getTarget().print(getStrategy()));
        } else {
            this.edges.add(new Arc(getIndent() + 1, this.getStrategy(), triple.getSource(), triple.getTarget(), triple.getLabel().print(getStrategy())));
        }
    }

    public void inlineNode(Name name) {
        this.nodes.get(name).setInlined(true);
        for (Arc arc : edges) {
            if (arc.getTo().equals(name)) {
                arc.setInlined(true);
                this.nodes.get(arc.getFrom()).addAttribute(arc.getLabel(), name.print(getStrategy()));
            }
        }
    }

    public void addStereotype(Name node, String stereotype) {
        this.nodes.get(node).setStereotype(stereotype);
    }

    public void addInheritance(Name subtype, Name supertype) {
        Arc inh = new Arc(getIndent() + 1, getStrategy(), subtype, subtype, "");
        inh.setArrowType(Arc.ArrowType.INHERITANCE);
        this.edges.add(inh);
    }

    public void makeComposition(Triple edge) {

    }

    public void makeAggregation(Triple edge) {

    }

    public void addSrcLabel(Triple edge, String label) {
        this.edges.stream()
                .filter(a -> {
                    return a.getFrom().equals(edge.getSource()) && a.getTo().equals(edge.getTarget()) && edge.getLabel().print(getStrategy()).equals(a.getLabel());
                })
                .findFirst()
                .ifPresent(a -> {
                    if (a.getSrcLabel() == null || a.getSrcLabel().isEmpty()) {
                        a.setSrcLabel(label);
                    } else {
                        a.setSrcLabel(a.getSrcLabel() + "\n" + label);
                    }
                });

    }

    public void addTrgLabel(Triple edge, String label) {
        this.edges.stream()
                .filter(a -> {
                    return a.getFrom().equals(edge.getSource()) && a.getTo().equals(edge.getTarget()) && edge.getLabel().print(getStrategy()).equals(a.getLabel());
                 })
                .findFirst()
                .ifPresent(a -> {
                    if (a.getTrgLabel() == null || a.getTrgLabel().isEmpty()) {
                        a.setTrgLabel(label);
                    } else {
                        a.setTrgLabel(a.getSrcLabel() + "\n" + label);
                    }
                });
    }

    public void addSymmetry(Triple one, Triple two) {

    }


    public void addAnnotation(Annotation annotation) {
        this.annotations.add(annotation);
    }

    public boolean contains(Name name) {
        return this.nodes.containsKey(name);
    }
}
