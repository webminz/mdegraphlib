package no.hvl.past.graph;


import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.stream.Stream;

/**
 * A special class to represent morphisms representing structural modifications.
 * The morphism is based on a given graph, which is modified in one of the following name
 *
 * - Elements are renamed => Isomoprhism
 * - Elements are added => Superobject
 * - Elements are removed => Subobject
 * - Elements are merged => Epimorphism
 */
public abstract class AbstractModification implements GraphMorphism {

    private final Name name;
    private final Graph base;
    private final boolean codomainDerived;
    private final Name resultName;
    private Graph result;

    AbstractModification(
            Name name,
            Graph base,
            Graph result,
            boolean codomainDerived) {
        this.name = name;
        this.codomainDerived = codomainDerived;
        this.base = base;
        this.result = result;
        this.resultName = result.getName();
    }


    AbstractModification(
            Name name,
            Graph base,
            Name resultName,
            boolean codomainDerived) {
        this.name = name;
        this.base = base;
        this.resultName = resultName;
        this.codomainDerived = codomainDerived;
    }

    public abstract Stream<Triple> elements();

    public abstract boolean contains(Triple element);

    @Override
    public Name getName() {
        return name;
    }

    @Override
    public Graph domain() {
        return codomainDerived ? getBase() : getResult();
    }

    @Override
    public Graph codomain() {
        return codomainDerived ? getResult() : getBase();
    }

    protected Graph getBase() {
        return base;
    }

    protected Graph getResult() {
        if (result == null) {
            result = new GraphModProxy(resultName, this);
        }
        return result;
    }

    public abstract boolean mentions(Name name);
}
