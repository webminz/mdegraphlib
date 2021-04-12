package no.hvl.past.systems;

import no.hvl.past.graph.Diagram;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.operations.Invert;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.graph.trees.TreeBuildStrategy;
import no.hvl.past.keys.Key;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A sys(tem) is a convenience wrapper on top of a {@link Sketch},
 * which adds some support message for the most common metamodel-query operations
 * that are known from popular Frameworks such as Ecore.
 *
 * Moreover, it explicitly adds the notion of messages (i.e. means to access and manipulate the data stored in a system).
 *
 */
public interface Sys {


    class Builder {

        private final String url;
        private final Sketch sketch;
        private final Map<Name, String> displayNames;
        private Set<MessageType> messages;

        void addMessage(MessageType msg) {
            this.messages.add(msg);
        }

        public Builder(String url, Sketch sketch) {
            this.url = url;
            this.sketch = sketch;
            this.displayNames = new HashMap<>();
        }

        public Builder displayName(Name formalName, String renderName) {
            this.displayNames.put(formalName, renderName);
            return this;
        }

        public MessageType.Builder beginMessage(Name msgType) {
            return new MessageType.Builder(sketch.carrier(), msgType, this);
        }

        public Sys build() {
            return new Impl(url, sketch, displayNames);
        }
    }


    class Impl implements Sys {
        private final String url;
        private final Sketch sketch;
        private final Set<Name> baseTypes;
        private final Set<MessageType> messages;
        private final Map<Name, String> displayNames;

        public Impl(String url, Sketch sketch, Map<Name, String> displayNames) {
            this.url = url;
            this.sketch = sketch;
            this.baseTypes = sketch.diagrams()
                    .filter(d -> DataTypePredicate.getInstance().diagramIsOfType(d))
                    .map(Diagram::nodeBinding)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            this.messages = sketch.diagrams().filter(d -> d instanceof MessageType).map(d -> (MessageType)d).collect(Collectors.toSet());
            this.displayNames = displayNames;
        }

        @Override
        public boolean isSimpleTypeNode(Name nodeName) {
            return this.baseTypes.contains(nodeName);
        }

        @Override
        public String displayName(Name name) {
            if (displayNames.containsKey(name)) {
                return this.displayNames.get(name);
            }
            return name.printRaw();
        }

        @Override
        public Optional<Triple> lookup(String... path) {
            if (path.length == 0) {
                return Optional.empty();
            }
            Name result = Name.identifier(path[path.length - 1]);
            if (path.length > 1) {
                for (int i = path.length - 2; i >= 0; i--) {
                    result = result.prefixWith(Name.identifier(path[i]));
                }
            }
            return sketch.carrier().get(result);
        }

        @Override
        public Sketch schema() {
            return sketch;
        }

        @Override
        public String url() {
            return url;
        }

        @Override
        public Stream<MessageType> messages() {
            return messages.stream();
        }
    }

    String displayName(Name name);

    Optional<Triple> lookup(String... path);

    Sketch schema();

    String url();


    default TreeBuildStrategy treeBuildStrategy() {
        return new TreeBuildStrategy.TypedStrategy() {

            @Override
            public Graph getSchemaGraph() {
                return schema().carrier();
            }

            @Override
            public Optional<Name> rootType(String label) {
                return lookup(label).map(Triple::getLabel);
            }

            @Override
            public Optional<Triple> lookupType(Name parentType, String field) {
                return features(parentType).filter(t -> t.getLabel().printRaw().equals(field)).findFirst();
            }

            @Override
            public boolean isStringType(Name typeName) {
                return Sys.this.isStringType(typeName);
            }

            @Override
            public boolean isBoolType(Name typeName) {
                return Sys.this.isBoolType(typeName);
            }

            @Override
            public boolean isFloatType(Name typeName) {
                return Sys.this.isFloatType(typeName);
            }

            @Override
            public boolean isIntType(Name typeName) {
                return Sys.this.isIntType(typeName);
            }

            @Override
            public boolean isEnumType(Name typeName) {
                return Sys.this.isEnumType(typeName);
            }
        };
    }

    default Stream<MessageType> messages() {
        return schema().diagrams().filter(diag -> diag instanceof MessageType).map(diagram -> (MessageType) diagram);
    }

    default boolean isSubtypeOf(Triple first, Triple second) {
        return first.equals(second);
    }

    default Stream<Triple> features(Name nodeName) {
        return schema().carrier().outgoing(nodeName).filter(Triple::isEddge);
    }

    default boolean isEnumType(Name nodeType) {
        return this.schema().diagramsOn(Triple.node(nodeType)).anyMatch(d -> EnumValue.getInstance().diagramIsOfType(d));
    }

    default boolean isBoolType(Name nodeType) {
        return this.schema().diagramsOn(Triple.node(nodeType)).anyMatch(d -> BoolDT.getInstance().diagramIsOfType(d));
    }

    default boolean isFloatType(Name nodeType) {
        return this.schema().diagramsOn(Triple.node(nodeType)).anyMatch(d -> FloatDT.getInstance().diagramIsOfType(d));
    }

    default boolean isIntType(Name nodeType) {
        return this.schema().diagramsOn(Triple.node(nodeType)).anyMatch(d -> IntDT.getInstance().diagramIsOfType(d));
    }

    default boolean isStringType(Name nodeType) {
        return this.schema().diagramsOn(Triple.node(nodeType)).anyMatch(d -> StringDT.getInstance().diagramIsOfType(d));
    }

    default boolean isSimpleTypeNode(Name nodeName) {
        return this.schema().diagramsOn(Triple.node(nodeName)).anyMatch(d -> DataTypePredicate.getInstance().diagramIsOfType(d));
    }

    default boolean isAttributeType(Triple edge) {
        return this.isSimpleTypeNode(edge.getTarget());
    }

    default Optional<Triple> getOppositeIfExists(Triple edge) {
        return this.schema().diagramsOn(edge).filter(d -> Invert.class.isAssignableFrom(d.label().getClass())).findFirst()
                .flatMap(d -> {
                    if (d.binding().map(Universe.CYCLE_FWD.getLabel()).map(n -> n.equals(edge.getLabel())).orElse(false)) {
                        return d.edgeBinding(Universe.CYCLE_BWD);
                    } else {
                        return d.edgeBinding(Universe.CYCLE_FWD);
                    }
                });
    }

    default boolean isCollectionValued(Triple edge) {
        return this.schema().diagramsOn(edge).noneMatch(d -> d instanceof TargetMultiplicity && ((TargetMultiplicity) d).getUpperBound() == 1);
    }

    default boolean hasTargetMultiplicity(Triple edge, int lowerBound, int upperBound) {
        return this.schema().diagramsOn(edge).anyMatch(d -> TargetMultiplicity.getInstance(lowerBound, upperBound).diagramIsOfType(d));
    }

    default boolean hasSourceMultiplicity(Triple edge, int lowerBound, int upperBound) {
        return this.schema().diagramsOn(edge).anyMatch(d -> SourceMultiplicity.getInstance(lowerBound, upperBound).diagramIsOfType(d));
    }

    default boolean isOrdered(Triple edge) {
        return this.schema().diagramsOn(edge).anyMatch(d -> Ordered.getInstance().diagramIsOfType(d));
    }

    default boolean isUnique(Triple edge) {
        return this.schema().diagramsOn(edge).anyMatch(d -> Unique.getInstance().diagramIsOfType(d));
    }




}
