package no.hvl.past.graph.modification;


import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.names.Name;

import java.util.Optional;

public class AtomicVersion implements Version {

    private final Graph graph;

    private final long revision;

    private final Name versionName;

    public AtomicVersion(Graph graph) {
        this.graph = graph;
        this.revision = 0L;
        this.versionName = graph.getName();
    }

    public AtomicVersion(Graph graph, long revision, Name versionName) {
        this.graph = graph;
        this.revision = revision;
        this.versionName = versionName;
    }

    @Override
    public Graph get() throws GraphError {
        return graph;
    }

    @Override
    public long revision() {
        return revision;
    }

    @Override
    public Optional<Name> name() {
        return Optional.of(versionName);
    }
}
