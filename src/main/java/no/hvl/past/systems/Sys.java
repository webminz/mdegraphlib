package no.hvl.past.systems;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.graph.operations.Invert;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.graph.trees.TreeBuildStrategy;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import no.hvl.past.util.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
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
        private Set<MessageType> messages = new LinkedHashSet<>();

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
            return new Impl(url, sketch.restrict(this.messages), displayNames);
        }
    }


    class Impl implements Sys {

        private final String url;
        private final Sketch sketch;
        private final Set<Name> abstractTypes;
        private final Set<Name> singletonTypes;
        private final Set<Name> stringTypes;
        private final Set<Name> intTypes;
        private final Set<Name> floatTypes;
        private final Set<Name> boolTypes;
        private final Set<Name> otherDataTypes;
        private final Map<Name, Set<Name>> enumTypes;
        private final Set<MessageType> messages;
        private final Map<Name, String> displayNames;
        private final Map<Triple, Pair<Integer, Integer>> targetMultiplicities;
        private final Map<Triple, Pair<Integer, Integer>> sourceMuttiplicities;
        private final Set<Triple> orderedTriples;
        private final Set<Triple> uniqueTriples;
        private final Set<Triple> acyclicTriples;


        public Impl(String url, Sketch sketch, Map<Name, String> displayNames) {
            this.url = url;
            this.sketch = sketch;
            this.displayNames = displayNames;

            this.abstractTypes = new HashSet<>();
            this.singletonTypes = new HashSet<>();
            this.messages = new HashSet<>();
            this.stringTypes = new HashSet<>();
            this.intTypes = new HashSet<>();
            this.floatTypes = new HashSet<>();
            this.boolTypes = new HashSet<>();
            this.otherDataTypes = new HashSet<>();
            this.enumTypes = new HashMap<>();
            this.targetMultiplicities = new HashMap<>();
            this.sourceMuttiplicities = new HashMap<>();
            this.orderedTriples = new HashSet<>();
            this.uniqueTriples = new HashSet<>();
            this.acyclicTriples = new HashSet<>();

            updateCaches();
        }

        private void updateCaches() {
            this.sketch.diagrams().forEach(diagram -> {
                if (AbstractType.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.abstractTypes::add);
                } else if (Singleton.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.singletonTypes::add);
                } if (StringDT.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.stringTypes::add);
                } else if (IntDT.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.intTypes::add);
                } else if (FloatDT.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.floatTypes::add);
                } else if (BoolDT.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.boolTypes::add);
                } else if (EnumValue.class.isAssignableFrom(diagram.label().getClass())) {
                    EnumValue en = (EnumValue) diagram.label();
                    diagram.nodeBinding().ifPresent(n-> enumTypes.put(n, en.literals()));
                } else if (DataTypePredicate.getInstance().diagramIsOfType(diagram)) {
                    diagram.nodeBinding().ifPresent(this.otherDataTypes::add);
                } else if (TargetMultiplicity.class.isAssignableFrom(diagram.label().getClass())) {
                    TargetMultiplicity multiplicity = (TargetMultiplicity) diagram.label();
                    diagram.edgeBinding().ifPresent(t -> this.targetMultiplicities.put(t, new Pair<>(multiplicity.getLowerBound(), multiplicity.getUpperBound())));
                } else if (SourceMultiplicity.class.isAssignableFrom(diagram.label().getClass())) {
                    SourceMultiplicity multiplicity = (SourceMultiplicity) diagram.label();
                    diagram.edgeBinding().ifPresent(t -> this.sourceMuttiplicities.put(t, new Pair<>(multiplicity.getLowerBound(), multiplicity.getUpperBound())));
                } else if (Ordered.getInstance().diagramIsOfType(diagram)) {
                    diagram.edgeBinding().ifPresent(this.orderedTriples::add);
                } else if (Unique.getInstance().diagramIsOfType(diagram)) {
                    diagram.edgeBinding().ifPresent(this.uniqueTriples::add);
                } else if (diagram instanceof MessageType) {
                    messages.add((MessageType) diagram);
                }
            });
        }


        @Override
        public boolean isAbstract(Name typeName) {
            return this.abstractTypes.contains(typeName);
        }

        @Override
        public boolean isSingleton(Name typeName) {
            return this.singletonTypes.contains(typeName);
        }

        @Override
        public boolean isStringType(Name nodeType) {
            return this.stringTypes.contains(nodeType);
        }

        @Override
        public boolean isIntType(Name nodeType) {
            return this.intTypes.contains(nodeType);
        }

        @Override
        public boolean isBoolType(Name nodeType) {
            return this.boolTypes.contains(nodeType);
        }

        @Override
        public boolean isFloatType(Name nodeType) {
            return this.floatTypes.contains(nodeType);
        }

        @Override
        public boolean isEnumType(Name nodeType) {
            return this.enumTypes.containsKey(nodeType);
        }


        @Override
        public boolean isSimpleTypeNode(Name nodeName) {
            return isBoolType(nodeName) || isStringType(nodeName) || isIntType(nodeName) || isEnumType(nodeName) || isFloatType(nodeName) ||
                    this.otherDataTypes.contains(nodeName);
        }

        @Override
        public Pair<Integer, Integer> getSourceMultiplicity(Triple edge) {
            if (this.sourceMuttiplicities.containsKey(edge)) {
                return this.sourceMuttiplicities.get(edge);
            }
            return new Pair<>(0, -1);
        }

        @Override
        public Pair<Integer, Integer> getTargetMultiplicity(Triple edge) {
            if (this.targetMultiplicities.containsKey(edge)) {
                return this.targetMultiplicities.get(edge);
            }
            return new Pair<>(0, -1);
        }

        @Override
        public boolean isComposition(Triple edge) {
            return getSourceMultiplicity(edge).getRight() == 1 && this.acyclicTriples.contains(edge);
        }

        @Override
        public boolean isAggregation(Triple triple) {
            return Sys.super.isAggregation(triple);
        }

        @Override
        public boolean isOrdered(Triple edge) {
            return this.orderedTriples.contains(edge);
        }

        @Override
        public boolean isUnique(Triple edge) {
            return this.uniqueTriples.contains(edge);
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
            Optional<Triple> source = sketch.carrier().get(Name.identifier(path[0]));
            if (path.length == 1 && source.isPresent()) {
                return source;
            }
            if (path.length == 2 && source.isPresent()) {
                return sketch.carrier().edges()
                        .filter(t -> displayName(t.getLabel()).equals(path[1]))
                        .filter(t -> sketch.carrier().isInvariant(t.getSource(), source.get().getSource()))
                        .findFirst();
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

    default Stream<Name> types() {
        return this.schema().carrier().nodes().filter(n -> !this.isMessageType(n));
    }

    default Stream<Triple> links() {
        return schema().carrier().edges().filter(t -> !this.isAttributeType(t) && !isMessageType(t.getSource()));
    }

    default Stream<MessageType> messages() {
        return schema().diagrams().filter(diag -> diag instanceof MessageType).map(diagram -> (MessageType) diagram);
    }

    default boolean isSubtypeOf(Triple first, Triple second) {
        return first.equals(second);
    }

    default Stream<Tuple> directSuperTypes() {
        Graph graph = schema().carrier();
        if (graph instanceof InheritanceGraph) {
            InheritanceGraph igraph = (InheritanceGraph) graph;
            return igraph.directInheritances();
        } else {
            return Stream.empty();
        }
    }

    default Stream<Triple> attributeFeatures(Name owner) {
        return schema().carrier().outgoing(owner).filter(Triple::isEddge).filter(this::isAttributeType);
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

    default Pair<Integer, Integer> getTargetMultiplicity(Triple edge) {
        return this.schema().diagramsOn(edge)
                .filter(d -> TargetMultiplicity.class.isAssignableFrom(d.label().getClass()))
                .findFirst()
                .map(d -> (TargetMultiplicity)d.label())
                .map(mult -> new Pair<>(mult.getLowerBound(), mult.getUpperBound()))
                .orElse(new Pair<>(0, -1));
    }

    default Pair<Integer, Integer> getSourceMultiplicity(Triple edge) {
        return this.schema().diagramsOn(edge)
                .filter(d -> SourceMultiplicity.class.isAssignableFrom(d.label().getClass()))
                .findFirst()
                .map(d -> (SourceMultiplicity)d.label())
                .map(mult -> new Pair<>(mult.getLowerBound(), mult.getUpperBound()))
                .orElse(new Pair<>(0, -1));
    }

    default boolean isCollectionValued(Triple edge) {
        return !getTargetMultiplicity(edge).getRight().equals(1);
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

    default boolean isAbstract(Name typeName) {
        return this.schema().diagramsOn(Triple.node(typeName)).anyMatch(d -> AbstractType.getInstance().diagramIsOfType(d));
    }

    default boolean isSingleton(Name typeName) {
        return this.schema().diagramsOn(Triple.node(typeName)).anyMatch(d -> Singleton.getInstance().diagramIsOfType(d));
    }

    default boolean isMessageType(Name typeName) {
        return this.messages().anyMatch(m -> m.typeName().equals(typeName));
    }

    default Set<Name> enumLiterals(Name enumTypeName) {
        return this.schema().diagramsOn(Triple.node(enumTypeName))
                .filter(d -> EnumValue.class.isAssignableFrom(d.label().getClass()))
                .findFirst()
                .map(d -> {
                    EnumValue e = (EnumValue) d.label();
                    return e.literals();
                }).orElse(new HashSet<>());
    }

    default boolean isAggregation(Triple triple) {
        return !this.isComposition(triple) && this.schema().diagramsOn(triple).anyMatch(d -> Acyclicity.class.isAssignableFrom(d.label().getClass()));
    }

    default boolean isComposition(Triple edge) {
        return this.schema().diagramsOn(edge).anyMatch(d -> Acyclicity.class.isAssignableFrom(d.label().getClass())) &&
                this.schema().diagramsOn(edge).anyMatch(d -> d.label() instanceof SourceMultiplicity && ((SourceMultiplicity)d.label()).getUpperBound() == 1);
    }



}
