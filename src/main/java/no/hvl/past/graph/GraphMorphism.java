package no.hvl.past.graph;

import com.google.common.collect.Streams;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A mapping between two graphs.
 * There are many practical occurrences of this theoretical construct, e.g.
 * - Typed Instances
 * - Variable Bindings
 * - Selections(Queries)
 * - Modifications
 * etc.
 */
public interface GraphMorphism extends Element {


    /**
     * The domain of the morphism, e.g. an instance.
     */
    Graph domain();

    /**
     * The range of the morphism, e.g. a type.
     */
    Graph codomain();

    /**
     * Performs a structural mapping of this name.
     */
    Optional<Name> map(Name name);

    default FrameworkElement elementType() {
        return FrameworkElement.GRAPH_MORPHISM;
    }


    /**
     * Provides a structural representation of this mapping
     * as a sequence of pairs mapping names in the domain
     * to names in the codomain.
     */
    default Stream<Tuple> mappings() {
        return domain().elements()
                .flatMap(Triple::parts)
                .map(n -> new Pair<>(n, map(n)))
                .filter(p -> p.getSecond().isPresent())
                .map(p -> new Tuple(p.getFirst(), p.getSecond().get()));
    }

    /**
     * Returns true if this morphism is defined at the given element.
     */
    default boolean definedAt(Name node) {
        return map(node).isPresent();
    }

    /**
     * Returns true if this morphism is defined at the given triple.
     */
    default boolean definedAt(Triple t) {
        return t.map(this::map).isPresent();
    }

    /**
     * Applies the morphism to the given node.
     * If this morphism is not defined at the given element Optional.empty is returned.
     */
    default Optional<Triple> applyOnNode(Name node) {
        return Triple.node(node).map(this::map);
    }

    /**
     * Applies the morphism to the given edge. If this morphism is not defined at the given element,
     * Optional.empty is returned.
     */
    default Optional<Triple> apply(Triple from) {
        return from.map(this::map);
    }

    /**
     * Performs a reverse mapping (lookup): Given an element in the codomain, it retrieves
     * all elements (fibre) in the domain that are mapped to this particular element.
     *
     */
    default Stream<Triple> select(Triple to) {
        return domain().elements().filter(t -> this.apply(t).map(to::equals).orElse(false));
    }

    /**
     * Selects all triples in the domain graph where the label is mapped to the given name.
     */
    default Stream<Triple> selectByLabel(Name toLabelName) {
        return domain().elements().filter(t -> this.apply(t).map(Triple::getLabel).map(toLabelName::equals).orElse(false));
    }

    /**
     * Performs a reverse mapping (lookup) for a set of elements (subgraphs) of the codomain, i.e
     * calculates the pre-image of this graph.
     * It is the set-valued variant of the lookup function above.
     *
     */
    default Stream<Triple> select(Set<Triple> subgraph) {
        return subgraph.stream().flatMap(this::select);
    }

    /**
     * Returns the image of this morphism, which is a subobject of the codomain.
     */
    default Stream<Triple> image() {
        return domain().elements().map(this::apply).filter(Optional::isPresent).map(Optional::get);
    }

    /**
     * Checks whether this morphism is well defined, i.e. the homomorphism property is fulfilled.
     */
    default boolean verify() {
        return domain().elements()
                .map(this::apply)
                .filter(Optional::isPresent)
                .allMatch(m -> codomain().contains(m.get()))
                &&
                domain().elements()
                        .map(Triple::getLabel)
                        .filter(this::definedAt)
                        .allMatch(n ->
                                domain().isNode(n) && codomain().isNode(this.map(n).get()) ||
                                        domain().isEdge(n) && codomain().isEdge(this.map(n).get()));
    }

    /**
     * Checks whether this morphism is defined for all elements.
     */
    default boolean isTotal() {
        return domain().elements().allMatch(this::definedAt);
    }

    /**
     * Checks whether this morphism is injective, i.e. basically an embedding modulo renaming.
     */
    default boolean isInjective() {
        return codomain().elements().map(this::select).allMatch(set -> set.count() <= 1);
    }

    /**
     * Checks whether this morphis is surjective, i.e. every element in the codomain has a preimage.
     */
    default boolean isSurjective() {
        return codomain().elements().map(this::select).allMatch(set -> set.count() >= 1);
    }

