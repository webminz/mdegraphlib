package no.hvl.past.graph.elements;

import no.hvl.past.names.Name;

import java.util.Objects;

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
}
