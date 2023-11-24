package no.hvl.past.attributes;

import no.hvl.past.logic.Model;
import no.hvl.past.logic.Signature;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.Map;

public class TypedVariables implements Signature {

    private final Map<Name, Class<? extends Value>> variableTyping;

    public TypedVariables(Map<Name, Class<? extends Value>> variableTyping) {
        this.variableTyping = variableTyping;
    }

    @Override
    public boolean isSyntacticallyCorrect(Model<? extends Signature> model) {
        if (model instanceof VariableAssignment) {
            VariableAssignment ass = (VariableAssignment) model;
            return ass.variables().allMatch(var -> variableTyping.containsKey(var)) &&
                    ass.variables().allMatch(var -> ass.assignedValue(var).map(val -> variableTyping.get(var).isAssignableFrom(val.getClass())).orElse(true));
        }
        return false;
    }

    // TODO builder
}