    default boolean isExtremalMonic() {
        return isInjective();
    }


    /**
     * Composes this morphism with another one.
     * Note that the codomain of this morphism must be equal to the domain of the other.
     */
    default GraphMorphism compose(GraphMorphism with) {
        return new GraphMorphismComposition(this, with);
    }


    /**
     * Calculates the pullback of this morphism (left) and the given right morphism.
     * A pullback can be seen as category theoretic generalization of a query.
     * The result is a pair comprising two morphisms.
     *
     */
    default Pair<GraphMorphism, GraphMorphism> pullback(
            GraphMorphism right,
            Name apexName) {
        if (!this.codomain().equals(right.codomain())) {
            throw new Error("invalid input");
        }

        Set<Triple> elements = codomain().elements()
                .flatMap(target -> this.select(target)
                        .flatMap(pre1 -> right.select(target).map(pre2 ->
                             pre1.combineMap(pre2, Name::pair)
                        )))
                .collect(Collectors.toSet());

        GraphImpl result = new GraphImpl(apexName, elements);
        GraphMorphism leftResultMorphism = new EpicMorphism(this.getName().query(right.getName()), result, this.domain()) {
            @Override
            public Triple assign(Triple element) {
                return element.mapName(Name::firstPart);
            }
        };
        GraphMorphism rightResultMorphism = new EpicMorphism(right.getName().query(this.getName()), result, right.domain()) {
            @Override
            public Triple assign(Triple element) {
                return element.mapName(Name::secondPart);
            }
        };
        return new Pair<>(leftResultMorphism, rightResultMorphism);
    }


    // TODO pushout

    // TODO final pullback complement

    // Diagram operations


    /**
     * Interprets this morphism as a typing morphism such that we can only consider
     * the domain graph were all element names receive a type name annotation.
     * Note that Elements in the domain graph that do not admit typing are removed!
     */
    default Graph flatten() {
        Subobject deleteUntyped = new Subobject(Name.anonymousIdentifier(), domain(), domain().getName()) {
            @Override
            public boolean deletes(Name name) {
                return !GraphMorphism.this.definedAt(name);
            }
        };
        Isomorphism addTyping = new Isomorphism(Name.anonymousIdentifier(), deleteUntyped.domain(), domain().getName().typeBy(codomain().getName())) {
            @Override
            public Name doRename(Name base) {
                Optional<Name> typ = GraphMorphism.this.map(base);
                if (typ.isPresent()) {
                    return base.typeBy(typ.get());
                } else {
                    return base;
                }
            }

            @Override
            public boolean hasBeenRenamed(Name name) {
                return name.isTyped();
            }

            @Override
            public Name undoRename(Name renamed) {
                return renamed.stripType();
            }
        };
        return addTyping.codomain();
    }


    @Override
    default void accept(Visitor visitor) {
        visitor.beginMorphism();
        visitor.handleName(this.getName());
        domain().accept(visitor);
        codomain().accept(visitor);
        this.mappings().forEach(visitor::handleTuple);
        visitor.endMorphism();
    }




    /**
     * Interprets a given graph as a graph morphism, where the elements
     * of the given graph have been tagged with their types.
     * The latter are expected to be elements of the given codomain (type) graph.
     */
    static GraphMorphism unflatten(Graph graph, Graph codomain, Name resultName) {
        Graph domain = new Graph() {
            @Override
            public boolean isInfinite() {
                return graph.isInfinite();
            }

            @Override
            public Stream<Triple> elements() {
                return graph.elements().map(t -> t.mapName(Name::stripType));
            }

            @Override
            public Name getName() {
                return graph.getName().stripType();
            }
        };
        return new GraphMorphism() {
            @Override
            public Graph domain() {
                return domain;
            }

            @Override
            public Graph codomain() {
                return codomain;
            }

            @Override
            public Optional<Name> map(Name name) {
                return graph
                        .elements()
                        .map(Triple::getLabel)
                        .filter(n -> n.stripType().equals(name))
                        .findFirst()
                        .flatMap(Name::getType);
            }

            @Override
            public Name getName() {
                return resultName;
            }
        };
    }


    default Stream<Triple> homPropViolations() {
        return Stream.empty(); // TODO
    }
}
