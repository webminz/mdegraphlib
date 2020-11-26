package no.hvl.past.graph;


import no.hvl.past.graph.elements.EquivalenceClass;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;

public interface Visitor {

    void handleName(Name name);

    void beginGraph();

    void handleTriple(Triple triple);

    void endGraph();

    void beginMorphism();

    void handleTuple(Tuple tuple);

    void endMorphism();

    void beginSketch();

    void beginDiagram();

    void endDiagram();

    void endSketch();

    void beginSketchMorphism();

    void endSketchMorphism();

    void beginSpan();

    void handleClass(EquivalenceClass clazz);

    void endSpan();

    void beginSpanMorphism();

    void endSpanMorphism();

    void handleDiagramLabel(Name name);
}
