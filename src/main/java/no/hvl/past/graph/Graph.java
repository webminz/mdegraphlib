package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;
import no.hvl.past.util.SearchEngine;
import no.hvl.past.util.SearchStrategy;
import no.hvl.past.util.StateSpace;

import java.util.*;
import java.util.stream.Collectors;


public class Graph implements StateSpace<Name>, AbstractGraph {

    private final Name name;

    private final Set<Triple> elements;


    public static class Builder {
        private Name name;
        private ArrayList<Triple> aggregator;

        public Builder(Name name) {
            this.name = name;
            this.aggregator = new ArrayList<>();
        }

        public Builder(String name) {
            this(Name.identifier(name));
        }

        /**
         * Constructs a node.
         */
        public Builder node(String name) {
            Name nodeName = Name.identifier(name);
            Triple toAdd = Triple.fromNode(nodeName);
            if (aggregator.contains(toAdd)) {
                return this;
            }
            aggregator.add(toAdd);
            return this;
        }

        /**
         * Constructs an edge (and its content if necessary).
         */
        public Builder edge(String from, String label, String to) {
            Name fromName = Name.identifier(from);
            Name labelName = Name.identifier(label);
            Name toName = Name.identifier(to);
            return edge(fromName, labelName, toName);
        }

        public Builder edge(Name from, Name label, Name to) {
            if (aggregator.contains(new Triple(from, label, to))) {
                return this;
            }
            if (!aggregator.contains(Triple.fromNode(from))) {
                aggregator.add(Triple.fromNode(from));
            }
            if (!aggregator.contains(Triple.fromNode(to))) {
                aggregator.add(Triple.fromNode(to));
            }
            aggregator.add(new Triple(from, label, to));
            return this;
        }


        public Graph build() {
            return new Graph(name, new HashSet<>(aggregator));
        }


    }

    public Set<Triple> getElements() {
        return elements;
    }

    Graph(Name name, Set<Triple> elements) {
        this.name = name;
        this.elements = elements;
    }

    public Name getName() {
        return name;
    }


    public Set<Tuple> getIdentityMapping() {
        return this.elements.stream().map(t -> new Tuple(t.getLabel(), t.getLabel())).collect(Collectors.toSet());
    }

    @Override
    public Morphism identity() {
        return new Morphism(this.name, this, this, getIdentityMapping());
    }

    Graph prefix() {
        return new Graph(name, elements.stream().map(t -> t.prefix(getName())).collect(Collectors.toSet()));
    }

    private Graph unprefix() {
        return new Graph(name, elements.stream().map(t -> t.unprefix(getName())).collect(Collectors.toSet()));
    }

    public Set<Triple> getEdges() {
        return elements.stream().filter(Triple::isEddge).collect(Collectors.toSet());
    }

    public Set<Name> getNodes() {
        return elements.stream().filter(Triple::isNode).map(Triple::getLabel).collect(Collectors.toSet());
    }

    @Override
    public boolean contains(Triple triple) {
        return this.elements.contains(triple);
    }

    @Override
    public boolean contains(Name name) {
        return this.elements.stream().map(Triple::getLabel).anyMatch(l -> l.equals(name));
    }

    @Override
    public Set<Triple> outgoing(Name from) {
        return this.elements.stream().filter(e -> e.getSource().equals(from)).collect(Collectors.toSet());
    }

    @Override
    public Set<Triple> incoming(Name to) {
        return this.elements.stream().filter(e -> e.getTarget().equals(to)).collect(Collectors.toSet());
    }

    @Override
    public Iterator<Triple> iterator() {
        return elements.iterator();
    }

    public boolean existsPath(Name fromNode, Name toNode) {
        return new SearchEngine<Name>(this, SearchStrategy.EXPLORATION)
                .search(fromNode, n -> n.equals(toNode)).isPresent();
    }

    /**
     * Searches for all possible matches of the other graphs in this graph.
     */
    public Set<Morphism> findMatches(Graph other) {
        return Collections.emptySet(); // TODO
    }


    @Override
    public Spliterator<Triple> spliterator() {
        return this.elements.spliterator();
    }

    public Graph sum(Graph other) {
        Set<Triple> elements = new HashSet<>();
        elements.addAll(this.prefix().elements);
        elements.addAll(other.prefix().elements);
        return new Graph(this.name.sum(other.name), elements);
    }

    public Graph sum(List<Graph> diagramNodes) {
        Set<Triple> elements = new HashSet<>();
        elements.addAll(this.prefix().elements);
        for (Graph g : diagramNodes) {
            elements.addAll(g.prefix().elements);
        }
        List<Name> collect = diagramNodes.stream().map(Graph::getName).collect(Collectors.toList());
        Name[] names = new Name[collect.size()];
        names = collect.toArray(names);
        return new Graph(this.name.merge(names), elements);
    }

    @Override
    public List<Name> step(Name current) {
        return this.elements.stream().filter(t -> t.getSource().equals(current)).map(Triple::getTarget).collect(Collectors.toList());
    }

    @Override
    public boolean isInfinite() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Graph) { // Equality of graphs is based on their name, therefore it is important to make sure that they have unique names
            Graph other = (Graph) obj;
            return this.name.equals(other.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }


    private static Set<Triple> getDanglingEdges(Graph g) {
        return g.elements.stream().filter(e -> !g.getNodes().contains(e.getSource()) || !g.getNodes().contains(e.getTarget())).collect(Collectors.toSet());
    }

    public static Graph create(Name name, Set<Triple> elements) throws GraphError {
        Graph result = new Graph(name, elements);
        Set<Triple> danglingEdges = getDanglingEdges(result);
        if (!danglingEdges.isEmpty()) {
            throw new GraphError(GraphError.ERROR_TYPE.DANGLING_EDGE, danglingEdges);
        }
        return result;
    }
}
