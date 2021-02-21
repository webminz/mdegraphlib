package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.util.Pair;

/**
 * Predicate that verifies whether the respective edges are ordered.
 */
public class Ordered implements GraphPredicate {

    private static Ordered instance;

    private Ordered() {
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.ARROW_SRC_NAME)
                .allMatch(node -> {
                    return instance.allOutgoingInstances(Universe.ARROW_THE_ARROW, node).reduce(
                                    new Pair<Triple, Boolean>(null, true),
                                    (acc, edge) -> {
                                        if (acc.getFirst() == null) {
                                            return new Pair<>(edge, acc.getSecond());
                                        } else {
                                            return new Pair<>(edge, acc.getSecond() && acc.getFirst().getLabel().inATotalOrderWith(edge.getLabel()));
                                        }
                                    },
                                    (acc1, acc2) -> new Pair<>(acc2.getFirst(), acc1.getSecond() && acc2.getSecond())
                            ).getSecond();
                });
    }

    @Override
    public String nameAsString() {
        return "[ordered]";
    }

    @Override
    public Graph arity() {
        return Universe.ARROW;
    }

    public static Ordered getInstance() {
        if (instance == null) {
            instance = new Ordered();
        }
        return instance;
    }
}
