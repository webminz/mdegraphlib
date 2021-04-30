package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Acyclicity implements GraphPredicate {

    private static Acyclicity instance;

    @Override
    public String nameAsString() {
        return "[acyclic]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    private Acyclicity() {
    }

    @Override
    public boolean check(GraphMorphism instance) {
        Set<Triple> select = instance.allInstances(Universe.ARROW_THE_ARROW).collect(Collectors.toSet());
        Set<Triple> closure = closure(select);
        return select.stream()
                .noneMatch(t1 -> closure.stream().filter(t2 ->
                        !t2.isNode()).anyMatch(t2 -> t2.getTarget().equals(t1.getSource()) &&
                        t2.getSource().equals(t1.getTarget())));
    }

    public static Acyclicity getInstance() {
        if (instance == null) {
            instance = new Acyclicity();
        }
        return instance;
    }



    private static Set<Triple> closure(Set<Triple> base) {
        Set<Triple> result = new HashSet<>(base);
        boolean finished = false;
        while (!finished) {
            finished = true;
            Set<Triple> newTriples = new HashSet<>();
            for (Triple t1 : result) {
                for (Triple t2 : base) {
                    Optional<Triple> compose = t1.compose(t2);
                    if (compose.isPresent()) {
                        if (result.stream()
                                .filter(Triple::isEddge)
                                .noneMatch(t -> t.getSource().equals(compose.get().getSource()) &&
                                        t.getTarget().equals(compose.get().getTarget()))) {
                            finished = false;
                            newTriples.add(compose.get());
                        }
                    }
                }
            }
            result.addAll(newTriples);
        }
        return result;
    }


}
