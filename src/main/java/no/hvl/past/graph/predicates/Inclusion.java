package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates whether the instances of the left hand sides are included in the right hand side.
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
        return instance.allInstances(Universe.CELL_LHS.getSource())
                .allMatch(source -> {
                    Set<Triple> lefts = instance.allInstances(Universe.CELL_LHS).filter(edge -> edge.getSource().equals(source.getLabel())).collect(Collectors.toSet());
                    if (lefts.isEmpty()) {
                        return true;
                    } else {
                        for (Triple left : lefts) {
                            if (instance.allInstances(Universe.CELL_RHS)
                                    .filter(edge -> edge.getSource().equals(source.getLabel()))
                                    .noneMatch(right -> right.getTarget().equals(left.getTarget()))) {
                                return false;
                            }
                        }
                        return true;
                    }
                });
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
