package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Model;
import no.hvl.past.logic.Theory;
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
public interface GraphLabelTheory extends Theory<GraphLabelTheory>, Label {

    /**
     * Return true if this abstract operation is a Predicate.
     */
    boolean isPredicate();

    /**
     * Return true if this abstract operation is an Operation.
     */
    boolean isOperation();

    String nameAsString();

    @Override
    default Optional<Theory<?>> semantics() {
        return Optional.of(this);
    }

    @Override
    default Name getName() {
        return Name.identifier(nameAsString());
    }

    @Override
    default boolean isEnumeratable() {
        return false;
    }

    @Override
    default boolean isFinite() {
        return false;
    }

    @Override
    default Stream<Model<GraphLabelTheory>> models() {
        return Stream.empty();
    }


    default Stream<Triple> queryEdge(TypedGraph instance, Triple triple) {
        return instance.select(triple).map(t -> t.mapName(Name::firstPart));
    }

    default Stream<Triple> queryEdge(TypedGraph instance, String src, String label, String trg) {
        return instance.select(new Triple(Name.variable(src), Name.variable(label), Name.variable(trg))).map(t -> t.mapName(Name::firstPart));
    }

    default Stream<Triple> queryNode(TypedGraph instance, String node) {
        return instance.select(Triple.node(Name.variable(node))).map(t -> t.mapName(Name::firstPart));
    }



}
