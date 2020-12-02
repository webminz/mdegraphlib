package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.logic.Model;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A graph diagram is an annotation in a graph,
 * i.e. it sits on some nodes and edges.
 * Its defined by a shape, called inputArity.
 * It can have two purposes:
 * - a diagrammatic predicate encodes a property that all instances of a graph should fulfill.
 * - a diagrammatic operation encodes elements in a graph that are derived by the elements in the given graph.
 */
public interface GraphLabelTheory extends Formula<Graph>, Label {

    /**
     * Return true if this abstract operation is a Predicate.
     */
    boolean isPredicate();

    /**
     * Return true if this abstract operation is an Operation.
     */
    boolean isOperation();

    String nameAsString();

    default Formula<Graph> semantics() {
        return this;
    }

    @Override
    default Name getName() {
        return Name.identifier(nameAsString());
    }


}
