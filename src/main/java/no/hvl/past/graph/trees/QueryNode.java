package no.hvl.past.graph.trees;

import no.hvl.past.attributes.TypedVariables;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A special type of Nodes in a query tree.
 */
public interface QueryNode extends TypedNode {

    @Override
    default Name elementName() {
        return parent().map(n -> branchName().childOf(elementName())).orElse(branchName());
    }

    Name branchName();

    Name typing();

    @Override
    default Optional<Name> nodeType() {
        return Optional.of(typing());
    }

    @Override
    Stream<QueryNodeChildren> children();

    interface Root extends QueryNode {

        default Name messageType() {
            return queryResultEdge().getSource();
        }

        @Override
        default Name typing() {
            return queryResultEdge().getTarget();
        }

        Triple queryResultEdge();

        @Override
        default void aggregateSubtree(Set<Triple> result) {
            result.add(Triple.node(branchName().source()));
            result.add(Triple.node(branchName().target()));
            result.add(Triple.edge(branchName().source(), branchName(), branchName().target()));
            children().map(TypedChildrenRelation::child).forEach(n -> n.aggregateSubtree(result));
        }

        @Override
        default void aggregateTypedPart(Set<Triple> elements, Set<Tuple> mappings) {
            mappings.add(new Tuple(branchName().source(), messageType()));
            mappings.add(new Tuple(branchName().target(), typing()));
            mappings.add(new Tuple(branchName(), queryResultEdge().getLabel()));
            elements.add(Triple.node(branchName().source()));
            elements.add(Triple.node(branchName().target()));
            elements.add(Triple.edge(branchName().source(), branchName(), branchName().target()));
        }

        default QueryNodeChildren asEdge() {
            return new QueryNodeChildren() {
                @Override
                public Triple feature() {
                    return queryResultEdge();
                }

                @Override
                public QueryNode child() {
                    return QueryNode.Root.this;
                }

                @Override
                public boolean isProjection() {
                    return true;
                }

                @Override
                public boolean isSelection() {
                    return false;
                }

                @Override
                public Node parent() {
                    return Root.this.parent().orElse(new Node.Impl(Name.identifier("ROOT")));
                }

                @Override
                public Name key() {
                    return branchName();
                }

                @Override
                public boolean isCollection() {
                    return Root.this.parent().isPresent();
                }

                @Override
                public int index() {
                    return 0;
                }
            };
        }
    }

}
