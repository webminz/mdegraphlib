package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.StreamExt;

/**
 * Predicate that verifies that there are no edges that reference target elements multiple times.
 */
public class Unique implements GraphPredicate {

    private static Unique instance;

    private Unique() {
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.ARROW_SRC_NAME)
                .allMatch(node ->
                        StreamExt.isUnique(instance.allOutgoingInstances(Universe.ARROW_THE_ARROW, node)
                                .map(Triple::getTarget)));

    }

    @Override
    public String nameAsString() {
        return "[unique]";
    }

    @Override
    public Graph arity() {
        return Universe.ARROW;
    }

    public static Unique getInstance() {
        if (instance == null) {
            instance = new Unique();
        }
        return instance;
    }
}
