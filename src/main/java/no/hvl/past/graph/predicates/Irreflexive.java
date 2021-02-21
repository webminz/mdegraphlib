package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;

/**
 * Validates whether the instance fibre of a loop edge induces an irreflexive relation.
 */
public class Irreflexive implements GraphPredicate {
    private static Irreflexive instance;


    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances( Universe.LOOP_THE_LOOP).noneMatch(
                triple -> triple.getSource().equals(triple.getTarget())
        );
    }

    @Override
    public String nameAsString() {
        return "[irreflexive]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.LOOP;
    }

    private Irreflexive() {
    }

    public static Irreflexive getInstance() {
        if (instance == null) {
            instance = new Irreflexive();
        }
        return instance;
    }
}
