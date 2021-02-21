package no.hvl.past.graph;


import no.hvl.past.graph.elements.EquivalenceClass;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

public interface Visitor {

    void handleElementName(Name name);

    void handleNode(Name node);

    void handleEdge(Triple triple);

    void handleMapping(Tuple tuple);

    void handleFormula(Formula<Graph> graphFormula);

    void beginGraph();

    void endGraph();

    void beginMorphism();

    void endMorphism();

    void beginSketch();

    void beginDiagram();

    void endDiagram();

    void endSketch();

    void beginSpan();

    void endSpan();

}
