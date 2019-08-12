package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Morphism implements AbstractMorphism {

    private final Name name;

    private final AbstractGraph domain;

    private final AbstractGraph codomain;

    private final Map<Name, Name> mapping;

    @Override
    public boolean definedAt(Name name) {
        return this.mapping.containsKey(name);
    }

    @Override
    public Iterator<Tuple> iterator() {
        return this.getTuples().iterator();
    }


    /**
     * A helper class to construct well formed Morphisms.
     * Builders are conservative and robust, i.e. invalid inputs are ignored
     * and the first given definition stays and cannot be overwritten later.
     */
    public static class Builder {

        private Name name;
        private Graph from;
        private Graph to;
        private Map<Name, Name> nodeMap;

        public Builder(String name, Graph from, Graph to) {
            this.name = Name.identifier(name);
            this.from = from;
            this.to = to;
            this.nodeMap = new HashMap<>();
        }

        public Builder(Name name, Graph from, Graph to) {
            this.name = name;
            this.from = from;
            this.to = to;
            this.nodeMap = new HashMap<>();
        }

        public Builder map(Name from, Name to) {
            if (nodeMap.containsKey(from)) {
                return this;
            }
            nodeMap.put(from, to);
            return this;
        }

        public Builder map(String from, String to) {
            return this.map(Name.identifier(from), Name.identifier(to));
        }

        public Builder map(Triple from, Triple to) {
            // Already there
            if (nodeMap.containsKey(from.getLabel())) {
                return this;
            }
            // Is not contained in Codomain
            if (!this.to.contains(to)) {
                return this;
            }
            // Is not contained in Domain
            if (!this.from.contains(from)) {
                return this;
            }
            if (nodeMap.containsKey(from.getSource()) && !nodeMap.get(from.getSource()).equals(to.getSource())) {
                return this; // invalid
            }
            if (nodeMap.containsKey(from.getTarget()) && !nodeMap.get(from.getTarget()).equals(to.getTarget())) {
                return this; // invalid
            }
            // All good! Can be added!

            this.nodeMap.put(from.getLabel(), to.getLabel());
            if (!nodeMap.containsKey(from.getSource())) {
                this.nodeMap.put(from.getSource(), to.getSource());
            }
            if (!nodeMap.containsKey(from.getTarget())) {
                this.nodeMap.put(from.getTarget(), to.getTarget());
            }
            return this;
        }

        public Morphism build() {
            return new Morphism(name, from, to, nodeMap);
        }
    }

    private Morphism(Name name, AbstractGraph domain, AbstractGraph codomain, Map<Name, Name> mapping) {
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.mapping = mapping;
    }

    Morphism(Name name, AbstractGraph domain, AbstractGraph codomain, Set<Tuple> tuples) {
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.mapping = new HashMap<>();
        tuples.forEach(tuple -> mapping.put(tuple.getDomain(), tuple.getCodomain()));
    }

    @Override
    public Optional<Name> apply(Name node) {
        if (this.mapping.containsKey(node)) {
            return Optional.of(mapping.get(node));
        }
        return Optional.empty();
    }

    public Name getName() {
        return name;
    }

    @Override
    public AbstractGraph getDomain() {
        return domain;
    }

    @Override
    public AbstractGraph getCodomain() {
        return codomain;
    }

    @Override
    public boolean definedAt(Triple t) {
        return this.mapping.containsKey(t.getSource()) && this.mapping.containsKey(t.getLabel()) && this.mapping.containsKey(t.getTarget());
    }

    /**
     * Gives the manifestation of this mapping, i.e. a set of tuples.
     */
    private Set<Tuple> getTuples() {
        return this.mapping.entrySet().stream().map(entry -> new Tuple(entry.getKey(),entry.getValue())).collect(Collectors.toSet());
    }

    @Override
    public Optional<Triple> apply(Triple from) {
        if (!domain.contains(from)) {
            return Optional.empty(); // Invalid input, the given triple is not contained in codomain
        }
        Triple target = new Triple(mapping.get(from.getSource()), mapping.get(from.getLabel()), mapping.get(from.getTarget()));
        if (!codomain.contains(target)) {
            return Optional.empty(); // The mapping definition of the morphism is wrong.
        }
        return Optional.of(target);
    }

    @Override
    public Set<Triple> select(Triple to) {
        return  StreamSupport.stream(this.getDomain().spliterator(), false)
                .filter(t -> new Triple(mapping.get(t.getSource()), mapping.get(t.getLabel()), mapping.get(t.getTarget())).equals(to))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Triple> select(Set<Triple> subgraph) {
        return subgraph.stream().flatMap(triple -> this.select(triple).stream()).collect(Collectors.toSet());
    }

    public boolean isTotal() {
        return StreamSupport.stream(this.getDomain().spliterator(), false).allMatch(this::definedAt);
    }

    public boolean isInjective() {
        return StreamSupport.stream(this.getCodomain().spliterator(), false).map(this::select).allMatch(set -> set.size() <= 1);
    }

    public boolean isSurjective() {
        return StreamSupport.stream(this.getCodomain().spliterator(), false).map(this::select).allMatch(set -> set.size() >= 1);
    }


    @Override
    public Spliterator<Tuple> spliterator() {
        return this.mapping.entrySet().stream().map(e -> new Tuple(e.getKey(), e.getValue())).collect(Collectors.toSet()).spliterator();
    }


    public Set<Morphism> findMatches(Morphism other) {
        return Collections.emptySet(); // TODO implement
    }


    /**
     * Checks if the homomorphism property holds.
     */
    private static boolean isWellDefined(Map<Name, Name> mapping, Graph domain, Graph codomain) {
        return domain.getEdges().stream()
                .filter(e -> mapping.containsKey(e.getSource()) && mapping.containsKey(e.getLabel()) && mapping.containsKey(e.getTarget()))
                .map(e -> new Triple(mapping.get(e.getSource()), mapping.get(e.getLabel()), mapping.get(e.getTarget())))
                .allMatch(codomain::contains);
    }



    public static Morphism fromTypedGraph(Name resultName, Graph typeGraph, Graph typedGraph) {
        Set<Tuple> collect = typedGraph.getElements().stream()
                .flatMap(triple -> {
                    Set<Tuple> mapping = new HashSet<>();
                    if (triple.getSource().isTyped() && typeGraph.contains(triple.getSource().getType().get())) {
                        mapping.add(new Tuple(triple.getSource().stripType(), triple.getSource().getType().get()));
                    }
                    if (triple.getLabel().isTyped() && typeGraph.contains(triple.getLabel().getType().get())) {
                        mapping.add(new Tuple(triple.getLabel().stripType(), triple.getLabel().getType().get()));
                    }
                    if (triple.getTarget().isTyped() && typeGraph.contains(triple.getTarget().getType().get())) {
                        mapping.add(new Tuple(triple.getTarget().stripType(), triple.getTarget().getType().get()));
                    }
                    return mapping.stream();
                })
                .collect(Collectors.toSet());
        Graph inst = new Graph(typedGraph.getName(),
                typedGraph.getElements()
                        .stream()
                        .map(t -> new Triple(t.getSource().stripType(), t.getLabel().stripType(), t.getTarget().stripType()))
                        .collect(Collectors.toSet()));
        return new Morphism(resultName, inst, typeGraph, collect);
    }


    public static Morphism create(Name morphismName, Graph domain, Graph codomain, Set<Tuple> mapping) throws GraphError {
        Morphism result = new Morphism(morphismName, domain, codomain, mapping);
        if (!isWellDefined(result.mapping, domain, codomain)) {
            throw new GraphError(GraphError.ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION, Collections.emptySet());
        }
        return result;
    }

}
