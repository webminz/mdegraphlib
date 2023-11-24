package no.hvl.past.graph.expressions;

import com.google.common.collect.Multimap;
import no.hvl.past.names.Name;

import java.util.ArrayList;
import java.util.List;

public class OrExpression extends CompositeExpression {

    public OrExpression(List<EvaluatableExpression> subExpressions) {
        super(subExpressions);
    }

    @Override
    public List<Name> evaluate(Multimap<Name, Name> actualParameterMap) {
        List<Name> results = new ArrayList<>();
        for (EvaluatableExpression expression : getSubExpressions()) {
            results.addAll(expression.evaluate(actualParameterMap));
        }
        return results;
    }
}
