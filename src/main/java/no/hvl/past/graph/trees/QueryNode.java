package no.hvl.past.graph.trees;

import no.hvl.past.attributes.TypedVariables;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.stream.Stream;

/**
 * A special type of Nodes in a query tree.
 */
public interface QueryNode {

    /**
     * The name of the relation that should be filtered, i.e. projection.
     * (in SQL: SELECT,FROM,JOIN)
     */
    Name filteredElementName();

    /**
     * The predicate the should be applied to the target of the projection (in SQL: WHERE).
     */
    Formula<TypedVariables> filterPredicate();

    /**
     * Further queries that should be applied to children.
     */
    Stream<QueryNode> children();


}
