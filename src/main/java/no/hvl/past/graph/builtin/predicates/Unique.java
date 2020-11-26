package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Unique implements GraphPredicate {

    @Override
    public boolean check(TypedGraph instance) {
        return instance.allInstances(Universe.ARROW_SRC_NAME).allMatch(node -> {
            List<Name> collect = instance.allInstances(Universe.ARROW_THE_ARROW).filter(triple -> triple.getSource().equals(node)).map(Triple::getTarget).collect(Collectors.toList());
            Set<Name> withoutDups = new HashSet<>(collect);
            return collect.size() == withoutDups.size();
        });
    }

    @Override
    public String nameAsString() {
        return "[unique]";
    }

    @Override
    public Graph arity() {
        return Universe.ARROW;
    }
}
