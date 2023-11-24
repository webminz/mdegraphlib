package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;

import java.util.stream.Stream;

/**
 * A graph that additionally comprises an inheritance relation between nodes.
 */
public interface InheritanceGraph extends Graph {

    /**
     * Returns true if the given node is a specialization of the second one.
     */
    boolean isUnder(Name subnode, Name supernode);

    Stream<Tuple> directInheritances();

    default Stream<Name> directSuperTypesOf(Name node) {
        return directInheritances().filter(t -> t.getDomain().equals(node)).map(Tuple::getCodomain);
    }


    @Override
    default boolean isInvariant(Name node1, Name node2) {
        return isUnder(node1, node2) || isUnder(node2, node1);
    }

    @Override
    default Graph rename(Name newName) {
        return new InheritanceGraph() {
            @Override
            public boolean isUnder(Name subnode, Name supernode) {
                return InheritanceGraph.this.isUnder(subnode, supernode);
            }

            @Override
            public Stream<Tuple> directInheritances() {
                return InheritanceGraph.this.directInheritances();
            }

            @Override
            public Stream<Triple> elements() {
                return InheritanceGraph.this.elements();
            }

            @Override
            public Name getName() {
                return newName;
            }

            @Override
            public boolean isInfinite() {
                return InheritanceGraph.this.isInfinite();
            }
        };
    }
}
