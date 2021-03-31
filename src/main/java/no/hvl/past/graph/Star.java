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
     * The names of apex witnesses that represent identities, i.e. the component elements
     * that are related via witnesses mentioned in this collection are treated as 'the same'
     * and are thus treated differently when creating a comprehensive system.
     */
    Stream<Name> identities();

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

    /**
     * Provides a global view of the complete star in terms of a single artifact (= a sketch).
     * The latter can be used to defined further inter-model constraints.
     */
    default Pair<Sketch, List<GraphMorphism>> comprehensiveSystem() {
        // throwing it all together
        List<Graph> base = new ArrayList<>();
        base.add(apex().carrier());
        components().map(Sketch::carrier).forEach(base::add);
        GraphUnion union = new GraphUnion(base, Name.identifier("SUM").prefixWith(getName()));


        //  linguistic extension for all non-identity commonalities
        Set<Triple> linguisticExtension = new HashSet<>();
        apex().carrier().nodes()
                .filter(n -> identities().noneMatch(n::equals))
                .forEach(n -> {
                    for (int i = 1; i <= size(); i++) {
                        GraphMorphism projection = projection(i).get();
                        if (projection.definedAt(n)) {
                            linguisticExtension.add(Triple.edge(
                                    n.prefixWith(apex().carrier().getName()),
                                    projection.getName().appliedTo(n),
                                    projection.map(n).get().prefixWith(component(i).get().carrier().getName())
                            ));
                        }
                    }
                });
        Superobject superobject = new Superobject(Name.anonymousIdentifier(), union, Name.identifier("LINGUSTIC_EXTENSION").appliedTo(union.getName())) {
            @Override
            protected Stream<Triple> inserts() {
                return linguisticExtension.stream();
            }
        };

        // epimorphism by partitioning along identities (basically colimit)
        PartitionAlgorithm<Name> partition = new PartitionAlgorithm<>(superobject.getResult().elements().map(Triple::getLabel).collect(Collectors.toSet()));
        apex().carrier().elements()
                .filter(t -> identities().anyMatch(name -> name.equals(t.getLabel())))
                .forEach(t -> {
                    for (int i = 1; i <= size(); i++) {
                        GraphMorphism projection = projection(i).get();
                        if (projection.definedAt(t.getLabel())) {
                            partition.relate(t.getLabel().prefixWith(apex().carrier().getName()), projection.map(t.getLabel()).get().prefixWith(component(i).get().carrier().getName()));
                        }
                    }
                });
        EpicMorphism congruence = EpicMorphism.fromPartition(
                Name.anonymousIdentifier(),
                Name.identifier("ID_CONGRUENCE").appliedTo(superobject.getResult().getName()),
                superobject.getResult(),
                partition
        );

        Map<Name, Name> nameSimplification = new HashMap<>();
        Map<Name, Name> nameSimplificationReverse = new HashMap<>();
        congruence.getResult().elements().map(Triple::getLabel).forEach(elementName -> {
            if (elementName instanceof MulitaryCombinator) {
                MulitaryCombinator combinator = (MulitaryCombinator) elementName;
                Name name = combinator.findNameWithPrefix(apex().carrier().getName()).get();
                nameSimplification.put(elementName, name);
                nameSimplificationReverse.put(name, elementName);
            } else if (elementName.getPrefix().isPresent()) {
                Name withoutPrefix = elementName.unprefixTop();
                if (nameSimplificationReverse.containsKey(withoutPrefix)) {
                    Name other = nameSimplificationReverse.get(withoutPrefix);
                    nameSimplification.put(elementName, elementName);
                    nameSimplification.put(other, other);
                    nameSimplificationReverse.put(elementName, elementName);
                    nameSimplificationReverse.put(other, other);
                } else {
                    nameSimplification.put(elementName, withoutPrefix);
                    nameSimplificationReverse.put(withoutPrefix, elementName);
                }
            } else {
                nameSimplification.put(elementName, elementName);
                nameSimplificationReverse.put(elementName, elementName);
            }
        });

        Isomorphism rename = new Isomorphism(Name.anonymousIdentifier(), congruence.getResult(), getName().global().absolute()) {
            @Override
            public Name doRename(Name base) {
                return nameSimplification.get(base);
            }

            @Override
            public boolean hasBeenRenamed(Name name) {
                return nameSimplificationReverse.containsKey(name);
            }

            @Override
            public Name undoRename(Name renamed) {
                return nameSimplificationReverse.get(renamed);
            }
        };


        Graph result = GraphImpl.materialize(rename.getResult());
        List<GraphMorphism> embeddings = new ArrayList<>();
        embeddings.add(GraphMorphismImpl.materialize(union.inclusionOf(apex().carrier()).get().compose(superobject).compose(congruence).compose(rename)));
        components().forEach(component -> {
            GraphMorphism morphism = union.inclusionOf(component.carrier()).get();
            embeddings.add(GraphMorphismImpl.materialize(morphism.compose(superobject).compose(congruence).compose(rename)));
        });

        return new Pair<>(new Sketch() {
            @Override
            public Graph carrier() {
                return result;
            }

            @Override
            public Stream<Diagram> diagrams() {
                return components().flatMap(sketch -> {
                    GraphMorphism morphism = embeddings.stream().filter(morph -> morph.domain().equals(sketch.carrier())).findFirst().get();
                    return sketch.diagrams().map(diag -> diag.substitue(morphism));
                });
            }

            @Override
            public Name getName() {
                return Star.this.getName().global();
            }
        }, embeddings);
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
