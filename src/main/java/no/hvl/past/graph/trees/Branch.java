package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

public interface Branch {

    Optional<Triple> type();

    Name parentNode();

    Name key();

    Node child();

    long index();

    boolean isCollection();

    default Optional<Name> edgeTyping() {
        return type().map(Triple::getLabel);
    }

    default Optional<Name> parentTyping() {
        return type().map(Triple::getSource);
    }

    default Optional<Name> childTyping() {
        return type().map(Triple::getTarget);
    }

    default boolean isAttribute() {
        return child().isLeaf() && child().elementName().isValue();
    }

    default Triple asEdge() {
            if (isCollection()) {
                return Triple.edge(parentNode(), key().prefixWith(parentNode()).index(index()), child().elementName());
            } else {
                return Triple.edge(parentNode(), key().prefixWith(parentNode()), child().elementName());
            }
    }

}
