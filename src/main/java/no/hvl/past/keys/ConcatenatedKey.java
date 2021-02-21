package no.hvl.past.keys;

import no.hvl.past.graph.Element;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConcatenatedKey implements Key {

    private final Graph carrier;
    private final Name rootType;
    private final List<Key> childKeys;

    public ConcatenatedKey(Graph carrier, Name rootType, List<Key> childKeys) {
        this.carrier = carrier;
        this.rootType = rootType;
        this.childKeys = childKeys;
    }

    @Override
    public Graph targetGraph() {
        return carrier;
    }

    @Override
    public Name definedOnType() {
        return rootType;
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
        List<Name> childEvals = new ArrayList<>();
        for (Key child : childKeys) {
            childEvals.add(child.evaluate(element));
        }
        return Name.concat(childEvals);    }

    @Override
    public Name getName() {
        return Name.concat(childKeys.stream().map(Element::getName).collect(Collectors.toList()));
    }
}
