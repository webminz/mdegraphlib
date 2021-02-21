package no.hvl.past.graph;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.logic.Model;
import no.hvl.past.logic.Signature;
import no.hvl.past.names.Name;
import no.hvl.past.searching.SearchEngine;
import no.hvl.past.searching.StateSpace;
import no.hvl.past.util.StreamExt;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A directed multigraph consisting of nodes and edges.
 * Nodes and Edges have an identity (Edges have a label).
 * Graphs are the lingua franca in Computer Science and Software Engineering,
 * they can represent all relevant artifacts, i.e.
 * Software (Meta-)models, source code (via the AST), XML documents, Database Schema etc.
 */
public interface Graph extends Element, StateSpace<Name, Triple>, Signature, Formula<Graph> {

    /**
     * Provides a stream with all graph elements (triples).
     * If this is a big graph, this operation could be rather expensive.
     */
    Stream<Triple> elements();

    /**
     * Returns true if this graph contains this edge.
     */
    default boolean contains(Triple edge) {
        return elements().anyMatch(edge::equals);
    }

    /**
     * Returns true if this graph contains the given node.
     */
    default boolean containsNode(Name node) {
        return elements().filter(Triple::isNode).anyMatch(t -> t.getLabel().equals(node));
    }

    /**
     * Returns true if the given name *appears* somewhere in this graph.
     */
    default boolean mentions(Name name) {
        return elements().flatMap(Triple::parts).anyMatch(name::equals);
    }

    /**
     * Returns all nodes in this graph.
     */
    default Stream<Name> nodes() {
        return elements().filter(Triple::isNode).map(Triple::getLabel);
    }

    /**
     * Returns all (propert) edges in this graph.
     */
    default Stream<Triple> edges() {
        return elements().filter(Triple::isEddge);
    }


    /**
     * Returns true if this graph is empty.
     */
    default boolean isEmpty() {
        return this.elements().count() == 0;
    }


    /**
     * Returns true if this graph is discrete, i.e. actually a set.
     */
    default boolean isDiscrete() {
        return this.edges().count() == 0;
    }

    /**
     * Retrieves all edges that have the given node as source.
     * The node is given by its name.
     */
    default Stream<Triple> outgoing(Name fromNode) {
        return elements().filter(t -> t.getSource().equals(fromNode));
    }

    /**
     * Retrieves all edges that have the given node as target.
     * The node is given by its name.
     */
    default Stream<Triple> incoming(Name toNode) {
        return elements().filter(t -> t.getTarget().equals(toNode));
    }

    /**
     * Returns (if existent) the triple where the name of the label is equal
     * to the given name.
     * This is a convenient methods as is is often necessary to access
     * an edge without explicitly knowing its source or target.
     */
    default Optional<Triple> get(Name label) {
        return elements().filter(t -> t.getLabel().equals(label)).findFirst();
    }

    /**
     * Checks if the given name is the name of a node present in this graph.
     */
    default boolean isNode(Name name) {
        return get(name).map(Triple::isNode).orElse(false);
    }

    /**
     * Checks if the given name is the name of an edge present in this graph.
     */
    default boolean isEdge(Name name) {
        return get(name).map(Triple::isEddge).orElse(false);
    }

    /**
     * A stream of all edges in this graph, which are dangling, i.e.
     * that have a unknown source or target.
     * In a well-formed graph this stream should be empty.
     */
    default Stream<Triple> danglingEdges() {
        return this.edges()
                .filter(e -> !this.contains(Triple.node(e.getSource())) ||
                        !this.contains(Triple.node(e.getTarget())));
    }

    /**
     * A stream of all names that are ambiguous in this graph.
     * In a well-formed graph, this stream should be empty.
     */
    default Stream<Name> duplicateNames() {
        Multiset<Name> m = HashMultiset.create();
        elements().map(Triple::getLabel).forEach(m::add);
        return m.elementSet().stream().filter(n -> m.count(n) > 1);
    }

    /**
     * Checks if there exists a connection between the two given nodes
     * via searching.
     */
    default boolean existsPath(Name fromNode, Name toNode) {
        if (fromNode.equals(toNode)) {
            return true;
        }
        return new SearchEngine<>(this)
                .simpleSearch(fromNode, node -> node.equals(toNode))
                .isPresent();
    }

    @Override
   default boolean isSyntacticallyCorrect(Model<? extends Signature> model) {
        if (model instanceof GraphMorphism) {
            GraphMorphism morphism = (GraphMorphism) model;
            return morphism.codomain().equals(this) && morphism.verify();
        }
        return false;
    }


