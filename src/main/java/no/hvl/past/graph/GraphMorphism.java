package no.hvl.past.graph;

import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.logic.Model;
import no.hvl.past.names.Name;
import no.hvl.past.names.NameSet;
import no.hvl.past.util.Pair;
import no.hvl.past.util.PartitionAlgorithm;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A mapping between two graphs.
 * There are many practical occurrences of this theoretical construct, e.g.
 * - Typed Instances
 * - Variable Bindings
 * - Selections(Queries)
 * - Modifications
 * etc.
 */
public interface GraphMorphism extends Element, Model<Graph> {

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


    @Override
    default void accept(Visitor visitor) {
        visitor.beginMorphism();
        visitor.handleElementName(this.getName());
        this.mappings().forEach(visitor::handleMapping);
        domain().accept(visitor);
        codomain().accept(visitor);
        visitor.endMorphism();
    }

    // ACCESSORS


    /**
     * Provides a structural representation of this mapping
     * as a sequence of pairs mapping names in the domain
     * to names in the codomain.
     */
    default Stream<Tuple> mappings() {
        return Stream.concat(
                domain().nodes()
                        .filter(this::definedAt)
                        .map(n -> new Tuple(n, map(n).get())),
                domain().edges()
                        .map(Triple::getLabel)
                        .filter(this::definedAt)
                        .map(n -> new Tuple(n, map(n).get())));
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
        return definedAt(t.getLabel());
    }


    /**
     * Applies the morphism to the given edge. If this morphism is not defined at the given element,
     * Optional.empty is returned.
     */
    default Optional<Triple> apply(Triple from) {
        return this.map(from.getLabel()).flatMap(codomain()::get);
    }

