package no.hvl.past.logic;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.names.Variable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Disjunction<Sig extends Signature> extends FormulaCombinator<Sig> {

    public Disjunction(Formula<Sig> lhs, Formula<Sig> rhs) {
        super(lhs, rhs);
    }

    @Override
    public boolean isSatisfied(Model<Sig> model) {
        return getLhs().isSatisfied(model) || getRhs().isSatisfied(model);
    }
}
