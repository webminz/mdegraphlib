package no.hvl.past.graph.predicates;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The set of edges typed over the inputArity forms a (partial) function,
 * aka [0..1] on the association target side.
 */
public class Function implements GraphPredicate {

    private static final String FUNCTION_PRED_NAME = "[function]";

    private static Function instance;

    @Override
    public String nameAsString() {
        return FUNCTION_PRED_NAME;
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    private Function() {
    }

    @Override
    public boolean check(GraphMorphism instance) {
        List<Name> sources = instance.allInstances(Universe.ARROW_THE_ARROW).map(Triple::getSource).collect(Collectors.toList());
        Set<Name> sourcesset = new HashSet<>(sources);
        return sources.size() == sourcesset.size();
    }

    public static Function getInstance() {
        if (instance == null) {
            instance = new Function();
        }
        return instance;
    }
}
