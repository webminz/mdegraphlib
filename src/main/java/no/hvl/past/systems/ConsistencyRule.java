package no.hvl.past.systems;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.stream.Stream;

public interface ConsistencyRule {

    Name commonality();

    Stream<Name> violations(ComprData instance);

}
