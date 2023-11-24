package no.hvl.past.graph.expressions;

import com.google.common.collect.Multimap;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.*;

public class PropertyExpression extends EvaluatableExpression {

    private final Triple propertyTriple;

    public PropertyExpression(Triple propertyTriple) {
        this.propertyTriple = propertyTriple;
    }


    @Override
    public Set<Name> requiredFeatures() {
        return Collections.singleton(propertyTriple.getLabel());
    }

    @Override
    public List<Name> evaluate(Multimap<Name, Name> actualParameterMap) {
        return new ArrayList<>(actualParameterMap.get(propertyTriple.getLabel()));
    }
}
