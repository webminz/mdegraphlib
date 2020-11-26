package no.hvl.past.graph;

import java.util.Set;
import java.util.stream.Stream;

import no.hvl.past.graph.elements.Triple;

/**
 * A sketch (a.k.a diagrammatic graph) is a graph together with a set of diagrams on this graph.
 */
public interface Sketch extends Element {

    /**
     * The underlying graph of the sketch.
     * @return
     */
    Graph carrier();

    /**
     * The collection of all diagrams.
     */
    Stream<Diagram> diagrams();


    @Override
    default FrameworkElement elementType() {
        return FrameworkElement.SKETCH;
    }

    /**
     * All diagrams sitting on the given element.
     */
    default Stream<Diagram> diagramsOn(Triple element) {
        return this.diagrams().filter(diagram -> diagram.isInScope(element));
    }

    /**
     * All diagrams sitting on a given subgraph.
     */
    default Stream<Diagram> diagramsOn(Set<Triple> subgraph) {
        return this.diagrams().filter(diagram -> subgraph.stream().allMatch(diagram::isInScope));
    }

    /**
     * All diagrams with the given label.
     */
    default Stream<Diagram> diagramWithLabel(Label label) {
        return this.diagrams().filter(diagram -> diagram.label().map(label::equals).orElse(false));
    }
}
