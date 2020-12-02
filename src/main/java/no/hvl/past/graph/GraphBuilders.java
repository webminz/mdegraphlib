package no.hvl.past.graph;

import com.google.common.collect.Sets;
import jdk.nashorn.internal.objects.AccessorPropertyDescriptor;
import no.hvl.past.attributes.StringValue;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import no.hvl.past.util.Pair;


import java.util.*;
import java.util.stream.Collectors;

public class GraphBuilders {

    private static final Name TEMPLATE_GRAPH_NAME = Name.identifier("__TEMPLATE");

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

    private final Stack<Class<? extends Element>> resultTypeStack = new Stack<>();

    public GraphBuilders(Universe universe, boolean createContext, boolean ignoreErrors) {
        this.universe = universe;
        this.createContext = createContext;
        this.ignoreErrors = ignoreErrors;
    }

    public GraphBuilders() {
        this.createContext = true;
        this.ignoreErrors = true;
    }

    private boolean isBuildingDomain() {
        return this.createContext && (this.graphAggregator.isEmpty() || this.graphAggregator.get(0).getName().equals(TEMPLATE_GRAPH_NAME));
    }

    private boolean isBuildingCodomain() {
        return this.createContext && (this.graphAggregator.isEmpty() || this.graphAggregator.size() == 1 || this.graphAggregator.get(1).getName().equals(TEMPLATE_GRAPH_NAME));
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
        if (createContext && !nodeAggregator.contains(from)) {
                this.nodeAggregator.add(from);
        }
        if (createContext && !nodeAggregator.contains(to)) {
                this.nodeAggregator.add(to);
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

        this.nodeAggregator.clear();
        this.edgeAggregator.clear();

        if (!ignoreErrors && !constructed.verify()) {
            constructed.duplicateNames().map(n -> new Pair<>(n, GraphError.ERROR_TYPE.DUPLICATE_NAME)).forEach(this.errors::add);
            constructed.danglingEdges().map(n -> new Pair<>(n.getLabel(), GraphError.ERROR_TYPE.DANGLING_EDGE)).forEach(this.errors::add);
            return this;
        }

        this.graphAggregator.add(constructed);
        this.resultTypeStack.push(Graph.class);
        this.universe.register(constructed);
        return this;
    }

    public GraphBuilders graph(String identifier) {
        return graph(Name.identifier(identifier));
    }

    public GraphBuilders domain(Graph graph) {
        this.graphAggregator.add(0, graph);
        return this;
    }

    public GraphBuilders domain(Name name) {
        if (this.universe.getTypeOfElement(name).map(FrameworkElement.GRAPH::equals).orElse(false)) {
            this.graphAggregator.add(0, (Graph) this.universe.getElement(name).get());
        } else {
            this.errors.add(new Pair<>(name, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
        }
        return this;
    }

    public GraphBuilders codomain(Graph graph) {
        if (this.graphAggregator.isEmpty()) {
            this.graphAggregator.add(new GraphImpl(TEMPLATE_GRAPH_NAME, Collections.emptySet()));
        }
        this.graphAggregator.add(1, graph);
        return this;
    }

    public GraphBuilders codomain(Name identifier) {
        if (this.universe.getTypeOfElement(identifier).map(FrameworkElement.GRAPH::equals).orElse(false)) {
            this.graphAggregator.add(1, (Graph) this.universe.getElement(identifier).get());
        } else {
            this.errors.add(new Pair<>(identifier, GraphError.ERROR_TYPE.UNKNOWN_MEMBER));
        }
        return this;
    }


    public GraphBuilders map(Name from, Name to) {
        if (this.bindingAggregator.containsKey(from) && !ignoreErrors) {
            if (!this.bindingAggregator.get(from).equals(to)) {
                this.errors.add(new Pair<>(from, GraphError.ERROR_TYPE.AMBIGUOS_MAPPING));
                return this;
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

        if (isBuildingDomain()) {
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
        }

        if (isBuildingCodomain()) {
            Set<Triple> codTriples = this.graphAggregator.get(0).elements().map(triple -> triple.map(n -> {
                if (this.bindingAggregator.containsKey(n)) {
                    return Optional.of(this.bindingAggregator.get(n));
                } else {
                    return Optional.empty();
                }
            })).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
            this.graphAggregator.add(new GraphImpl(Name.identifier("cod").appliedTo(morphismName), codTriples));
        }

        if (this.graphAggregator.size() < 2) {
            this.errors.add(new Pair<>(morphismName, GraphError.ERROR_TYPE.ILL_FORMED));
            return this;
        }

        GraphMorphism constructed = new GraphMorphismImpl(morphismName, this.graphAggregator.get(0), this.graphAggregator.get(1), binding);
        this.graphAggregator.remove(1);
        this.graphAggregator.remove(0);
        this.bindingAggregator.clear();

        if (!ignoreErrors && ! constructed.verify()) {
           constructed.mappedToUndefined().map(t -> new Pair<>(t.getLabel(), GraphError.ERROR_TYPE.UNKNOWN_MEMBER)).forEach(this.errors::add);
           constructed.mappedToUndefined().map(t -> new Pair<>(t.getLabel(), GraphError.ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION)).forEach(this.errors::add);
            return this;
        }

        this.universe.register(constructed);
        this.morphismAggregator.add(constructed);
        this.resultTypeStack.add(GraphMorphism.class);
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
        this.resultTypeStack.push(Diagram.class);
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
        this.resultTypeStack.push(Sketch.class);
        return this;
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


    public GraphBuilders attribute(String owner, String attributeName, Value value) {
        return this.edge(Name.identifier(owner), Name.identifier(attributeName).prefixWith(Name.identifier(owner)), value);
    }

    public GraphBuilders inheritanceGraph(String name) {
        return this.inheritanceGraph(Name.identifier(name));
    }

    public GraphBuilders inheritanceGraph(Name name) {
        this.graph(name);
        if (!graphAggregator.isEmpty()) {
            Graph base = graphAggregator.get(graphAggregator.size() - 1);
            graphAggregator.remove(graphAggregator.size() - 1);
            graphAggregator.add(new InheritanceAugmentedGraph(base, Tuple.fromMap(bindingAggregator)));
            this.resultTypeStack.push(InheritanceGraph.class);
        }
        return this;
    }

    public <E extends Element> E getResult(Class<E> type) throws GraphError {
        if (!this.errors.isEmpty()) {
            throw new GraphError(errors);
        }

        if (this.resultTypeStack.isEmpty() || !type.isAssignableFrom(this.resultTypeStack.peek())) {
            throw new GraphError(GraphError.ERROR_TYPE.NOT_CONSTRUCTED, Collections.emptySet());
        }

        this.resultTypeStack.pop();

        Element result = null;

        if (Graph.class.isAssignableFrom(type)) {
            result = this.graphAggregator.get(this.graphAggregator.size() - 1);
            this.graphAggregator.remove(this.graphAggregator.size() - 1);
        } else if (GraphMorphism.class.isAssignableFrom(type)) {
            result = this.morphismAggregator.get(this.morphismAggregator.size() - 1);
            this.morphismAggregator.remove(this.morphismAggregator.size() - 1);
        } else if (Sketch.class.isAssignableFrom(type)) {
            result = this.sketchAggregator.get(this.sketchAggregator.size() - 1);
            this.sketchAggregator.remove(this.sketchAggregator.size() - 1);
        } else { // TODO other framework elements
            throw new GraphError(GraphError.ERROR_TYPE.NOT_CONSTRUCTED, Collections.emptySet());
        }

        try {
            return (E) result;
        } catch (ClassCastException e) {
            throw new GraphError(Collections.singletonList(new Pair<>(result.getName(), GraphError.ERROR_TYPE.ILL_FORMED)));
        }
    }
}
