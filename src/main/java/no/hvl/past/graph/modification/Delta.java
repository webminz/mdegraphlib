package no.hvl.past.graph.modification;


import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphError;
import no.hvl.past.graph.names.Name;

import java.util.Optional;

public class Delta implements Version {

    private final Version old;

    private final Diff diff;

    private final Optional<Name> deltaName;

    public Delta(Version old, Diff diff) {
        this.old = old;
        this.diff = diff;
        this.deltaName = Optional.empty();
    }

    public Delta(Version old, Diff diff, Name deltaName) {
        this.old = old;
        this.diff = diff;
        this.deltaName = Optional.of(deltaName);
    }

    @Override
    public Graph get() throws GraphError {
        return diff.applyTo(old.get());
    }

    @Override
    public long revision() {
        return  1L + old.revision();
    }

    @Override
    public Optional<Name> name() {
        return deltaName;
    }
}
