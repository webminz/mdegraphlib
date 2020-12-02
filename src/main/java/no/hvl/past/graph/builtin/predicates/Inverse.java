package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;

/**
 * Validates if two opposite arrows represent inverses of each other.
 */
public class Inverse implements GraphPredicate {

    private static Inverse instance;

    @Override
    public String nameAsString() {
        return "[inverse]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.CYCLE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.CYCLE_FWD).allMatch(t1 ->
                instance.allInstances(Universe.CYCLE_BWD)
                        .filter(t -> t.getSource().equals(t1.getTarget()))
                        .allMatch(t2 -> t2.getTarget().equals(t1.getSource()))) &&
            instance.allInstances(Universe.CYCLE_BWD).allMatch(t1 ->
                    instance.allInstances(Universe.CYCLE_FWD)
                            .filter(t -> t.getSource().equals(t1.getTarget()))
                            .allMatch(t2 -> t2.getTarget().equals(t1.getSource())));
    }


    public static Inverse getInstance() {
        if (instance == null) {
            instance = new Inverse();
        }
        return instance;
    }
}
