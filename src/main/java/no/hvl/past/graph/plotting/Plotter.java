package no.hvl.past.graph.plotting;

import com.google.common.collect.Sets;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.Visitor;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

import java.io.*;
import java.util.*;

public class Plotter extends AbstractPlotter implements Visitor {

    private enum State {
        INITIAL,
        STARTING_GRAPH,
        STARTED_GRAPH,
        STARTING_MORPHISM,
        STARTED_MORPHISM,
        STARTIN_DIAGRAM,
        STARTED_DIAGRAM

    }

    private static final Set<Class> LABELS_RENDERED_AS_COMPARTMENTS = Sets.newHashSet(
            StringDT.class,
            IntDT.class,
            EnumValue.class,
            FloatDT.class,
            BoolDT.class
    );

    private final boolean ignoreAttributes;
    private final Map<Name, Cluster> subgraphs;
    private final List<Arc> morphismMappings;
    private int clusters = 0;
    private BufferedWriter writer;
    private Stack<State> state;
    private Cluster currentSubgraph;
    private Name currentName;
    private Map<Name, Name> currentMapping;
    private Annotation.SpecialAction currentAction;


    public Plotter(boolean ignoreAttributes, PrintingStrategy printingStrategy) {
        super(0, printingStrategy);
        this.ignoreAttributes = ignoreAttributes;
        this.subgraphs = new HashMap<>();
        this.morphismMappings = new ArrayList<>();
        this.state = new Stack<>();
        this.state.push(State.INITIAL);
    }

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
            Cluster sub = this.subgraphs.get(this.subgraphs.keySet().iterator().next());
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

        for (Arc morph : this.morphismMappings) {
            morph.serialize(writer);
            newLine(writer);
        }

