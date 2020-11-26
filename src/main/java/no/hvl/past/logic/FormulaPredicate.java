package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.names.Name;
import no.hvl.past.names.Variable;
import no.hvl.past.graph.GraphPredicate;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FormulaPredicate extends Formula {

    private final GraphPredicate predicate;

    private final Map<Name, Name> assignment;

    public FormulaPredicate(GraphPredicate predicate, Map<Name, Name> assignment) {
        this.predicate = predicate;
        this.assignment = assignment;
    }


    @Override
    public Set<Variable> getVariables() {
        return assignment.keySet().stream().filter(Name::isVariable).map(n -> (Variable)n).collect(Collectors.toSet());
    }

    @Override
    public boolean verify(Context context, GraphMorphism instance) {
        return false; // TODO
    }
}
