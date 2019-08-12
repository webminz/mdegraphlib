package no.hvl.past.graph;

import no.hvl.past.graph.names.Merge;
import no.hvl.past.graph.names.Name;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A mapping between two graphs.
 * There are many practical occurences of this theoretical construct, e.g.
 * - Typed Instances
 * - Variable Bindings
 * - Selections(Queries)
 */
public interface AbstractMorphism extends Element, Iterable<Tuple> {

    /**
     * The domain of the morphism, e.g. an instance.
     */
    AbstractGraph getDomain();

    /**
     * The range of the morphism, e.g. a type.
     */
    AbstractGraph getCodomain();

    /**
     * Returns true if this morphism is defined at the given element.
     */
    boolean definedAt(Name node);

    /**
     * Returns true if this morphism is defined at the given triple.
     */
    boolean definedAt(Triple t);

    /**
     * Applies the morphism to the given element. If this morphism is not defined at the given element,
     * Optional.empty is returned.
     */
    Optional<Name> apply(Name node);

    /**
     * Applies the morphism to the given element. If this morphism is not defined at the given element,
     * Optional.empty is returned.
     */
    Optional<Triple> apply(Triple from);

    /**
     * Performs a reverse mapping (lookup): Given an element in the codomain, it retrieves
     * all elements in the domain that are mapped to this particular element.
     *
     */
    Set<Triple> select(Triple to);

    /**
     * Performs a reverse mapping (lookup) for a set of elements (subgraphs).
     * It is the set-valued variant of the lookup function above.
     */
    Set<Triple> select(Set<Triple> subgraph);

    default Multispan pullback(Name spanName, AbstractMorphism other, Name resultGraphName, NamingStrategy strategy) {
        if (!this.getCodomain().equals(other.getCodomain())) {
            throw new Error("invalid input");
        }

        Set<Triple> elements = StreamSupport.stream(this.getCodomain().spliterator(), false)
                .flatMap(target -> this.select(target).stream().map(triple -> triple.prefix(this.getDomain().getName()))
                .flatMap(pre1 -> other.select(target).stream().map(triple -> triple.prefix(other.getDomain().getName()))
                        .map(pre1::combine)))
                .collect(Collectors.toSet());


        Set<Tuple> thisPrime = new HashSet<>();
        Set<Tuple> otherPrime = new HashSet<>();


        Graph result = new Graph(resultGraphName,
                elements.stream()
                        .map(triple -> giveNames(triple, this.getDomain().getName(), thisPrime, other.getDomain().getName(), otherPrime, strategy))
                        .collect(Collectors.toSet()));

        Morphism g1Morphism = new Morphism(this.getName().query(other.getName()), result, this.getDomain(), thisPrime);
        Morphism g2Morphism = new Morphism(other.getName().query(this.getName()), result, other.getDomain(), otherPrime);

        return new Multispan(spanName ,result, Arrays.asList(this.getDomain(), other.getDomain()), Arrays.asList(g1Morphism, g2Morphism));
    }


    static Triple giveNames(Triple triple, Name g1Name, Set<Tuple> g1Mapping, Name g2Name, Set<Tuple> g2Mapping, NamingStrategy strategy) {
        Merge source = (Merge) triple.getSource();
        Name sourceName = strategy.name(source.getMembers());
        source.findNameWithPrefix(g1Name).ifPresent(name -> g1Mapping.add(new Tuple(sourceName, name)));
        source.findNameWithPrefix(g2Name).ifPresent(name -> g2Mapping.add(new Tuple(sourceName, name)));

        Merge label = (Merge) triple.getLabel();
        Name labelName = strategy.name(label.getMembers());
        label.findNameWithPrefix(g1Name).ifPresent(name -> g1Mapping.add(new Tuple(labelName, name)));
        label.findNameWithPrefix(g2Name).ifPresent(name -> g2Mapping.add(new Tuple(labelName, name)));

        Merge target = (Merge) triple.getTarget();
        Name targetName = strategy.name(target.getMembers());
        target.findNameWithPrefix(g1Name).ifPresent(name -> g1Mapping.add(new Tuple(targetName, name)));
        target.findNameWithPrefix(g2Name).ifPresent(name -> g2Mapping.add(new Tuple(targetName, name)));

        return new Triple(sourceName, labelName, targetName);

    }


    /**
     * Interprets this morphism as a typing morphism such that we can only consider
     * the domain graph were all element names receive a type name annotation.
     */
    default AbstractGraph toTypedGraph() {
        return new AbstractGraph() {

            private Triple applyMorph(Triple triple) {
                return new Triple(
                        apply(triple.getSource()).map(t -> triple.getSource().typeBy(t)).orElse(triple.getSource()),
                        apply(triple.getLabel()).map(t -> triple.getLabel().typeBy(t)).orElse(triple.getLabel()),
                        apply(triple.getTarget()).map(t -> triple.getTarget().typeBy(t)).orElse(triple.getTarget()));
            }

            @Override
            public Name getName() {
                return getDomain().getName().typeBy(getCodomain().getName());
            }

            @Override
            public Iterator<Triple> iterator() {
                Iterator<Triple> iterator = getDomain().iterator();
                return new Iterator<Triple>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Triple next() {
                        return applyMorph(iterator.next());
                    }
                };
            }

            @Override
            public boolean contains(Triple triple) {
                return contains(triple.getSource()) && contains(triple.getLabel()) && contains(triple.getTarget());
            }

            @Override
            public boolean contains(Name name) {
                if (name.isTyped() && definedAt(name.stripType())) {
                    return getDomain().contains(name.stripType()) && name.equals(name.stripType().typeBy(apply(name.stripType()).get()));
                }
                return getDomain().contains(name) && !definedAt(name);
            }

            @Override
            public Set<Triple> outgoing(Name from) {
                if (from.isTyped() && definedAt(from.stripType()) && from.equals(from.stripType().typeBy(apply(from.stripType()).get()))) {
                    return getDomain().outgoing(from.stripType()).stream().map(this::applyMorph).collect(Collectors.toSet());
                }
                return getDomain().outgoing(from).stream().map(this::applyMorph).collect(Collectors.toSet());
            }

            @Override
            public Set<Triple> incoming(Name to) {
                if (to.isTyped() && definedAt(to.stripType()) && to.equals(to.stripType().typeBy(apply(to.stripType()).get()))) {
                    return getDomain().incoming(to.stripType()).stream().map(this::applyMorph).collect(Collectors.toSet());
                }
                return getDomain().incoming(to).stream().map(this::applyMorph).collect(Collectors.toSet());
            }
        };
    }


    @Override
    default Graph toGraph(Name containerName) {
        return new Graph(containerName, new HashSet<>(Arrays.asList(
                Triple.fromNode(this.getDomain().getName()),
                Triple.fromNode(this.getCodomain().getName()),
                new Triple(this.getDomain().getName(), this.getName(), this.getCodomain().getName()))));
    }

    @Override
    default void sendTo(OutputPort<?> port) {
        port.beginMorphism(getName(), getDomain().getName(), getCodomain().getName());
        getDomain().sendTo(port);
        getCodomain().sendTo(port);
        for (Tuple tuple : this) {
            port.handleTuple(tuple);
        }
        port.endMorphism();
    }


}
