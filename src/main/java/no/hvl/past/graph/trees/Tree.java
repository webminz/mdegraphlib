package no.hvl.past.graph.trees;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public interface Tree extends Graph, GraphMorphism {

    // mandatory core part, i.e. a tree must provide a name and a root node.

    Node root();

    Optional<Graph> type();


    default void sendTo(TreeReceiver handler) throws Exception {
        if (type().isPresent()) {
            handler.treeType(type().get(), TreeTypeLibrary.fromGraphForRootElement(root().elementName().printRaw(), type().get()));
        }
        handler.startRoot(root().elementName());
        root().sendTo(handler);
        handler.endRoot();
    }

    // derived accessors

    default Optional<Name> map(Name name) {
        return findNodeById(name).flatMap(Node::nodeType);
    }

    default Optional<Node> findNodeById(Name elementId) {
        return this.root().byName(elementId);
    }


    @Override
    default Stream<Triple> allOutgoingInstances(Triple type, Name src) {
        return root().byName(src)
                    .map(node -> node.childrenByType(type))
                    .orElse(Stream.empty())
                .map(Branch::asEdge);
    }


    @Override
    default Stream<Triple> preimage(Triple to) {
        Set<Triple> preResult = new HashSet<>();
        root().collectTriplesOfType(to, preResult::add);
        return preResult.stream();
    }

    // Methods required from supertypes

    @Override
    default Stream<Triple> outgoing(Name fromNode) {
        return root().byName(fromNode).map(Node::outgoing).orElse(Stream.empty());
    }

    @Override
    default Stream<Triple> elements() {
        return root().subTree().stream();
    }


    default Graph domain() {
        return this;
    }

    @Override
    default Graph codomain() {
        return type().orElse(Universe.CYCLE); // if not explicitly specified, it is assumed to be typed over
                                              // the terminal object in the category of graphs
    }

    @Override
    default void accept(Visitor visitor) {
        GraphMorphism.super.accept(visitor);
    }

    @Override
    default boolean verify() {
        return GraphMorphism.super.verify();
    }

    @Override
    default boolean isInfinite() {
        return false;
    }



    default GraphMorphism typedPartToMorphism() {
        Pair<Set<Triple>, Set<Tuple>> elementsANDmappings = root().typedSubTree();
        return GraphMorphismImpl.create(
                getName(),
                new GraphImpl(
                        Name.identifier("dom").appliedTo(getName()),
                        elementsANDmappings.getFirst()),
                codomain(),
                elementsANDmappings.getSecond());

    }
}
