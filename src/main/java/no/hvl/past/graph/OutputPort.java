package no.hvl.past.graph;

import no.hvl.past.graph.Triple;
import no.hvl.past.graph.Tuple;
import no.hvl.past.graph.names.Name;

import java.util.List;

public interface OutputPort<R> {

    void beginMultispan(Name spanName, List<Name> diagramMorphismNames, List<Name> diagramNodeNames);

    void endMultispan();

    void beginMorphism(Name morphismName, Name domainName, Name codomainName);

    void endMorphism();

    void beginGraph(Name graphName);

    void endGraph();

    void handleTriple(Triple triple);

    void handleTuple(Tuple tuple);

    boolean isOutputReady();

    R getOutput();

}
