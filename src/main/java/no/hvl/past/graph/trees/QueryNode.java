package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A special type of Nodes in a query tree.
 */
public interface QueryNode extends TypedNode {

    Name branchName();

    @Override
    default Name elementName() {
        return parent().map(n -> branchName().childOf(elementName())).orElse(branchName());
    }

    default Stream<QueryBranch> queryChildren() {
        return children().filter(b -> b instanceof QueryBranch).map(b -> (QueryBranch) b);
    }

    default Stream<QueryBranch> queryChildrenByLabel(String label) {
        return childrenByKey(label).filter(b -> b instanceof QueryBranch).map(b -> (QueryBranch) b);
    }

    interface Root extends QueryNode {

        default Name messageType() {
            return queryResultEdge().getSource();
        }

        @Override
        default Name nodeType() {
            return queryResultEdge().getTarget();
        }

        Triple queryResultEdge();

        @Override
        default void aggregateSubtree(Set<Triple> result) {
            result.add(Triple.node(branchName().source()));
            result.add(Triple.node(branchName().target()));
            result.add(Triple.edge(branchName().source(), branchName(), branchName().target()));
            queryChildren().map(TypedBranch::child).forEach(n -> n.aggregateSubtree(result));
        }

        @Override
        default void aggregateTypedPart(Set<Triple> elements, Set<Tuple> mappings) {
            mappings.add(new Tuple(branchName().source(), messageType()));
            mappings.add(new Tuple(branchName().target(), nodeType()));
            mappings.add(new Tuple(branchName(), queryResultEdge().getLabel()));
            elements.add(Triple.node(branchName().source()));
            elements.add(Triple.node(branchName().target()));
            elements.add(Triple.edge(branchName().source(), branchName(), branchName().target()));
        }

    }

}
