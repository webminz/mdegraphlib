package no.hvl.past.systems;

import no.hvl.past.graph.Diagram;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.operations.Invert;
import no.hvl.past.graph.predicates.*;
import no.hvl.past.keys.Key;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Sys {


    class Builder {

        private final String url;
        private final Sketch sketch;
        private Set<MessageType> messages;

        void addMessage(MessageType msg) {
            this.messages.add(msg);
        }

        public Builder(String url, Sketch sketch) {
            this.url = url;
            this.sketch = sketch;
        }

        public MessageType.Builder beginMessage(Name msgType) {
            return new MessageType.Builder(sketch.carrier(), msgType, this);
        }

        public Sys build() {
            return new Impl(url, sketch);
        }
    }


    class Impl implements Sys {
        private final String url;
        private final Sketch sketch;
        private final Set<Name> baseTypes;
        private final Set<MessageType> messages;

        public Impl(String url, Sketch sketch) {
            this.url = url;
            this.sketch = sketch;
            this.baseTypes = sketch.diagrams()
                    .filter(d -> DataTypePredicate.getInstance().diagramIsOfType(d))
                    .map(Diagram::nodeBinding)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());
            this.messages = sketch.diagrams().filter(d -> d instanceof MessageType).map(d -> (MessageType)d).collect(Collectors.toSet());
        }

        @Override
        public boolean isSimpleTypeNode(Name nodeName) {
            return this.baseTypes.contains(nodeName);
        }

        @Override
        public String displayName(Name name) {
            return null;
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

    // TODO check if this can go into the query handler
    String displayName(Name name);

    Optional<Triple> lookup(String... path);

    Sketch schema();

    String url();

    default Stream<MessageType> messages() {
        return schema().diagrams().filter(diag -> diag instanceof MessageType).map(diagram -> (MessageType) diagram);
    }

    default boolean isSubtypeOf(Triple first, Triple second) {
        return first.equals(second);
    }

    default Stream<Triple> features(Name nodeName) {
        return schema().carrier().outgoing(nodeName).filter(Triple::isEddge);
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
                        // TODO make a method in Diagram
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
