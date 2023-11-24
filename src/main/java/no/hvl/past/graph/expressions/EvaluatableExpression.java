package no.hvl.past.graph.expressions;

import com.google.common.collect.Multimap;
import no.hvl.past.names.Name;

import java.util.List;
import java.util.Set;

public abstract class EvaluatableExpression {

    public abstract Set<Name> requiredFeatures();

    public abstract List<Name> evaluate(Multimap<Name, Name> actualParameterMap);
}
