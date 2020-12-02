package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;

/**
 * Holds if the the instance fibre of the respective node contains exactly one element.
 */
public class Singleton implements GraphPredicate {

    private static final String SINGLETON_PRED_NAME = "[singleton]";

    private static Singleton instance;

    @Override
    public String nameAsString() {
        return SINGLETON_PRED_NAME;
    }


    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }


    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE).count() == 1;
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

}
