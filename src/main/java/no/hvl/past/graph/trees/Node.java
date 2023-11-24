package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.AnonymousIdentifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;
import no.hvl.past.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a node in a tree.
 * A node may have a collection attributes (= key-value pairs where both are names)
 * A node may have a number of children, which are nodes.
 */
public interface Node {

    // Common constants

    Name ROOT_NAME = Name.identifier("/");

    Name FORREST_NAME = Name.identifier("$BUNDLE");

    // Mandatory core methods

    /**
     * Each node in a tree should have unique name.
     * If this node is complex object (i.e.) it has children, this name is rather unimportant.
     * If this node is a leaf then the name is important, it might be a reference or a value then.
     */
    Name elementName();

    /**
     * A node should in most cases by typed over an element from a suitable schema (metamodel).
     */
    Optional<Name> nodeType();

    /**
     * Retrieves the children of this node, represented by {@link Branch} objects.
     */
    Stream<Branch> children();

    /**
     * The name of parent node.
     */
    Optional<Name> parentNode();


    // Extended interface with many convenient methods that can be derived directly from the core API.

    default boolean isLeaf() {
        return children().noneMatch(x -> true);
    }

    default Stream<Name> keys() {
        return children().map(Branch::key).distinct();
    }

    default Stream<Node> childNodes() {
        return children().map(Branch::child);
    }

    default Stream<Node> childNodesByKey(Name key) {
        return childrenByKey(key).map(Branch::child);
    }

    default Stream<Branch> childrenByKey(Name key) {
        return children().filter(child -> child.key().equals(key));
    }

    default Stream<Branch> childrenByType(Name branchTypeName) {
        return children().filter(branch -> branch.edgeTyping().map(branchTypeName::equals).orElse(false));
    }

    default Optional<Branch> childrenByKeyAndNo(Name key, int no) {
        List<Branch> collect = childrenByKey(key).collect(Collectors.toList());
        if (no < collect.size()) {
            return Optional.of(collect.get(no));
        } else {
            return Optional.empty();
        }
    }

    default Stream<Branch> childrenByType(Triple type) {
        return nodeType()
                .map(nodetype -> nodetype.equals(type.getSource()) ? childrenByType(type.getLabel()) : Stream.<Branch>empty())
                .orElse(Stream.empty());
    }

    default Stream<Node> typedChildNodes() {
        return childNodes().filter(node -> node.nodeType().isPresent());
    }

    default Optional<Node> byName(Name elementName) {
        if (elementName().equals(elementName)) {
            return Optional.of(this);
        }
        return children().map(child -> child.child().byName(elementName)).filter(Optional::isPresent).findFirst().map(Optional::get);
    }

    default boolean isSimple() {
        return isLeaf();
    }

    default boolean isComplex() {
        return !isSimple();
    }

    default boolean isCollection(Name featureKey) {
        return childrenByKey(featureKey).count() > 1;
    }

    default boolean isAtomic(Name featureKey) {
        return childrenByKey(featureKey).count() <= 1;
    }

    default boolean isEmpty(Name featureKey) {
        return childrenByKey(featureKey).noneMatch(x -> true);
    }

    // Extra accessors available when given the containing tree.


    default boolean isRoot(Tree container) {
        return container.root().equals(this);
    }

    default Optional<Node> parentNode(Tree container) {
        return parentNode().flatMap(container::findNodeById);
    }

    default Stream<Branch> siblings(Tree container) {
        return parentNode(container).map(node -> node.children().filter(b -> !b.child().equals(this))).orElse(Stream.empty());
    }

    default Stream<Node> siblingNodes(Tree container) {
        return siblings(container).map(Branch::child);
    }


    // Tree analysis

    default boolean isCycleFree() {
        return this.hasCycle(new HashSet<>());
    }

    default boolean hasCycle(Set<Name> visitedNodeNames) {
        if (elementName() instanceof Value) {
            return false;
        }
        if (visitedNodeNames.contains(elementName())) {
            return true;
        }
        visitedNodeNames.add(elementName());
        return children().reduce(false, (agg, branch) -> agg || branch.child().hasCycle(visitedNodeNames), (l, r) -> l || r);
    }

    default int depth() {
        return children().map(b -> b.child().depth()).max(Integer::compare).orElse(0) + 1;
    }


    // Graph-like API

    default Stream<Triple> outgoing() {
        return children().map(Branch::asEdge);
    }

