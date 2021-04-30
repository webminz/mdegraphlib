package no.hvl.past.systems;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.trees.Node;
import no.hvl.past.graph.trees.TypedBranch;
import no.hvl.past.graph.trees.TypedNode;
import no.hvl.past.graph.trees.TypedTree;
import no.hvl.past.keys.Key;
import no.hvl.past.keys.KeyNotEvaluated;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents an abstract view of the data stemming from a system.
 */
public interface Data {

    /**
     * The system, this data is associated with.
     */
    Sys origin();

    /**
     * Retrieves the identifiers of all elements that are (transitively) typed over the given type.
     */
    Stream<Name> all(Name type);

    /**
     * Retrieves the values of the given property for the given element id.
     */
    Stream<Name> properties(Name elementId, Name propertyName);

    static Data fromMorphism(Sys system, GraphMorphism instance) {
        return new Data() {
            @Override
            public Sys origin() {
                return system;
            }

            @Override
            public Stream<Name> all(Name type) {
                return instance.allInstances(type).map(Triple::getLabel);
            }

            @Override
            public Stream<Name> properties(Name elementId, Name propertyName) {
                return instance.codomain().get(propertyName).map(t -> instance.allOutgoingInstances(t, elementId).map(Triple::getTarget)).orElse(Stream.empty());
            }

            @Override
            public Stream<Pair<Name, Name>> evaluate(Key k) {
                return all(k.sourceType())
                        .map(n -> k.evaluate(n, instance)
                                .map(res -> new Pair<>(res, n)))
                        .filter(Optional::isPresent)
                        .map(Optional::get);
            }

            @Override
            public Name typeOf(Name element) {
                return instance.map(element).orElse(null);
            }
        };
    }


    static Data fromTree(Sys system, TypedTree tree) {
        return new Data() {
            @Override
            public Sys origin() {
                return system;
            }

            @Override
            public Stream<Name> all(Name type) {
                Set<TypedNode> result = new HashSet<>();
                tree.root().nodesWithType(type, result);
                return result.stream().map(TypedNode::elementName);
            }

            @Override
            public Stream<Name> properties(Name elementId, Name propertyName) {
                return tree.findNodeById(elementId)
                        .filter(n -> n instanceof TypedNode)
                        .map(n -> (TypedNode)n)// TODO also include the parent branch?
                        .map(tn -> tn.typedChildren().filter(tb -> tb.typeFeature().getLabel().equals(propertyName)).map(TypedBranch::child).map(Node::elementName))
                        .orElse(Stream.empty());
            }

            @Override
            public Stream<Pair<Name, Name>> evaluate(Key k) {
                Set<TypedNode> result = new HashSet<>();
                tree.root().nodesWithType(k.sourceType(), result);
                return result.stream()
                        .map(tn ->
                            k.evaluate(tn, tree).map(keyValue -> new Pair<>(keyValue, tn.elementName()))
                        )
                        .filter(Optional::isPresent)
                        .map(Optional::get);
            }

            @Override
            public Name typeOf(Name element) {
                return tree.findNodeById(element).filter(n -> n instanceof TypedNode).map(n -> (TypedNode)n).map(TypedNode::nodeType).orElse(null);
            }
        };
    }



    Stream<Pair<Name,Name>> evaluate(Key k);

    Name typeOf(Name element);
}
