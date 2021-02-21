package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Model;
import no.hvl.past.logic.Signature;
import no.hvl.past.names.Name;

import java.util.stream.Stream;

public interface Tree extends Graph {

    static final Name ROOT = Name.identifier("/");

    Node root();

    default Tree query(QueryTree query) {
        return new Tree() {
            @Override
            public Node root() {
                return Tree.this.root().query(query.root());
            }

            @Override
            public Name getName() {
                return Name.identifier(query.textualRepresentation()).appliedTo(getName());
            }

            @Override
            public boolean isInfinite() {
                return false;
            }
        };
    }

    @Override
    default Stream<Triple> elements() {
        return root().subTree();
    }

    @Override
    default boolean isInfinite() {
        return false;
    }
}
