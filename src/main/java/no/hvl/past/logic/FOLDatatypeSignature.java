package no.hvl.past.logic;

import com.google.common.collect.Sets;
import no.hvl.past.attributes.DataOperation;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import no.hvl.past.names.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The signature for First Order Logic (FOL) over the built-in datatypes, i.e.
 * a set of _variable names_.
 *
 * Models are assignments of these names to base type values.
 * Predicates are the boolean base type operations.
 */
public class FOLDatatypeSignature implements Signature {

    private final Set<Name> variables;

    public FOLDatatypeSignature(Set<Name> variables) {
        this.variables = variables;
    }

    public Set<Name> getVariables() {
        return variables;
    }

    public static class VariableAssignment implements Model<FOLDatatypeSignature> {

        private final Map<Name, Value> assignmentMap;
        private final FOLDatatypeSignature signature;

        public VariableAssignment(Map<Name, Value> assignmentMap, FOLDatatypeSignature signature) {
            this.assignmentMap = assignmentMap;
            this.signature = signature;
        }

        public boolean isClosed() {
            return signature.variables.stream().allMatch(assignmentMap::containsKey);
        }

        public boolean isAssigned(Name var) {
            return assignmentMap.containsKey(var);
        }

        public Value getAssignment(Name var) {
            return assignmentMap.get(var);
        }
    }

    public static class FOLPredicate extends FormulaLiteral<FOLDatatypeSignature> {

        private final List<Name> variable;
        private final DataOperation operation;

        public FOLPredicate(List<Name> variable, DataOperation operation) {
            this.variable = variable;
            this.operation = operation;
        }

        @Override
        public boolean isSatisfied(Model<FOLDatatypeSignature> model) {
            if (model instanceof VariableAssignment) {
                VariableAssignment ass = (VariableAssignment) model;
                Value[] arguments = new Value[variable.size()];
                for (int i = 0; i < variable.size(); i++) {
                    if (ass.isAssigned(variable.get(i))) {
                        arguments[i] = ass.getAssignment(variable.get(i));
                    }
                }
                return operation.apply(arguments).equals(Name.trueValue());
            }
            return false;
        }
    }


}
