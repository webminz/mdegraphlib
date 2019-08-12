package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.Collections;
import java.util.Set;


/**
 * A directed multigraph consisting of nodes and edges.
 * Nodes and Edges have an identity (Edges have a label).
 * Graphs are the lingua franca in Computer Science and Software Engineering,
 * they can represent all relevant artifacts, i.e.
 * Software (Meta-)models, source code (via the AST), XML documents, Database Schema etc.
 */
public interface AbstractGraph extends Element, Iterable<Triple> {

    boolean contains(Triple triple);

    boolean contains(Name name);

    Set<Triple> outgoing(Name from);

    Set<Triple> incoming(Name to);

    default AbstractMorphism identity() {
        return new IdentityMorphism(this);
    }

    @Override
    default Graph toGraph(Name containerName) {
        return new Graph(containerName, Collections.singleton(Triple.fromNode(this.getName())));
    }

    @Override
    default void sendTo(OutputPort<?> port) {
        port.beginGraph(getName());
        for (Triple t : this) {
            port.handleTriple(t);
        }
        port.endGraph();
    }
}
