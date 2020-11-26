package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;


import java.util.*;
import java.util.stream.Collectors;

public class GraphBuilders {

    /**
     * When this flag is set to true, upon creation of new elements the missing context is automatically
     * created e.g. the target node is created for an edge if its not preexisting.
     */
    private boolean createContext;

    /**
     * When this flag is set to true, the builder behaves rather resilient and simply ignores creations
     * that would cause new inconsistencies. The invalid command then has no effect.
     */
    private boolean ignoreErrors;

    /**
     * The universe for this graph builders.
     */
    private Universe universe;

    private final List<Pair<Name, GraphError.ERROR_TYPE>> errors = new ArrayList<>();

    private final Set<Name> nodeAggregator = new HashSet<>();
    private final Set<Triple> edgeAggregator = new HashSet<>();
    private final List<Graph> graphAggregator = new ArrayList<>();
    private final Map<Name, Name> bindingAggregator = new HashMap<>();
    private final List<GraphMorphism> morphismAggregator = new ArrayList<>();
    private final List<Diagram> diagramAggregator = new ArrayList<>();
    private final List<Sketch> sketchAggregator = new ArrayList<>();
    private final Stack<GraphLabelTheory> labelStack = new Stack<>();

    private final Stack<FrameworkElement> resultTypeStack = new Stack<>();

    public GraphBuilders(Universe universe, boolean createContext, boolean ignoreErrors) {
        this.universe = universe;
        this.createContext = createContext;
        this.ignoreErrors = ignoreErrors;
    }

    public GraphBuilders() {
        this.createContext = true;
        this.ignoreErrors = true;
    }

    /**
     * Constructs a node with the given name.
     */
    public GraphBuilders node(Name nodeName) {
        nodeAggregator.add(nodeName);
        return this;
    }


    /**
     * Constructs a node where the given string is interpreted as an identifier.
     */
    public GraphBuilders node(String name) {
        Name nodeName = Name.identifier(name);
        return node(nodeName);
    }

    /**
     * Constructs an edge.
     */
    public GraphBuilders edge(Name from, Name label, Name to) {
        if (edgeAggregator.stream().anyMatch(t -> t.getLabel().equals(label))) {
            if (!ignoreErrors) {
                this.errors.add(new Pair<>(label, GraphError.ERROR_TYPE.DUPLICATE_NAME));
            }
        }
        if (!nodeAggregator.contains(from)) {
            if (createContext) {
                this.nodeAggregator.add(from);
            } else if (!ignoreErrors) {
                this.errors.add(new Pair<>(label, GraphError.ERROR_TYPE.DANGLING_EDGE));
            }
        }
        if (!nodeAggregator.contains(to)) {
            if (createContext) {
                this.nodeAggregator.add(to);
            } else if (!ignoreErrors) {
                this.errors.add(new Pair<>(label, GraphError.ERROR_TYPE.DANGLING_EDGE));
            }
        }
        edgeAggregator.add(new Triple(from, label, to));
        return this;
    }

    /**
     * Constructs an edge.
     * Names are given as Strings and interpreted as simple identifiers.
     */
    public GraphBuilders edge(String from, String label, String to) {
        Name fromName = Name.identifier(from);
        Name labelName = Name.identifier(label);
        Name toName = Name.identifier(to);
        return edge(fromName, labelName, toName);
    }

    /**
     * Constructs a graph with the given name and the previously constructed nodes and edges.
     */
    public GraphBuilders graph(Name name) {
        Set<Triple> elements = new HashSet<>();
        elements.addAll(nodeAggregator.stream().map(Triple::node).collect(Collectors.toSet()));
        elements.addAll(edgeAggregator);
        Graph constructed = new GraphImpl(name, elements);
        this.graphAggregator.add(constructed);
        this.resultTypeStack.push(FrameworkElement.GRAPH);
        this.nodeAggregator.clear();
        this.edgeAggregator.clear();
        return this;
    }

    public GraphBuilders graph(String identifier) {
        return graph(Name.identifier(identifier));
    }

    public GraphBuilders domain(Graph graph) {
        this.graphAggregator.add(0, graph);
        return this;
    }

    public GraphBuilders codomain(Graph graph) {
        if (this.graphAggregator.isEmpty()) {
            this.graphAggregator.add(Universe.EMPTY);
        }
        this.graphAggregator.add(1, graph);
        return this;
    }

    public GraphBuilders codomain(Name identifier) {
        // TODO
        return this;
    }



