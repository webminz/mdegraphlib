package no.hvl.past.graph;

import no.hvl.past.names.Name;

public interface InheritanceGraph extends Graph {

    boolean isUnder(Name subnode, Name supernode);

}
