package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

/**
 * Provides all the functionality, which is needed to label a tree structure with typing information.
 */
public interface TreeTypeLibrary {

    /**
     * Provides the type name of the root element.
     */
    Name rootTyping();

    /**
     * Provides the type information (if existent) for the current child element based on the type
     * of the current parent element and the label of the current branch (property).
     * The result is given as a {@link Triple} which simultaneously provides the type of the branch (edge label)
     * and the child (edge target).
     */
    Optional<Triple> childTyping(Name parentType, String childBranchLabel);
}
