package no.hvl.past.graph.modification;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.names.Name;

import java.util.Optional;

public interface Version {

    Graph get() throws GraphError;

    long revision();

    Optional<Name> name();

}
