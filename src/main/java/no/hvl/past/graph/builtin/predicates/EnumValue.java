package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Set;

public class EnumValue implements GraphPredicate {

    private final Set<Name> allowed;

    private EnumValue(Set<Name> allowed) {
        this.allowed = allowed;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ARROW_THE_ARROW)
                .map(Triple::getTarget)
                .allMatch(allowed::contains);
    }

    @Override
    public String nameAsString() {
        return "[enum" + allowed.toString() + "]";
    }

    @Override
    public Graph arity() {
        return Universe.ARROW;
    }
}
