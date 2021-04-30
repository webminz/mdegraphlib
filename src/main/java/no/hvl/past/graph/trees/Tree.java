package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Model;
import no.hvl.past.logic.Signature;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface Tree extends Graph {

    class Impl implements Tree {
        private final Node root;
        private final Name name;
        private Set<Triple> cachedGraphRepresentation;
        private Map<Name, Node> nodeByIdCache;

        public Impl(Node root, Name name) {
            this.root = root;
            this.name = name;
        }

        @Override
        public Optional<Node> findNodeById(Name elementId) {
            if (nodeByIdCache.containsKey(elementId)) {
                return Optional.ofNullable(nodeByIdCache.get(elementId));
            } else {
                Optional<Node> nodeById = Tree.super.findNodeById(elementId);
                if (nodeById.isPresent()) {
                    nodeByIdCache.put(elementId, nodeById.get());
                } else {
                    nodeByIdCache.put(elementId, null);
                }
                return nodeById;
            }
        }

        @Override
        public Node root() {
            return root;
        }

        @Override
        public Name getName() {
            return name;
        }

        @Override
        public Stream<Triple> elements() {
            if (cachedGraphRepresentation == null) {
                this.cachedGraphRepresentation = new HashSet<>();
                root.aggregateSubtree(this.cachedGraphRepresentation);
            }
            return cachedGraphRepresentation.stream();
        }

        @Override
        public boolean isInfinite() {
            return false;
        }
    }

    default Optional<Node> findNodeById(Name elementId) {
        return this.root().findByName(elementId);
    }


    Node root();

    @Override
    default Stream<Triple> outgoing(Name fromNode) {
        return root().findByName(fromNode).map(Node::outgoing).orElse(Stream.empty());
    }

    @Override
    default Stream<Triple> elements() {
        return root().subTree();
    }


}
