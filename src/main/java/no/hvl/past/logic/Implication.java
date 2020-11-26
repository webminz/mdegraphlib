package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.names.Variable;

import java.util.HashSet;
import java.util.Set;

public class Implication extends Formula {

    private final Formula guard;

    private final Formula conclusion;

    public Implication(Formula guard, Formula conclusion) {
        this.guard = guard;
        this.conclusion = conclusion;
    }

    @Override
    public Set<Variable> getVariables() {
        Set<Variable> result = new HashSet<>();
        result.addAll(guard.getVariables());
        result.addAll(conclusion.getVariables());
        return null;
    }

    @Override
    public boolean verify(Context context, GraphMorphism instance) {
        if (guard.verify(context, instance)) {
            return conclusion.verify(context, instance);
        }
        return true;
    }
}
