package no.hvl.past.graph.trees;

import no.hvl.past.attributes.VariableAssignment;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a node in a tree.
 * A node may have a collection attributes (= key-value pairs where both are names)
 * A node may have a number of children, which are nodes.
 */
public interface Node {

    Name elementName();

    Optional<Name> parentName();

    Optional<Name> attribute(Name attributeName);

    Stream<Name> attributeNames();

    Stream<Node> children(Name childBranchName);

    Stream<Name> childBranchNames();

    default boolean isLeaf() {
        return allChildren().noneMatch(x -> true);
    }

    default Stream<Node> allChildren() {
        return childBranchNames().flatMap(this::children);
    }

    default void aggregateSubtree(Set<Triple> result) {
        attributeNames()
                .map(att -> attribute(att).map(val -> Triple.edge(elementName(), att, val)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(result::add);

        result.add(Triple.node(elementName()));
        parentName().ifPresent(parent -> result.add(Triple.edge(
                parent,
                elementName().childOf(parent),
                elementName()
        )));

        allChildren().forEach(node -> node.aggregateSubtree(result));
    }


    default Stream<Triple> subTree() {
        Set<Triple> result = new HashSet<>();
        aggregateSubtree(result);
        return result.stream();
    }

    default Node query(QueryNode query) {
        return new NodeImpl.FilteredNode(this, query);
    }

    default VariableAssignment attributeValues() {
        Map<Name, Value> ass = new HashMap<>();
        attributeNames().forEach(att ->{
            if (attribute(att).isPresent()) {
                Name name = attribute(att).get();
                if (name instanceof Value) {
                    ass.put(att, (Value) name);
                }
            }
        });
        return new VariableAssignment(ass);
    }
}
