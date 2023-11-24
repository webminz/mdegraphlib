package no.hvl.past.graph.expressions;

import com.google.common.collect.Multimap;
import no.hvl.past.names.Name;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConstantExpression extends EvaluatableExpression {

    private final List<Name> values;

    public ConstantExpression(List<Name> values) {
        this.values = values;
    }


    @Override
    public Set<Name> requiredFeatures() {
        return Collections.emptySet();
    }

    @Override
    public List<Name> evaluate(Multimap<Name, Name> actualParameterMap) {
        return values;
    }
}
