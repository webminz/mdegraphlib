package no.hvl.past.graph.expressions;

import com.google.common.collect.Multimap;
import no.hvl.past.attributes.StringValue;
import no.hvl.past.names.Name;
import no.hvl.past.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToStringConcatExpression extends CompositeExpression {

    public ToStringConcatExpression(List<EvaluatableExpression> subExpressions) {
        super(subExpressions);
    }

    @Override
    public List<Name> evaluate(Multimap<Name, Name> actualParameterMap) {
        List<Name> parameterList = new ArrayList<>();
        for (EvaluatableExpression expr : getSubExpressions()) {
            parameterList.addAll(expr.evaluate(actualParameterMap));
        }
        String result = StringUtils.fuseList(parameterList.stream().map(name -> {
            if (name instanceof StringValue) {
                return ((StringValue) name).getStringValue();
            } else {
                return name.printRaw();
            }
        }), "");

        return Collections.singletonList(Name.value(result));
    }
}
