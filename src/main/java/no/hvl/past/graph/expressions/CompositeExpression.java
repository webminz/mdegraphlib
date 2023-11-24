package no.hvl.past.graph.expressions;

import no.hvl.past.names.Name;

import java.util.*;

public abstract class CompositeExpression extends EvaluatableExpression {

    private final List<EvaluatableExpression> subExpressions;

    public CompositeExpression(List<EvaluatableExpression> subExpressions) {
        this.subExpressions = subExpressions;
    }


    @Override
    public Set<Name> requiredFeatures() {
        HashSet<Name> result = new HashSet<>();
        for (EvaluatableExpression expr : subExpressions) {
            result.addAll(expr.requiredFeatures());
        }
        return result;
    }

    protected List<EvaluatableExpression> getSubExpressions() {
        return subExpressions;
    }
}
