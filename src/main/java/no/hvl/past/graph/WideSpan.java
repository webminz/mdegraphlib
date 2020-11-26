package no.hvl.past.graph;

import no.hvl.past.graph.elements.EquivalenceClass;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.StreamExtensions;
import sun.jvm.hotspot.runtime.Frame;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A wide (i.e. multi-ary) span of (partial) graph morphisms.
 * This provides a categorical representation of a multi-relation
 * between elements in different graphs.
 */
public interface WideSpan extends Element {

    /**
     * The arity of this span, i.e. the number of components.
     */
    int size();

    /**
     * Given a tuple of elements from different graphs, this method
     * returns all elements in the apex graph that witness a relation between
     * the elements in the given tuple.
     */
    Stream<Triple> witnesses(Triple... elements);

    /**
     * Returns true if the elements in the given tuple are related somehow.
     */
    default boolean areRelated(Triple... elements) {
        return witnesses(elements).count() > 0;
    }

    /**
     * The apex of the span, which contains relation witnesses.
     */
    Sketch apex();

    /**
     * Returns the i'th component of this relation.
     */
    Optional<Sketch> component(int i);

    /**
     * Returns all components of this relation in form of a stream.
     */
    default Stream<Sketch> components() {
        return StreamExtensions.iterate(1, size(), 1).mapToObj(this::component).map(Optional::get);
    }

    /**
     * Returns the i'th projection in this span.
     */
    Optional<SketchMorphism> projection(int i);

    /**
     * Returns all projections in this span in form of a stream.
     */
    default Stream<SketchMorphism> projections() {
        return StreamExtensions.iterate(1, size(), 1).mapToObj(this::projection).map(Optional::get);
    }

    /**
     * Calculates equivalence classes based on this span.
     */
    default Stream<EquivalenceClass> classes() {
        // TODO implement in the known way
        return Stream.empty();
    }


    @Override
    default FrameworkElement elementType() {
        return FrameworkElement.STAR;
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.beginSpan();
        visitor.handleName(getName());
        this.apex().accept(visitor);
        this.components().forEach(m -> m.accept(visitor));
        visitor.endSpan();
    }
}
