package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.names.Variable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Conjunction extends Formula {

    private final List<Formula> subFormulas;

    public Conjunction(List<Formula> subFormulas) {
        this.subFormulas = subFormulas;
    }

    @Override
    public Set<Variable> getVariables() {
        Set<Variable> result = new HashSet<>();
        for (Formula f : subFormulas) {
            result.addAll(f.getVariables());
        }
        return result;
    }

    @Override
    public boolean verify(Context context, GraphMorphism instance) {
        for (Formula f : subFormulas) {
            if (!f.verify(context, instance)) {
                return false;
            }
        }
        return true;
    }
}
