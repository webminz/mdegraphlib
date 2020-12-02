package no.hvl.past.plotting;

import no.hvl.past.graph.Visitor;
import no.hvl.past.graph.elements.EquivalenceClass;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOTWriter extends AbstractDOTWriter implements Visitor {

    private enum State {
        INITIAL,
        STARTING_GRAPH,
        STARTED_GRAPH,
        STARTING_MORPHISM,
        STARTED_MORPHISM
    }

    private final boolean ignoreAttributes;
    private final PrintingStrategy printingStrategy;
    private final Map<Name, DOTSubgraph> subgraphs;
    private final List<Tuple> morphismMappings;

    private int clusters = 0;


    public DOTWriter(boolean ignoreAttributes, PrintingStrategy printingStrategy) {
        super(0);
        this.ignoreAttributes = ignoreAttributes;
        this.printingStrategy = printingStrategy;
        this.subgraphs = new HashMap<>();
        this.morphismMappings = new ArrayList<>();
        this.state = State.INITIAL;
    }

    private BufferedWriter writer;
    private State state;
    private DOTSubgraph currentSubgraph;

    public void writeToFile(File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            this.writer = new BufferedWriter(fw);
            this.serialize(this.writer);
            this.close();
        } catch (IOException e) {
            throw e;
        }
    }

    public void close() throws IOException {
        if (this.writer != null) {
            this.writer.flush();
            this.writer.close();
        }
    }

    public void serialize(BufferedWriter writer) throws IOException {
        this.writer.append("digraph {");
        newLine(writer);

        if (this.subgraphs.keySet().size() == 1) {
            DOTSubgraph sub = this.subgraphs.get(this.subgraphs.keySet().iterator().next());
            sub.skipContext();
            sub.serialize(writer);
            newLine(writer);
        } else {
            for (Name n : this.subgraphs.keySet()) {
                this.subgraphs.get(n).serialize(writer);
                newLine(writer);
            }
        }

        newLine(writer);

        for (Tuple t : this.morphismMappings) {
            writer.append('"');
            writer.append(t.getDomain().print(printingStrategy));
            writer.append("\" -> \"");
            writer.append(t.getCodomain().print(printingStrategy));
            writer.append("\" [arrowhead=\"lvee\" style=\"dashed\" color=\"blue\"];");
            newLine(writer);
        }

        // TODO morphisms

        this.writer.append("}");
        newLine(writer);
    }


    @Override
    public void handleName(Name name) {
        if (this.state == State.STARTING_GRAPH) {
            this.currentSubgraph = new DOTSubgraph(clusters++, name, 1, printingStrategy, ignoreAttributes);
            this.subgraphs.put(name, currentSubgraph);
            this.state = State.STARTED_GRAPH;
        } else if (this.state == State.STARTING_MORPHISM) {
            this.state = State.STARTED_MORPHISM;
        }
    }

    @Override
    public void beginGraph() {
        this.state = State.STARTING_GRAPH;
    }

    @Override
    public void handleTriple(Triple triple) {
        if (this.state == State.STARTED_GRAPH) {
            this.currentSubgraph.handleTriple(triple);
        }

    }

    @Override
    public void endGraph() {
        this.state = State.INITIAL;
    }

    @Override
    public void beginMorphism() {
        this.state = State.STARTING_MORPHISM;
    }

    @Override
    public void handleTuple(Tuple tuple) {
        if (this.state == State.STARTED_MORPHISM) {
            this.morphismMappings.add(tuple);
        }
    }

    @Override
    public void endMorphism() {
        this.state = State.INITIAL;
    }

    @Override
    public void beginSketch() {

    }

    @Override
    public void beginDiagram() {

    }

    @Override
    public void endDiagram() {

    }

    @Override
    public void endSketch() {

    }

    @Override
    public void beginSketchMorphism() {

    }

    @Override
    public void endSketchMorphism() {

    }

    @Override
    public void beginSpan() {

    }

    @Override
    public void handleClass(EquivalenceClass clazz) {

    }

    @Override
    public void endSpan() {

    }

    @Override
    public void beginSpanMorphism() {

    }

    @Override
    public void endSpanMorphism() {

    }

    @Override
    public void handleDiagramLabel(Name name) {

    }
}
