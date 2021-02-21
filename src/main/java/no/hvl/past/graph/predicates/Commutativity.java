package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates if two parallel arrows commute, i.e.
 * the result of following the left path is the same as following the right path for
 * all source nodes in the instance-fibre.
 */
public class Commutativity implements GraphPredicate {

    private static Commutativity instance;

    @Override
    public String nameAsString() {
        return "[=]";
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
            if (!left.stream()
                    .filter(t -> t.getSource().equals(s))
                    .map(Triple::getTarget)
                    .collect(Collectors.toSet()).equals(
                        right.stream()
                                .filter(t -> t.getSource().equals(s))
                                .map(Triple::getTarget)
                                .collect(Collectors.toSet()))) {
                return false;
            }
        }
        return true;
    }

    private Commutativity() {
    }

    public static Commutativity getInstance() {
        if (instance == null) {
            instance = new Commutativity();
        }
        return instance;
    }
}
