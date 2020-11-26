package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.names.Variable;

import java.util.Set;

public class Negation extends Formula {

    private final Formula nested;

    public Negation(Formula nested) {
        this.nested = nested;
    }

    @Override
    public Set<Variable> getVariables() {
        return nested.getVariables();
    }

    @Override
    public boolean verify(Context context, GraphMorphism instance) {
        return !nested.verify(context, instance);
    }
}