    /**
     * Performs a reverse mapping (lookup): Given an element in the codomain, it retrieves
     * all elements (fibre) in the domain that are mapped to this particular element.
     *
     */
    default Stream<Triple> preimage(Triple to) {
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
    default Stream<Triple> preimage(Set<Triple> subgraph) {
        return subgraph.stream().flatMap(this::preimage);
    }

    /**
     * Returns the image of this morphism, which is a subobject of the codomain.
     */
    default Stream<Triple> image() {
        return domain().elements().map(this::apply).filter(Optional::isPresent).map(Optional::get);
    }

    // OCL like query methods

    default Stream<Triple> allInstances(Triple type) {
        return this.preimage(type);//this.preimage(type).map(t -> t.mapName(Name::firstPart))l
    }

    default Stream<Triple> allInstances(Name type) {
        return this.preimage(Triple.node(type)); //.map(t -> t.mapName(Name::firstPart));
    }

    default Stream<Name> allNodeInstances(Name type) {
        return allInstances(type).filter(Triple::isNode).map(Triple::getLabel);
    }

    default Stream<Triple> allOutgoingInstances(Triple type, Name src) {
        return allInstances(type).filter(edge -> edge.getSource().equals(src));
    }

    default Stream<Triple> allIncomingInstances(Triple type, Name trg) {
        return allInstances(type).filter(edge -> edge.getTarget().equals(trg));
    }

    default Stream<Pair<Triple, Triple>> allSrcCoincidentInstances(Triple leftType, Triple rightType) {
        return allInstances(leftType)
                .flatMap(left -> allOutgoingInstances(rightType, left.getSource()).map(right -> new Pair<>(left, right)));
    }

    default Stream<Pair<Triple, Triple>> allTrgCoincidentInstances(Triple leftType, Triple rightType) {
        return allInstances(leftType)
                .flatMap(left -> allIncomingInstances(rightType, left.getTarget()).map(right -> new Pair<>(left, right)));
    }

    // PROPERTIES


    /**
     * Checks whether this morphism is well defined, i.e. the homomorphism property is fulfilled.
     */
    default boolean verify() {
        return  // check whether everything is mapped to an element that actually exists in the codomain
                    mappedToUndefined().count() == 0
                &&  // and no homomorphism property violations
                    homPropViolations().count() == 0;
    }


    /**
     * Returns all elements of the domain graph that are mapped to an element that actually does not exist
     * in the codomain.
     */
    default Stream<Triple> mappedToUndefined() {
        return domain().elements()
                .filter(this::definedAt)
                .filter(triple -> !codomain().get(this.map(triple.getLabel()).get()).isPresent());
    }

    /**
     * Returns all the elements of the domain graph that violate the homomorphism-property (edge-node incidence)
     * under this morphism.
     */
    default Stream<Triple> homPropViolations() {
        return this.domain()
                .edges()
                .filter(this::definedAt)
                .filter(e -> codomain().get(this.map(e.getLabel()).get()).isPresent())
                .filter(edge -> {
                    Triple mappedEdge = codomain().get(this.map(edge.getLabel()).get()).get();
                    return !this.map(edge.getSource()).get().equals(mappedEdge.getSource()) || // incidence source
                            !this.map(edge.getTarget()).get().equals(mappedEdge.getTarget()) || // incidence target
                            domain().isNode(edge.getLabel()) && codomain().isEdge(mappedEdge.getLabel()) || // no nodes to edges
                            domain().isEdge(edge.getLabel()) && codomain().isNode(mappedEdge.getLabel()); // no edges to nodes
                });
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
    default boolean isMonic() {
        return codomain().elements().map(this::preimage).allMatch(set -> set.count() <= 1);
    }

    /**
     * Checks whether this morphis is surjective, i.e. every element in the codomain has a preimage.
     */
    default boolean isEpic() {
        return codomain().elements().map(this::preimage).allMatch(set -> set.count() >= 1);
    }

    /**
     * Returns true if this morphism extremally injective, i.e. those that cannot be further decomposed into
     * a proper (= not injective) surjective morphism.
     * Usually in graphs all injective morphisms are also extremal, however there might be special types of graphs, e.g.
     * those with inheritance where this is not true.
     */
    default boolean isExtremalMonic() {
        return isMonic();
    }


    // CONSTRUCTIONS


    /**
     * Composes this morphism with another one.
     * Note that the codomain of this morphism must be equal to the domain of the other.
     */
    default GraphMorphism compose(GraphMorphism with) {
        return new GraphMorphismComposition(this, with);
    }


    /**
     * Calculates the pullback of the cospan given by _this_ morphism (left) and the given _right_ morphism.
     * The result is a pair representing a span two morphisms, the left argument is the morphism whose codomain coincides
     * with the domain of _this_ morphism.
     * A pullback can be seen as a generalized intersection when thinking of objects as sets and morphisms
     * as inclusions.
     */
    default Pair<GraphMorphism, GraphMorphism> pullback(
            GraphMorphism right) {
        GraphMorphism left = this;
        if (!this.codomain().equals(right.codomain())) {
            // input does not have the same codomain
            throw new GraphError().addError(new GraphError.DomainOrCodomainMismatch(left.codomain().getName(), right.codomain().getName(), true));
        }
        if (left.isMonic()) {
            // Calculate preimage
            Name resultObjectName = right.getName().preimage(left.domain().getName());
            Map<Name, Name> invertedMonicMapping = new HashMap<>();
            left.domain().elements().map(Triple::getLabel).filter(left::definedAt).forEach(l -> invertedMonicMapping.put(left.map(l).get(),l));
            Set<Name> deleted = right.domain().elements()
                    .map(Triple::getLabel)
                    .filter(n -> {
                        Optional<Name> mapped = right.map(n);
                        if (mapped.isPresent()) {
                            Name target = mapped.get();
                            return !left.selectByLabel(target).anyMatch(t -> true);
                        }
                        return true;
                    }).collect(Collectors.toSet());
            Subobject inclusion = new Subobject(resultObjectName.subTypeOf(right.domain().getName()), right.domain(), resultObjectName) {
                @Override
                public boolean deletes(Name name) {
                    return deleted.contains(name);
                }
            };
            GraphMorphism restriction = new GraphMorphism() {
                @Override
                public Graph domain() {
                    return inclusion.domain();
                }

                @Override
                public Graph codomain() {
                    return left.domain();
                }

                @Override
                public Optional<Name> map(Name name) {
                    return right.map(name).map(invertedMonicMapping::get);
                }

                @Override
                public Name getName() {
                    return right.getName().downTypeAlong(left.getName());
                }
            };
            return new Pair<>(restriction, inclusion);
        } else if (right.isMonic()) {
            // Calculate preimage the other way
            Name resultObjectName = left.getName().preimage(right.domain().getName());
            Set<Name> deleted = left.domain().elements()
                    .map(Triple::getLabel)
                    .filter(n -> {
                        Optional<Name> mapped = left.map(n);
                        if (mapped.isPresent()) {
                            Name target = mapped.get();
                            return !right.selectByLabel(target).anyMatch(t -> true);
                        }
                        return true;
                    }).collect(Collectors.toSet());
            Subobject inclusion = new Subobject(resultObjectName.subTypeOf(left.domain().getName()), left.domain(), resultObjectName) {
                @Override
                public boolean deletes(Name name) {
                    return deleted.contains(name);
                }
            };
            GraphMorphism restriction = new GraphMorphism() {
                @Override
                public Graph domain() {
                    return inclusion.domain();
                }

                @Override
                public Graph codomain() {
                    return right.domain();
                }

                @Override
                public Optional<Name> map(Name name) {
                    return left.map(name);
                }

                @Override
                public Name getName() {
                    return left.getName().downTypeAlong(right.getName());
                }
            };
            return new Pair<>(inclusion, restriction);
        } else {
            // default
            Set<Triple> elements = codomain().elements()
                    .flatMap(target -> left.preimage(target)
                            .flatMap(pre1 -> right.preimage(target).map(pre2 ->
                                    pre1.combineMap(pre2, Name::pair)
                            )))
                    .collect(Collectors.toSet());

            GraphImpl result = new GraphImpl(Name.identifier("P.B.").appliedTo(left.domain().getName().pair(right.domain().getName())), elements);
            GraphMorphism leftResultMorphism = new GraphMorphism() {
                @Override
                public Name getName() {
                    return right.getName().addSuffix(left.getName());
                }

                @Override
                public Graph domain() {
                    return result;
                }

                @Override
                public Graph codomain() {
                    return left.domain();
                }

                @Override
                public Optional<Name> map(Name name) {
                    if (result.mentions(name)) {
                        return Optional.of(name.firstPart());
                    }
                    return Optional.empty();
                }
            };
            GraphMorphism rightResultMorphism = new GraphMorphism() {
                @Override
                public Graph domain() {
                    return result;
                }

                @Override
                public Graph codomain() {
                    return right.domain();
                }

                @Override
                public Optional<Name> map(Name name) {
                    if (result.mentions(name)) {
                        return Optional.of(name.secondPart());
                    }
                    return Optional.empty();
                }

                @Override
                public Name getName() {
                    return left.getName().addSuffix(right.getName());
                }
            };
            return new Pair<>(leftResultMorphism, rightResultMorphism);
        }
    }


    /**
     * Calculates the pushout the span given by this (left) morphism and the given right morphis.
     * The result is pair representing the resulting co-span, where left argument is coincident with
     * _this_.
     * A pushout can be seen as gluing of two graphs at given interface.
     */
    default Pair<GraphMorphism, GraphMorphism> pushout(GraphMorphism right) {
        GraphMorphism left = this;
        if (!left.domain().equals(right.domain())) {
            throw new GraphError().addError(new GraphError.DomainOrCodomainMismatch(left.domain().getName(), right.domain().getName(), false));
        }

        if (!left.isTotal() || !right.isTotal()) {
            throw new Error("Not implemented yet !!!"); // TODO pushouts of partial maps...
        }
        if (right.isMonic()) {
            return glue(right, left).revert();
        } else if (left.isMonic()) {
            return glue(left, right);
        } else {
            GraphUnion union = new GraphUnion(Arrays.asList(left.domain(), left.codomain(), right.codomain()), left.domain().getName().mergeWith(left.codomain().getName(), right.codomain().getName()));
            PartitionAlgorithm<Name> partition = new PartitionAlgorithm<>(union.elements().map(Triple::getLabel).collect(Collectors.toSet()));
            left.domain()
                    .elements()
                    .map(Triple::getLabel)
                    .forEach(n -> {
                        Name l = left.map(n).get().prefixWith(left.codomain().getName());
                        Name r = right.map(n).get().prefixWith(right.codomain().getName());
                        partition.relate(n.prefixWith(left.domain().getName()), l);
                        partition.relate(n.prefixWith(right.domain().getName()), r);
                    });
            EpicMorphism congruence = EpicMorphism.fromPartition(
                    Name.identifier("CONGRUENCE").appliedTo(union.getName()),
                    Name.identifier("P.O").appliedTo(left.codomain().getName().pair(right.codomain().getName())),
                    union,
                    partition,
                    NameSet.DEFAULT_NAME_MERGING_STRATEGY
            );
            GraphMorphism leftResult = union.inclusionOf(left.codomain().getName()).get().compose(congruence);
            GraphMorphism rightResult = union.inclusionOf(left.codomain().getName()).get().compose(congruence);

            // regular
            return new Pair<>(leftResult, rightResult);
        }

    }

    // TODO pushout

    // TODO final pullback complement


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


    static Pair<GraphMorphism, GraphMorphism> glue(GraphMorphism monic, GraphMorphism other) {
        Name resultGraphName = other.codomain().getName().extendedBy(monic.codomain().getName());
        Set<Triple> inserts = new HashSet<>();
        monic.codomain().nodes().filter(n -> monic.allNodeInstances(n).noneMatch(x -> true)).map(n -> n.prefixWith(monic.codomain().getName())).map(Triple::node).forEach(inserts::add);
        monic.codomain()
                .edges()
                .filter(t -> monic.selectByLabel(t.getLabel()).noneMatch(x -> true))
                .map(t -> {
                    Optional<Triple> oldSrc = monic.selectByLabel(t.getSource()).findFirst();
                    Optional<Triple> oldTrg = monic.selectByLabel(t.getTarget()).findFirst();
                    Name newSrc;
                    Name newTrg;
                    if (oldSrc.isPresent()) {
                        newSrc = other.map(oldSrc.get().getLabel()).get();
                    } else {
                        newSrc = t.getSource().prefixWith(monic.codomain().getName());
                    }
                    if (oldTrg.isPresent()) {
                        newTrg = other.map(oldTrg.get().getLabel()).get();
                    } else {
                        newTrg = t.getTarget().prefixWith(monic.codomain().getName());
                    }
                    return Triple.edge(newSrc, t.getLabel().prefixWith(monic.codomain().getName()), newTrg);
                })
                .forEach(inserts::add);

        Superobject result = new Superobject(other.codomain().getName().subTypeOf(resultGraphName), other.codomain(), resultGraphName) {

            @Override
            protected Stream<Triple> inserts() {
                return inserts.stream();
            }
        };
        GraphMorphism extended = new GraphMorphism() {
            @Override
            public Graph domain() {
                return monic.codomain();
            }

            @Override
            public Graph codomain() {
                return result.codomain();
            }

            @Override
            public Optional<Name> map(Name name) {
                if (monic.domain().mentions(name)) {
                    return other.map(name);
                }
                return Optional.of(name);
            }

            @Override
            public Name getName() {
                return other.getName().extendedBy(monic.codomain().getName());
            }
        };
        return new Pair<>(extended, result);
    }


}