        this.writer.append("}");
        newLine(writer);
    }


    @Override
    public void handleElementName(Name name) {
        if (this.state.peek().equals(State.STARTING_GRAPH)) {
            this.currentSubgraph = new Cluster(clusters++, name, 1, getStrategy(), ignoreAttributes);
            this.subgraphs.put(name, currentSubgraph);
            this.state.push(State.STARTED_GRAPH);
        } else if (this.state.peek().equals(State.STARTING_MORPHISM)) {
            this.currentName = name;
            this.state.push(State.STARTED_MORPHISM);
        } else if (this.state.peek().equals(State.STARTIN_DIAGRAM)) {
            this.currentName = name;
            this.state.push(State.STARTED_DIAGRAM);
        }
    }

    @Override
    public void handleNode(Name node) {
        if (this.state.peek().equals(State.STARTED_GRAPH)) {
            this.currentSubgraph.addNode(node);
        }
    }

    @Override
    public void beginGraph() {
        if (!this.state.peek().equals(State.STARTED_DIAGRAM)) {
            this.state.push(State.STARTING_GRAPH);
        }
    }

    @Override
    public void handleEdge(Triple triple) {
        if (this.state.peek().equals(State.STARTED_GRAPH)) {
            this.currentSubgraph.addEdge(triple);
        }
    }

    @Override
    public void endGraph() {
        if (this.state.peek().equals(State.STARTED_GRAPH)) {
            this.state.pop();
        }
        if (this.state.peek().equals(State.STARTING_GRAPH)) {
            this.state.pop();
        }
    }

    @Override
    public void beginMorphism() {
        if (!this.state.peek().equals(State.STARTED_DIAGRAM)) {
            this.state.push(State.STARTING_MORPHISM);
        }
    }

    @Override
    public void handleMapping(Tuple tuple) {
        if (this.state.peek().equals(State.STARTED_MORPHISM)) {
            Arc morph = new Arc(getIndent() + 1, getStrategy(), tuple.getDomain(), tuple.getCodomain(), this.currentName.print(getStrategy()));
            morph.setLineColor("blue");
            morph.setTextColor("blue");
            this.morphismMappings.add(morph);
        } else if (this.state.peek().equals(State.STARTED_DIAGRAM)) {
            this.currentMapping.put(tuple.getDomain(), tuple.getCodomain());
        }

    }

    @Override
    public void handleFormula(Formula<Graph> graphFormula) {
        this.currentMapping = new HashMap<>();

        if (LABELS_RENDERED_AS_COMPARTMENTS.stream().anyMatch(c -> c.isAssignableFrom(graphFormula.getClass()))) {
            this.currentAction = new Annotation.SpecialAction() {
                @Override
                public void execute(Map<Name, Name> mapping) {
                    currentSubgraph.inlineNode(mapping.get(Universe.ONE_NODE_THE_NODE));
                }
            };
        } else if (SourceMultiplicity.class.isAssignableFrom(graphFormula.getClass())) {
            SourceMultiplicity multiplicity = (SourceMultiplicity) graphFormula;
            String label = (multiplicity.getLowerBound() < 0 ? "*" : Integer.toString(multiplicity.getLowerBound())) + ".." + (multiplicity.getUpperBound() < 0 ? "*" : Integer.toString(multiplicity.getUpperBound()));
            this.currentAction = new Annotation.SpecialAction() {
                @Override
                public void execute(Map<Name, Name> mapping) {
                    currentSubgraph.addSrcLabel(
                            Triple.edge(
                                    mapping.get(Universe.ARROW_SRC_NAME),
                                    mapping.get(Universe.ARROW_LBL_NAME),
                                    mapping.get(Universe.ARROW_TRG_NAME)
                            )
                            , label);
                }
            };
        } else if (TargetMultiplicity.class.isAssignableFrom(graphFormula.getClass())) {
            TargetMultiplicity multiplicity = (TargetMultiplicity) graphFormula;
            String label = (multiplicity.getLowerBound() < 0 ? "*" : Integer.toString(multiplicity.getLowerBound())) + ".." + (multiplicity.getUpperBound() < 0 ? "*" : Integer.toString(multiplicity.getUpperBound()));
            this.currentAction = new Annotation.SpecialAction() {
                @Override
                public void execute(Map<Name, Name> mapping) {
                    currentSubgraph.addTrgLabel(
                            Triple.edge(
                                    mapping.get(Universe.ARROW_SRC_NAME),
                                    mapping.get(Universe.ARROW_LBL_NAME),
                                    mapping.get(Universe.ARROW_TRG_NAME)
                            )
                            , label);
                }
            };
        } else if (Ordered.class.isAssignableFrom(graphFormula.getClass())) {
            this.currentAction = new Annotation.SpecialAction() {

                @Override
                public void execute(Map<Name, Name> mapping) {
                    currentSubgraph.addTrgLabel(
                            Triple.edge(
                                    mapping.get(Universe.ARROW_SRC_NAME),
                                    mapping.get(Universe.ARROW_LBL_NAME),
                                    mapping.get(Universe.ARROW_TRG_NAME)
                            )
                            , "{ordered}");
                }
            };
            // TODO ordered, unique, composition=aggregation plus 1..1 at src, aggregation=acyclic-pred, symmetric=inverse-op
        } else {
            // default: make an annotation
            this.currentAction = new Annotation.SpecialAction() {
                @Override
                public void execute(Map<Name, Name> mapping) {
                    Annotation annotation = new Annotation(getIndent() + 1, getStrategy(), currentName.print(getStrategy()));
                    for (Name key : mapping.keySet()) {
                        if (currentSubgraph.contains(mapping.get(key))) {
                            annotation.addRefersTo(mapping.get(key));
                        }
                    }
                    currentSubgraph.addAnnotation(annotation);
                }
            };
        }

    }

    @Override
    public void endMorphism() {
        if (this.state.peek().equals(State.STARTED_DIAGRAM)) {
            this.currentAction.execute(this.currentMapping);
        }
        if (this.state.peek().equals(State.STARTED_MORPHISM)) {
            this.state.pop();
        }
        if (this.state.pop().equals(State.STARTING_MORPHISM)) {
            this.state.pop();
        }
    }

    @Override
    public void beginDiagram() {
        this.state.push(State.STARTIN_DIAGRAM);
    }

    @Override
    public void endDiagram() {
        if (this.state.peek().equals(State.STARTED_DIAGRAM)) {
            this.state.pop();
        }
        if (this.state.peek().equals(State.STARTIN_DIAGRAM)) {
            this.state.pop();
        }
    }

    // Does not require special attention (yet)

    @Override
    public void beginSketch() {
    }

    @Override
    public void endSketch() {
    }

    @Override
    public void beginSpan() {
    }

    @Override
    public void endSpan() {
    }

}
