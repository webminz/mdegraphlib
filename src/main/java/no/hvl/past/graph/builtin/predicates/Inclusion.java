package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates if one parallel arrows is included in the other,
 * i.e. a weakened form of commutativity.
 */
public class Inclusion implements GraphPredicate {

    private static Inclusion instance;

    @Override
    public String nameAsString() {
        return "[incl]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.CELL;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        Set<Triple> left = instance.allInstances(Universe.CELL_LHS).collect(Collectors.toSet());
        Set<Triple> right = instance.allInstances(Universe.CELL_RHS).collect(Collectors.toSet());
        Set<Name> sources = left.stream().map(Triple::getSource).collect(Collectors.toSet());
        if (!sources.equals(right.stream().map(Triple::getSource).collect(Collectors.toSet()))) {
            return false;
        }
        for (Name s : sources) {
            Set<Name> rightTargets = right.stream()
                    .filter(t -> t.getSource().equals(s))
                    .map(Triple::getTarget)
                    .collect(Collectors.toSet());
            if (!left.stream()
                    .filter(t -> t.getSource().equals(s))
                    .map(Triple::getTarget)
                    .allMatch(rightTargets::contains)) {
                return false;
            }
        }
        return true;
    }

    private Inclusion() {
    }

    public static Inclusion getInstance() {
        if (instance == null) {
            instance = new Inclusion();
        }
        return instance;
    }
}
