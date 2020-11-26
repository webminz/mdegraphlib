package no.hvl.past.graph.builtin.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The set of edges typed over the inputArity forms an injective relation,
 * aka [0..1] on the association source side.
 */
public class Injective implements GraphPredicate {
    private static final String INJECTIVE_PRED_NAME = "[injective]";

    private static Injective instance;

    @Override
    public String nameAsString() {
        return INJECTIVE_PRED_NAME;
    }


    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    private Injective() {
    }

    @Override
    public boolean check(TypedGraph instance) {
        List<Name> targets = instance.allInstances(Universe.ARROW_THE_ARROW).map(Triple::getTarget).collect(Collectors.toList());
        Set<Name> targetsset = new HashSet<>(targets);
        return targets.size() == targetsset.size();
    }

    public static Injective getInstance() {
        if (instance == null) {
            instance = new Injective();
        }
        return instance;
    }
}
