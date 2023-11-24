package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;

import java.util.*;
import java.util.stream.Collectors;


public class GraphMorphismImpl implements GraphMorphism, Iterable<Tuple> {

    private final Name name;
    private final Graph domain;
    private final Graph codomain;
    private final Map<Name, Name> mapping;

    public GraphMorphismImpl(Name name, Graph domain, Graph codomain, Map<Name, Name> mapping) {
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.mapping = mapping;
    }

    GraphMorphismImpl(Name name, Graph domain, Graph codomain, Set<Tuple> tuples) {
        this.name = name;
        this.domain = domain;
        this.codomain = codomain;
        this.mapping = new HashMap<>();
        tuples.forEach(tuple -> mapping.put(tuple.getDomain(), tuple.getCodomain()));
    }

    @Override
    public boolean definedAt(Name name) {
        return this.mapping.containsKey(name);
    }

    @Override
    public Iterator<Tuple> iterator() {
        return this.getTuples().iterator();
    }

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Graph domain() {
        return domain;
    }

    @Override
    public Graph codomain() {
        return codomain;
    }

    @Override
    public boolean definedAt(Triple t) {
        return this.mapping.containsKey(t.getSource()) && this.mapping.containsKey(t.getLabel()) && this.mapping.containsKey(t.getTarget());
    }

    @Override
    public Optional<Name> map(Name name) {
        if (this.mapping.containsKey(name)) {
            return Optional.of(this.mapping.get(name));
        }
        return Optional.empty();
    }

    @Override
    public Spliterator<Tuple> spliterator() {
        return this.mapping.entrySet().stream().map(e -> new Tuple(e.getKey(), e.getValue())).collect(Collectors.toSet()).spliterator();
    }

    /**
     * Gives the manifestation of this mapping, i.e. a set of tuples.
     */
    public Set<Tuple> getTuples() {
        return this.mapping.entrySet().stream().map(entry -> new Tuple(entry.getKey(),entry.getValue())).collect(Collectors.toSet());
    }



    /**
     * Checks if the homomorphism property holds.
     */
    private static Set<Triple> findIllFormed(Map<Name, Name> mapping, Graph domain, Graph codomain) {
        return domain.edges()
                .filter(e -> mapping.containsKey(e.getSource()) && mapping.containsKey(e.getLabel()) && mapping.containsKey(e.getTarget()))
                .map(e -> new Triple(mapping.get(e.getSource()), mapping.get(e.getLabel()), mapping.get(e.getTarget())))
                .filter(edge -> !codomain.contains(edge))
                .collect(Collectors.toSet());
    }

    public static GraphMorphismImpl create(Name morphismName, Graph domain, Graph codomain, Set<Tuple> mapping) throws GraphError {
        GraphMorphismImpl result = new GraphMorphismImpl(morphismName, domain, codomain, mapping);
        Set<Triple> illFormed = findIllFormed(result.mapping, domain, codomain);
      //  if (!illFormed.isEmpty()) {
       //     throw new GraphError(GraphError.ERROR_TYPE.HOMOMORPHISM_PROPERTY_VIOLATION, illFormed);
       // }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphMorphismImpl tuples = (GraphMorphismImpl) o;
        return Objects.equals(domain, tuples.domain) &&
                Objects.equals(codomain, tuples.codomain) &&
                Objects.equals(mapping, tuples.mapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, codomain, mapping);
    }


    public static GraphMorphism materialize(GraphMorphism morphism) {
        Map<Name, Name> binding = new HashMap<>();
        morphism.domain().elements()
                .filter(morphism::definedAt)
                .map(Triple::getLabel)
                .forEach(s -> {
                    binding.put(s, morphism.map(s).get());
                });
        return new GraphMorphismImpl(morphism.getName(), morphism.domain(), morphism.codomain(), binding);
    }
}
