package no.hvl.past.graph;

import no.hvl.past.names.Name;

/**
 * A graph that additionally comprises an inheritance relation between nodes.
 */
public interface InheritanceGraph extends Graph {

    /**
     * Returns true if the given node is a specialization of the second one.
     */
    boolean isUnder(Name subnode, Name supernode);

}
