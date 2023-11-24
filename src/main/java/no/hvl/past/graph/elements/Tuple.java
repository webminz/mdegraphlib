package no.hvl.past.graph.elements;

import no.hvl.past.names.Name;

import java.util.*;
import java.util.stream.Stream;

/**
 * Constituents of a mapping, e.g. a graph homorphism.
 */
public class Tuple {

    private final Name domain;

    private final Name codomain;

    public Tuple(Name domain, Name codomain) {
        this.domain = domain;
        this.codomain = codomain;
    }

    public Name getDomain() {
        return domain;
    }

    public Name getCodomain() {
        return codomain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return Objects.equals(domain, tuple.domain) &&
                Objects.equals(codomain, tuple.codomain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, codomain);
    }

    @Override
    public String toString() {
        return domain.toString() + "=>" + codomain.toString();
    }


    public static Map<Name, Name> toMap(Set<Tuple> tuples) {
        Map<Name, Name> result = new HashMap<>();
        tuples.forEach(t -> result.put(t.domain, t.getCodomain()));
        return result;
    }


    public static Set<Tuple> fromMap(Map<Name, Name> map) {
        Set<Tuple> result = new HashSet<>();
        for (Map.Entry<Name, Name> entry : map.entrySet()) {
            result.add(new Tuple(entry.getKey(), entry.getValue()));
        }
        return result;
    }


    public static Set<Tuple> transitiveClosure(Set<Tuple> inheritanceEdges) {
        boolean converged = false;
        Set<Tuple> result = new HashSet<>(inheritanceEdges);
        while (!converged) {
            converged = true;
            Set<Tuple> addedThisRound = new HashSet<>();
            for (Tuple lhs : result) {
                for (Tuple rhs : inheritanceEdges) {
                    if (lhs.getCodomain().equals(rhs.getDomain())) {
                        Tuple toAdd = new Tuple(lhs.getDomain(), rhs.getCodomain());
                        if (!result.contains(toAdd)) {
                            converged = false;
                            addedThisRound.add(toAdd);
                        }
                    }
                }
            }
            result.addAll(addedThisRound);
        }
        return result;
    }


}
