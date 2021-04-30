package no.hvl.past.graph.trees;


import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.PrintingStrategy;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Query trees are special trees that are used to filter elements.
 * They invert the ``normal'' representation of nodes and edges,
 * i.e. node actually represent edges
 */
public interface QueryTree extends TypedTree {

    Stream<QueryNode.Root> queryRoots();

    default boolean isMultiRootQuery() {
        return queryRoots().count() > 1;
    }

    /**
     * The root of the query.
     */
    default TypedNode root() {
        if (isMultiRootQuery()) {
            TypedNode.Builder builder = new TypedNode.Builder(getName(), null);
            queryRoots().forEach(q -> {
                builder.beginChild(getName().printRaw() + "." + q.branchName().printRaw(), q);
            });
            return builder.build();
        } else {
            Optional<QueryNode.Root> root = queryRoots().findFirst();
            if (root.isPresent()) {
                return root.get();
            } else {
                return new TypedNode.Impl(getName(), null);
            }
        }
    }

    @Override
    default Stream<Triple> elements() {
        Set<Triple> result = new LinkedHashSet<>();
        if (isMultiRootQuery()) {
            result.add(Triple.node(getName()));
            queryRoots().forEach(n -> {
                result.add(Triple.edge(getName(), n.branchName().prefixWith(getName()), n.branchName().source()));
                n.aggregateSubtree(result);
            });
        } else {
            queryRoots().findFirst().ifPresent(n -> n.aggregateSubtree(result));
        }
        return result.stream();
    }

    /**
     * Textual representation of the query.
     */
    String textualRepresentation();


}
