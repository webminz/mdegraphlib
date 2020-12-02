package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A diagram in the categorical sense, i.e. a selection of graphs (as nodes)
 * and morphisms between them (as edges).
 *
 * We assume that all diagrams we are dealing with are small and finite.
 * Moreover, there is a total order over nodes and edges.
 */
public interface Diagram extends Element {

    /**
     *
     * Returns the optional (!) label of this diagram.
     */
    Optional<Label> label();

    /**
     * The binding of the shape into elements in a concrete graph.
     */
    GraphMorphism binding();

    @Override
    default FrameworkElement elementType() {
        return FrameworkElement.DIAGRAM;
    }

    /**
     * The shape of the diagram (= a graph).
     */
    default Graph arity() {
        return binding().domain();
    }



    /**
     * Given an element in the arity, it provides the element it is bound to.
     */
    default Optional<Triple> image(Triple of) {
        return binding().apply(of);
    }

    /**
     * Provides all the elements in the image of the diagram.
     */
    default Stream<Triple> scope() {
        return this.binding().image();
    }

    default boolean isInScope(Triple element) {
        return this.scope().anyMatch(element::equals);
    }

    /**
     * Calculates the colimit of this diagram, i.e. a one object (= graph) summary
     * that is the smallest comprehensive object above the elements in this diagram (least upper bound).
     */
    Graph colimit();

    /**
     * Calculates the limit of this diagram, i.e. a one object (=graph) summary
     * that is the biggest common object below the elements in this diagram (greatest lower bound).
     */
    Graph limit();

    /**
     * Calculates a one object representation of this diagram by "internalizing" the
     * additional structure.
     */
    Graph flatten();


}
