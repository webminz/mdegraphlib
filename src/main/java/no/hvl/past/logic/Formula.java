package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.names.Variable;

import java.util.Set;

public abstract class Formula {

    public abstract Set<Variable> getVariables();

    public abstract boolean verify(Context context, GraphMorphism instance);
}
