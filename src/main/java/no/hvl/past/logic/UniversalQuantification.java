package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;

import java.util.Set;

public class UniversalQuantification extends Quantification {

    public UniversalQuantification(Set<TypedVariable> variables, Formula nested) {
        super(variables, nested);
    }

    @Override
    public boolean verify(Context context, GraphMorphism instance) {
        return false; // TODO
    }
}
