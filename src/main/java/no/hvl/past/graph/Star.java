package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.MulitaryCombinator;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import no.hvl.past.util.PartitionAlgorithm;
import no.hvl.past.util.StreamExt;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wide (i.e. multi-ary) span of (partial) graph morphisms.
 * This provides a categorical representation of a multi-relation
 * between elements in different graphs.
 */
public interface Star extends Element {

    /**
     * The arity of this span, i.e. the number of components.
     */
    int size();

    /**
     * The apex of the span, which contains relation witnesses.
     */
    Sketch apex();

    /**
     * Returns the i'th component of this relation.
     */
    Optional<Sketch> component(int i);

    /**
     * Returns the i'th projection in this span.
     */
    Optional<GraphMorphism> projection(int i);


    /**
     * Returns all components of this relation in form of a stream.
     */
    default Stream<Sketch> components() {
        return StreamExt.iterate(1, size(), 1).mapToObj(this::component).map(Optional::get);
    }

    /**
     * Returns all projections in this span in form of a stream.
     */
    default Stream<GraphMorphism> projections() {
        return StreamExt.iterate(1, size(), 1).mapToObj(this::projection).map(Optional::get);
    }

    /**
     * Given a tuple of elements from different graphs, this method
     * returns all elements in the apex graph that witness a relation between
     * the elements in the given tuple.
     */
    default Stream<Triple> witnesses(Triple... elements) {
        Set<Triple> toBeMatched = new HashSet<>(Arrays.asList(elements));
        return apex().carrier().elements().filter(witnes -> {
            return toBeMatched.stream().allMatch(target -> {
                return projections().anyMatch(morphism -> morphism.apply(witnes).map(target::equals).orElse(false));
            });
        });
    }

    default Stream<Triple> imagesIn(int component, Triple of) {
        OptionalInt source = StreamExt.iterate(1, size(), 1)
                .filter(i -> component(i).map(sketch -> sketch.carrier().contains(of)).orElse(false))
                .findFirst();
        if (source.isPresent()) {
            GraphMorphism m = projection(source.getAsInt()).get();
            return m.allInstances(of).flatMap(edge -> {
                if (projection(component).map(m2 -> m2.definedAt(edge)).orElse(false)) {
                    return Stream.of(projection(component).get().apply(edge).get());
                } else {
                    return Stream.empty();
                }
            });
        }
        return Stream.empty();
    }


    @Override
    default boolean verify() {
        return projections().allMatch(GraphMorphism::verify);
    }

    /**
     * Returns true if the elements in the given tuple are related somehow.
     */
    default boolean areRelated(Triple... elements) {
        return witnesses(elements).anyMatch(x -> true);
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.beginSpan();
        visitor.handleElementName(getName());
        this.apex().accept(visitor);
        this.projections().forEach(m -> m.accept(visitor));
        visitor.endSpan();
    }
}
