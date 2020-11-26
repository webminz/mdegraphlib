package no.hvl.past.logic;

import no.hvl.past.names.Name;
import no.hvl.past.names.Variable;

import java.util.HashSet;
import java.util.Set;

public abstract class Quantification extends Formula {

    public static class TypedVariable {
        private final Variable variable;
        private final Name type;

        public TypedVariable(Variable variable, Name type) {
            this.variable = variable;
            this.type = type;
        }

        public Variable getVariable() {
            return variable;
        }

        public Name getType() {
            return type;
        }
    }

    private final Set<TypedVariable> variables;

    private final Formula nested;

    public Quantification(Set<TypedVariable> variables, Formula nested) {
        this.variables = variables;
        this.nested = nested;
    }

    public Set<Variable> getVariables() {
        Set<Variable> result = new HashSet<>();
        this.variables.forEach(tv -> result.add(tv.variable));
        result.addAll(nested.getVariables());
        return result;
    }

}
