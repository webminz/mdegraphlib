package no.hvl.past.systems;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.trees.*;
import no.hvl.past.keys.Key;
import no.hvl.past.keys.KeyNotEvaluated;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
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


    void index(Multimap<Name, Key> keys, Commonalities commonalities);

    Name typeOf(Name element);

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
            public void index(Multimap<Name, Key> keys, Commonalities commonalities) {
                for (Name type : keys.keySet()) {
                    all(type).forEach(el -> {
                            Multimap<Name, Name> evalKeys = HashMultimap.create();
                            for (Key key : keys.get(type)) {
                                key.evaluate(el, instance).ifPresent(value -> evalKeys.put(key.targetType(), value));
                            }
                            for (Name commType : evalKeys.keySet()) {
                            for (Name commId : evalKeys.get(commType)) {
                                commonalities.put(commType, commId, el, Name.identifier(origin().url()));
                            }
                            if (evalKeys.get(commType).size() > 1) {
                                commonalities.notifyDoubleRelationship(commType, el, Name.identifier(origin().url()), new HashSet<>(evalKeys.get(commType)));
                            }
                        }
                    });
                }

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
            public void index(Multimap<Name, Key> keys, Commonalities commonalities) {
                Iterator<TypedNode> iterator = TreeIterator.depthFirstTypedComplex(tree.root());
                while (iterator.hasNext()) {
                    TypedNode typedNode = iterator.next();
                    if (!keys.get(typedNode.nodeType()).isEmpty()) {
                        Multimap<Name, Name> evalKeys = HashMultimap.create();
                        for (Key key : keys.get(typedNode.nodeType())) {
                            key.evaluate(typedNode, tree).ifPresent(value -> evalKeys.put(key.targetType(), value));
                        }
                        for (Name commType : evalKeys.keySet()) {
                            for (Name commId : evalKeys.get(commType)) {
                                commonalities.put(commType, commId, typedNode.elementName(), Name.identifier(origin().url()));
                            }
                            if (evalKeys.get(commType).size() > 1) {
                                commonalities.notifyDoubleRelationship(commType, typedNode.elementName(), Name.identifier(origin().url()), new HashSet<>(evalKeys.get(commType)));
                            }

                        }
                    }
                }
            }

            @Override
            public Name typeOf(Name element) {
                return tree.findNodeById(element).filter(n -> n instanceof TypedNode).map(n -> (TypedNode)n).map(TypedNode::nodeType).orElse(null);
            }
        };
    }




}