    public GraphBuilders map(Name from, Name to) {
        // Already there
        if (bindingAggregator.containsKey(from)) {
            if (!bindingAggregator.get(from).equals(to)) {
                if (!ignoreErrors) {
                    this.errors.add(new Pair<>(from, GraphError.ERROR_TYPE.AMBIGUOS_MAPPING));
                }
                return this;
            }
        }

        // We have no graphs given
        if (this.graphAggregator.isEmpty()) {
            if (createContext) {
                this.bindingAggregator.put(from, to);
            } else if (!this.ignoreErrors) {
                this.errors.add(new Pair<>(from, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
            }
            return this;
        }


        // We have no codomain
        if (this.graphAggregator.size() <= 1) {
            if (createContext) {
                this.bindingAggregator.put(from, to);
            } else if (!ignoreErrors) {
                this.errors.add(new Pair<>(to, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
            }
            return this;
        }

        // Okay we have both proceed regularly ...

        // if we have a domain we can check if the 'from' name actually exists there
        if (this.graphAggregator.get(0).isEmpty() && createContext) {
            if (!ignoreErrors && (!this.nodeAggregator.contains(from) && this.edgeAggregator.stream().map(Triple::getLabel).noneMatch(from::equals))) {
                this.errors.add(new Pair<>(from, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
                return this;
            }
        } else {
            if (!ignoreErrors && !this.graphAggregator.get(0).mentions(from)) {
                this.errors.add(new Pair<>(from, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
                return this;
            }
        }


        // if we have a codomain we can check if the 'to' name actually exists there
        if (this.graphAggregator.size() >= 2) {
            if (!ignoreErrors && !this.graphAggregator.get(1).mentions(to)) {
                this.errors.add(new Pair<>(to, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
                return this;
            }
        }

        // when we have both domain and codomain we check for the homomorphism property
        if (this.graphAggregator.size() >= 2) {
            Optional<Triple> fromTriple;
            if (this.graphAggregator.get(0).isEmpty()) {
                fromTriple = this.edgeAggregator.stream().filter(triple -> triple.getLabel().equals(from)).findFirst();
            } else {
                fromTriple = this.graphAggregator.get(0).get(from);
            }

            if (fromTriple.isPresent() && fromTriple.get().isEddge() && this.graphAggregator.get(1).mentions(to)) {
                Optional<Triple> toTriple = this.graphAggregator.get(1).get(to);
                boolean hasError = false;
                if (this.bindingAggregator.containsKey(fromTriple.get().getSource()) &&
                        !this.bindingAggregator.get(fromTriple.get().getSource()).equals(toTriple.get().getSource())) {
                    hasError = true;
                }
                if (this.bindingAggregator.containsKey(fromTriple.get().getTarget()) &&
                        !this.bindingAggregator.get(fromTriple.get().getTarget()).equals(toTriple.get().getTarget())) {
                    hasError = true;
                }
                if (hasError) {
                    if (!ignoreErrors) {
                        this.errors.add(new Pair<>(from, GraphError.ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION));
                    }
                    return this;
                }
            }

        }

        this.bindingAggregator.put(from, to);
        return this;
    }

    public GraphBuilders map(String fromIdentifier, String toIdentifier) {
        return this.map(Name.identifier(fromIdentifier), Name.identifier(toIdentifier));
    }

    public GraphBuilders morphism(Name morphismName) {
        Map<Name, Name> binding = new HashMap<>(this.bindingAggregator);

        if (createContext) {
            if (this.graphAggregator.isEmpty()) {
                this.bindingAggregator.keySet().stream().forEach(from -> {
                    if (!this.nodeAggregator.contains(from) && this.edgeAggregator.stream().map(Triple::getLabel).noneMatch(from::equals)) {
                        this.nodeAggregator.add(from);
                    }
                });
                this.graph(Name.identifier("dom").appliedTo(morphismName));
            } else if (this.graphAggregator.get(0).isEmpty() && !this.bindingAggregator.isEmpty()) {
                this.graph(Name.identifier("dom").appliedTo(morphismName));
            }

            if (this.graphAggregator.size() <= 1) {
                Set<Triple> codTriples = this.graphAggregator.get(0).elements().map(triple -> triple.map(n -> {
                    if (this.bindingAggregator.containsKey(n)) {
                        return Optional.of(this.bindingAggregator.get(n));
                    } else {
                        return Optional.empty();
                    }
                })).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
                this.graphAggregator.add(new GraphImpl(Name.identifier("cod").appliedTo(morphismName), codTriples));
            }
        }

        if (this.graphAggregator.size() < 2) {
            if (!ignoreErrors) {
                this.errors.add(new Pair<>(morphismName, GraphError.ERROR_TYPE.ILL_FORMED));
            }
            return this;
        }

        GraphMorphism constructed = new GraphMorphismImpl(morphismName, this.graphAggregator.get(0), this.graphAggregator.get(1), binding);
        this.graphAggregator.remove(1);
        this.graphAggregator.remove(0);
        this.bindingAggregator.clear();
        this.morphismAggregator.add(constructed);
        this.resultTypeStack.add(FrameworkElement.GRAPH_MORPHISM);
        return this;
    }

    public GraphBuilders morphism(String morphismName) {
        return morphism(Name.identifier(morphismName));
    }


    public GraphBuilders startDiagram(GraphLabelTheory label) {
        this.graphAggregator.add(0, label.arity());
        this.labelStack.push(label);
        return this;
    }


    public GraphBuilders endDiagram(Name diagramName) {
        if (labelStack.isEmpty()) {
            this.errors.add(new Pair<>(diagramName, GraphError.ERROR_TYPE.ILL_FORMED));
            return this;
        }
        Graph target = this.graphAggregator.get(1);
        this.morphism(diagramName.absolute());
        this.resultTypeStack.pop();
        Diagram diagram = new DiagramImpl(diagramName, this.labelStack.pop(), this.morphismAggregator.get(this.morphismAggregator.size() - 1));
        this.diagramAggregator.add(diagram);
        this.graphAggregator.add(0, target);
        this.morphismAggregator.remove(this.morphismAggregator.size() - 1);
        this.resultTypeStack.push(FrameworkElement.DIAGRAM);
        return this;
    }

    public GraphBuilders sketch(String nameAsIdentifier) {
        return sketch(Name.identifier(nameAsIdentifier));
    }

    public GraphBuilders sketch(Name name) {
        if (this.graphAggregator.isEmpty()) {
            this.graph(name.absolute());
        }
        List<Diagram> diagrams = new ArrayList<>(this.diagramAggregator);
        Sketch constructed = new DiagrammaticGraph(name, this.graphAggregator.get(0), diagrams);
        this.sketchAggregator.add(constructed);
        this.diagramAggregator.clear();
        this.graphAggregator.remove(0);
        while (this.resultTypeStack.peek().equals(FrameworkElement.DIAGRAM)) {
            this.resultTypeStack.pop();
        }
        if (this.resultTypeStack.peek().equals(FrameworkElement.GRAPH)) {
            this.resultTypeStack.pop();
        }
        this.resultTypeStack.push(FrameworkElement.SKETCH);
        return this;
    }

    Graph getGraphResult() {
        Graph result = this.graphAggregator.get(this.graphAggregator.size() - 1);
        this.graphAggregator.remove(this.graphAggregator.size() - 1);
        return result;
    }

    GraphMorphism getMorphismResult() {
        GraphMorphism result = this.morphismAggregator.get(this.morphismAggregator.size() - 1);
        this.morphismAggregator.remove(this.morphismAggregator.size() - 1);
        return result;
    }

    Sketch getSketchResult() {
        Sketch result = this.sketchAggregator.get(this.sketchAggregator.size() - 1);
        this.sketchAggregator.remove(this.sketchAggregator.size() - 1);
        return result;
    }


    public Graph fetchResultGraph() throws GraphError {
        if (!this.errors.isEmpty()) {
            throw new GraphError(this.errors);
        }
        if (!this.resultTypeStack.isEmpty() && this.resultTypeStack.peek().equals(FrameworkElement.GRAPH)) {
            this.resultTypeStack.pop();
            return getGraphResult();
        } else {
            throw new GraphError(GraphError.ERROR_TYPE.ILL_FORMED, Collections.emptySet());
        }
    }

    public GraphMorphism fetchResultMorphism() throws GraphError {
        if (!this.errors.isEmpty()) {
            throw new GraphError(this.errors);
        }
        if (!this.resultTypeStack.isEmpty() && this.resultTypeStack.peek().equals(FrameworkElement.GRAPH_MORPHISM)) {
            this.resultTypeStack.pop();
            return getMorphismResult();
        } else {
            throw new GraphError(GraphError.ERROR_TYPE.ILL_FORMED, Collections.emptySet());
        }
    }

    public Sketch fetchResultSketch() throws GraphError {
        if (!this.errors.isEmpty()) {
            throw new GraphError(errors);
        }
        if (!this.resultTypeStack.isEmpty() && this.resultTypeStack.peek().equals(FrameworkElement.SKETCH)) {
            this.resultTypeStack.pop();
            return getSketchResult();
        } else {
            throw new GraphError(GraphError.ERROR_TYPE.ILL_FORMED, Collections.emptySet());
        }

    }


    public GraphBuilders importGraph(Graph domain) {
        this.nodeAggregator.addAll(domain.nodes().collect(Collectors.toSet()));
        this.edgeAggregator.addAll(domain.edges().collect(Collectors.toSet()));
        return this;
    }

    public GraphBuilders typedEdge(Triple instance, Triple type) {
        if (this.graphAggregator.size() > 1 || this.createContext) {
            if (this.graphAggregator.get(1).contains(type) || this.ignoreErrors) {
                this.edgeAggregator.add(instance);
                this.map(instance.getSource(), type.getSource());
                this.map(instance.getLabel(), type.getLabel());
                this.map(instance.getTarget(), type.getTarget());
            } else {
                this.errors.add(new Pair<>(type.getLabel(), GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
                return this;
            }
        }
        return this;
    }

    public void clear() {
        this.errors.clear();
        this.nodeAggregator.clear();
        this.edgeAggregator.clear();
        this.bindingAggregator.clear();
        this.diagramAggregator.clear();
        this.graphAggregator.clear();
        this.morphismAggregator.clear();
        this.sketchAggregator.clear();
    }


}
