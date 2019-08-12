package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

public interface Element {

    Name getName();

    Graph toGraph(Name containerName);

    void sendTo(OutputPort<?> port);
}