    // inherited methods to implement

    // From element

    @Override
    default void accept(Visitor visitor) {
        visitor.beginGraph();
        visitor.handleElementName(getName());
        nodes().forEach(visitor::handleNode);
        edges().forEach(visitor::handleEdge);
        visitor.endGraph();
    }

    default boolean verify() {
        return this.danglingEdges().count() == 0 && this.duplicateNames().count() == 0;
    }


    // from state space

    @Override
    default List<Triple> availableActions(Name current) {
        return this.outgoing(current).collect(Collectors.toList());
    }

    @Override
    default Optional<Name> applyAction(Name current, Triple action) {
        if (action.getSource().equals(current)) {
            return Optional.of(action.getTarget());
        }
        return Optional.empty();
    }


    // From Formula

    @Override
    default boolean isSatisfied(Model<Graph> model) {
        if (model instanceof GraphMorphism) {
            GraphMorphism m = (GraphMorphism) model;
            if (m.codomain().equals(this)) {
                return m.verify(); // Every well-defined graph morphism that has this graph as codomain is a model of it.
            }
        }
        return false;
    }

    // Constructions

    /**
     * Constructs a morphism that represents the identity on this graph (idle).
     */
    default GraphMorphism identity() {
        return new Isomorphism(this.getName(), this, getName()) {
            @Override
            public Name doRename(Name base) {
                return base;
            }

            @Override
            public boolean hasBeenRenamed(Name name) {
                return true;
            }

            @Override
            public Name undoRename(Name renamed) {
                return renamed;
            }
        };
    }


    /**
     * Returns a graph that is basically identitcal to this one but every
     * node and edge name has been prefixed with the name of this graph.
     * It can be used to assure that the set of names in this graph is
     * disjoint with another graph.
     *
     */
    default Graph prefix() {
        return new Isomorphism(Name.identifier("addPrefix").appliedTo(getName()), this, getName()) {
            @Override
            public Name doRename(Name base) {
                return base.prefixWith(getName());
            }

            @Override
            public boolean hasBeenRenamed(Name name) {
                return name.hasPrefix(getName());
            }

            @Override
            public Name undoRename(Name renamed) {
                return renamed.unprefix(getName());
            }
        }.getResult();
    }

    /**
     * Removes the prefix of elements prefixed with the name of this graph, if any.
     */
    default Graph unprefix() {
        return new Isomorphism(
                Name.identifier("stripPrefix").appliedTo(getName()),
                this,
                getName()) {
            @Override
            public Name doRename(Name base) {
                if (hasBeenRenamed(base)) {
                    return base.unprefix(Graph.this.getName());
                }
                return base;
            }

            @Override
            public boolean hasBeenRenamed(Name name) {
                return name.hasPrefix(Graph.this.getName());
            }

            @Override
            public Name undoRename(Name renamed) {
                if (hasBeenRenamed(renamed)) {
                    return renamed;
                }
                return renamed.prefixWith(Graph.this.getName());
            }
        }.getResult();
    }

    /**
     * Computes the sum (dsjoint union) of this graph with another graph.
     */
    default Graph sum(Graph other) {
        return new GraphUnion(Arrays.asList(this, other), this.getName().sum(other.getName()));
    }

    default Graph multiSum(Graph... others) {
        return multiSum(Arrays.asList(others));
    }

    default Graph multiSum(List<Graph> others) {
        List<Graph> all = new ArrayList<>();
        all.add(this);
        all.addAll(others);
        return new GraphUnion(all, Name.merge(all.stream().map(Element::getName).collect(Collectors.toList())));
    }

    /**
     * Computes the cartesian product of the two graphs.
     */
    default Graph cartesianProduct(Graph other) {
        Set<Name> nodes = StreamExt.cartesianProduct(this::nodes, other::nodes, Name::pair).collect(Collectors.toSet());
        Set<Triple> edges = StreamExt.cartesianProduct(this::edges, other::edges, (a, b) -> a.combineMap(b, Name::pair))
                .filter(t -> nodes.contains(t.getSource()) && nodes.contains(t.getTarget()))
                .collect(Collectors.toSet());

        Set<Triple> elements = new HashSet<>();
        nodes.forEach(n -> elements.add(Triple.node(n)));
        elements.addAll(edges);
        return new GraphImpl(this.getName().times(other.getName()), elements);
    }


}
