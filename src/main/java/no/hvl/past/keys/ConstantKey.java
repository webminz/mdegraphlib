package no.hvl.past.keys;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Collections;
import java.util.List;

/**
 * A key that always evaluates to the same value.
 * Attention: Should be used in connection with concatenation,
 * otherwise all elements would be always identified!
 */
public class ConstantKey implements Key {

    private final Graph carrierGraph;
    private final Name value;
    private final Name definedOn;

    public ConstantKey(Graph carrierGraph, Name keyName, Name value, Name definedOn) {
        this.carrierGraph = carrierGraph;
        this.value = value;
        this.definedOn = definedOn;
    }

    @Override
    public Graph targetGraph() {
        return carrierGraph;
    }

    @Override
    public Name definedOnType() {
        return definedOn;
    }

    @Override
    public List<Triple> requiredProperties() {
        return Collections.emptyList();
    }

    @Override
    public Name evaluate(Name element, GraphMorphism typedContainer) {
        return value;
    }

    @Override
    public Name evaluate(Object element) {
        return value;
    }

    @Override
    public Name getName() {
        return value;
    }
}
