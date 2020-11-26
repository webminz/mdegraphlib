package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

public class Ordered implements GraphPredicate {
    @Override
    public boolean check(TypedGraph instance) {
        return instance.allInstances(Universe.ARROW_THE_ARROW).map(Triple::getLabel).allMatch(Name::isIndexed);
    }

    @Override
    public String nameAsString() {
        return "[ordered]";
    }

    @Override
    public Graph arity() {
        return Universe.ARROW;
    }
}
