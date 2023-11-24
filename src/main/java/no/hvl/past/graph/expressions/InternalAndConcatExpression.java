package no.hvl.past.graph.expressions;

import com.google.common.collect.Multimap;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.names.Name;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class InternalAndConcatExpression extends CompositeExpression {

    public InternalAndConcatExpression(List<EvaluatableExpression> subExpressions) {
        super(subExpressions);
    }

    @Override
    public List<Name> evaluate(Multimap<Name, Name> actualParameterMap) {
        List<Name> parameterList = new ArrayList<>();
        for (EvaluatableExpression expr : getSubExpressions()) {
            Collection<Name> evaluate = expr.evaluate(actualParameterMap);
            if (evaluate.isEmpty()) {
                parameterList.add(ErrorValue.INSTANCE);
            } else {
                parameterList.addAll(evaluate);
            }
        }
        return Collections.singletonList(Name.merge(parameterList));
    }
}