    default boolean contains(Name nodeName) {
        if (elementName().equals(nodeName)) {
            return true;
        }
        if (isLeaf()) {
            return false;
        }
        return children().map(Branch::child).anyMatch(n -> n.contains(nodeName));
    }


    // Aggregators

    default void collectTriples(Consumer<Triple> collector) {
        collector.accept(Triple.node(elementName()));
        children().forEach(child -> {
            collector.accept(child.asEdge());
            child.child().collectTriples(collector);
        });
    }

    default void aggregateSubtree(Set<Triple> result) {
        collectTriples(result::add);
    }

    default Set<Triple> subTree() {
        Set<Triple> result = new HashSet<>();
        aggregateSubtree(result);
        return result;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") // there is an isPresent but IntelliJ does not detect it
    default void collectTypedSubtree(Consumer<Triple> tripleCollector, Consumer<Tuple> tupleConsumer) {
        this.nodeType().ifPresent(typ -> {
            tripleCollector.accept(Triple.node(elementName()));
            tupleConsumer.accept(new Tuple(elementName(), typ));
        });
        children().filter(b -> b.edgeTyping().isPresent()).forEach(b -> {
            Triple asEdge = b.asEdge();
            tripleCollector.accept(asEdge);
            tupleConsumer.accept(new Tuple(asEdge.getLabel(), b.edgeTyping().get()));
        });
        typedChildNodes().forEach(child -> child.collectTypedSubtree(tripleCollector, tupleConsumer));
    }

    default void aggregateTypedSubTree(Set<Triple> elements, Set<Tuple> mappings) {
       collectTypedSubtree(elements::add, mappings::add);
    }

    default Pair<Set<Triple>, Set<Tuple>> typedSubTree() {
        Set<Triple> tripleSet = new HashSet<>();
        Set<Tuple> tupleSet = new HashSet<>();
        aggregateTypedSubTree(tripleSet, tupleSet);
        return new Pair<>(tripleSet, tupleSet);
    }

    default void collectNodesOfType(Name nodeType, Consumer<Node> collector) {
        nodeType().ifPresent(nt -> { if (nodeType.equals(nt)) {
           collector.accept(this);
        }});
        childNodes().forEach(child -> collectNodesOfType(nodeType, collector));
    }

    default void aggregateNodesOfType(Name nodeType, Set<Node> aggregate) {
        collectNodesOfType(nodeType, aggregate::add);
    }

    default void collectTriplesOfType(Triple type, Consumer<Triple> collector) {
        if (type.isNode()) {
            collectNodesOfType(type.getLabel(), node -> collector.accept(Triple.node(node.elementName())));
        } else {
            children().forEach(branch -> {
                if (branch.type().isPresent() && branch.type().get().equals(type)) {
                    collector.accept(branch.asEdge());
                }
                branch.child().collectTriplesOfType(type, collector);
            });
        }
    }


   default void sendTo(TreeReceiver handler) throws Exception {
       if (isSimple()) {
           handler.valueLeaf(elementName());
           if (nodeType().isPresent()) {
               handler.nodeType(nodeType().get());
           }
       } else {
           handler.startComplexNode();
           if (!(elementName() instanceof AnonymousIdentifier)) {
               handler.nodeId(elementName());
           }
           if (nodeType().isPresent()) {
               handler.nodeType(nodeType().get());
           }
           for (Name key : this.keys().collect(Collectors.toSet())) {
               handler.startBranch(key, this.isCollection(key));
               for (Node n : this.childrenByKey(key).map(Branch::child).collect(Collectors.toSet())) {
                   n.sendTo(handler);
               }
               handler.endBranch();
           };
           // TODO id
           handler.endComplexNode();
       }
   }

    default Optional<Name> attribute(Name key) {
        return childNodesByKey(key).findFirst().map(Node::elementName);
    }


    default boolean structurallyEquivalent(Node node) {
        if (this.isLeaf() && node.isLeaf()) {
            return this.elementName().equals(node.elementName());
        }
        List<Name> thisKeys = this.keys().collect(Collectors.toList());
        if (thisKeys.equals(node.keys().collect(Collectors.toList()))) {
            for (Name branchKey : thisKeys) {
                List<Node> thisList = this.childNodesByKey(branchKey).collect(Collectors.toList());
                List<Node> otherList = node.childNodesByKey(branchKey).collect(Collectors.toList());
                if (thisList.size() != otherList.size()) {
                    return false;
                }
                for (int i = 0; i < thisList.size(); i++) {
                    if (!thisList.get(i).structurallyEquivalent(otherList.get(i))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
