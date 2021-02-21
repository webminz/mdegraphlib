package no.hvl.past.attributes;

import no.hvl.past.logic.Formula;
import no.hvl.past.logic.Model;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.List;

/**
 * A first order statement.
 */
public class AtomicFormula implements Formula<TypedVariables> {

    private final List<Name> arguments;
    private final DataOperation operation;

    public AtomicFormula(List<Name> arguments, DataOperation operation) {
        this.arguments = arguments;
        this.operation = operation;
    }

    @Override
    public boolean isSatisfied(Model<TypedVariables> model) {
        if (model instanceof VariableAssignment) {
            VariableAssignment assignment = (VariableAssignment) model;
            if (arguments.stream().allMatch(var -> assignment.assignedValue(var).isPresent())) {
                Value[] args = new Value[arguments.size()];
                for (int i = 0; i < arguments.size(); i++) {
                    args[i] = assignment.assignedValue(arguments.get(i)).get();
                }
                Value result = operation.apply(args);
                return result.equals(Name.trueValue());
            }
        }
        return false;
    }
}
