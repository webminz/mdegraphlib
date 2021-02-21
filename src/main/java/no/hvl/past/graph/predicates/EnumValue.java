package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A predicate that veriefied that all instances typed over that given node are taken from a given set of names,
 */
public class EnumValue implements GraphPredicate {

    private final Set<Name> allowed;

    private EnumValue(Set<Name> allowed) {
        this.allowed = allowed;
    }

    public Set<Name> literals() {
        return allowed;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .map(Triple::getLabel)
                .allMatch(allowed::contains);
    }

    @Override
    public String nameAsString() {
        return "[enum" + allowed.toString() + "]";
    }

    @Override
    public Graph arity() {
        return Universe.ONE_NODE;
    }


    public static EnumValue getInstance(Name... literals) {
        return new EnumValue(new HashSet<>(Arrays.asList(literals)));
    }
}
