package no.hvl.past.keys;

import no.hvl.past.attributes.StringValue;
import no.hvl.past.graph.Element;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConcatenatedKey implements Key {

    private final Graph carrier;
    private final Name targetType;
    private final List<Key> childKeys;

    public ConcatenatedKey(Graph carrier, Name targetType, List<Key> childKeys) {
        this.carrier = carrier;
        this.childKeys = childKeys;
        this.targetType = targetType;
    }

    @Override
    public Graph container() {
        return carrier;
    }


    @Override
    public Name targetType() {
        return targetType;
    }

    @Override
    public List<Triple> requiredProperties() {
        List<Triple> result = new ArrayList<>();
        for (Key child : childKeys) {
            result.addAll(child.requiredProperties());
        }
        return result;
    }

    @Override
    public Name evaluate(Name element, GraphMorphism typedContainer) throws KeyNotEvaluated {
        List<Name> childEvals = new ArrayList<>();
        for (Key child : childKeys) {
            childEvals.add(child.evaluate(element, typedContainer));
        }
        return Name.concat(childEvals);
    }

    @Override
    public Name evaluate(Object element) throws KeyNotEvaluated {
        // TODO add parsing etc.
        List<StringValue> childEvals = new ArrayList<>();
        for (Key child : childKeys) {
            Name evaluate = child.evaluate(element);
            if (!evaluate.isValue() && !(evaluate instanceof StringValue)) {
                throw new KeyNotEvaluated();
            }
            childEvals.add((StringValue) evaluate);
        }
        StringValue result = Name.value("");
        for (StringValue v : childEvals) {
            result = result.concat(v);
        }
        return result;
    }

    @Override
    public Name getName() {
        return Name.concat(childKeys.stream().map(Element::getName).collect(Collectors.toList()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetType, this.childKeys);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConcatenatedKey) {
            ConcatenatedKey k = (ConcatenatedKey) obj;
            return this.targetType.equals(k.targetType) && childKeys.equals(k.childKeys);
        }
        return false;
    }
}
