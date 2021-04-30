package no.hvl.past.graph.predicates;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.GraphPredicate;
import no.hvl.past.graph.Universe;

public class AbstractType implements GraphPredicate {

    private static AbstractType instance;

    private AbstractType() {
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE).noneMatch( x-> true);
    }

    @Override
    public String nameAsString() {
        return "[abstract]";
    }

    @Override
    public Graph arity() {
        return Universe.ONE_NODE;
    }

    public static AbstractType getInstance() {
        if (instance == null) {
            instance = new AbstractType();
        }
        return instance;
    }
}
