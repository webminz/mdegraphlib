package no.hvl.past.attributes;

import no.hvl.past.logic.Model;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class VariableAssignment implements Model<TypedVariables> {

    private final Map<Name, Value> assignmentMap;

    public VariableAssignment(Map<Name, Value> assignmentMap) {
        this.assignmentMap = assignmentMap;
    }

    public Stream<Name> variables() {
        return assignmentMap.keySet().stream();
    }

    public Optional<Value> assignedValue(Name variable) {
        return Optional.ofNullable(assignmentMap.get(variable));
    }

}
