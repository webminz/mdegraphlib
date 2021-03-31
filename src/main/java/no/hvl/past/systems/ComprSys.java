package no.hvl.past.systems;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.Star;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.keys.Key;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.util.Pair;

import java.util.*;
import java.util.stream.Stream;

public interface ComprSys extends Sys {


    class Impl implements ComprSys {

        private final String url;
        private final Sketch comprehensiveSchema;
        private final Map<Name, String> namingMap;
        private final Map<Sys, GraphMorphism> systemsWithEmbeddings;
        private final Set<Name> mergedElements;
        private final Set<Key> keys;

        public Impl(
                String url,
                Sketch comprehensiveSchema,
                Map<Name, String> namingMap,
                Map<Sys, GraphMorphism> systemsWithEmbeddings,
                Set<Name> mergedElements,
                Set<Key> keys) {
            this.url = url;
            this.comprehensiveSchema = comprehensiveSchema;
            this.namingMap = namingMap;
            this.systemsWithEmbeddings = systemsWithEmbeddings;
            this.mergedElements = mergedElements;
            this.keys = keys;
        }

        @Override
        public Stream<Key> keys() {
            return keys.stream();
        }

        @Override
        public Stream<Sys> components() {
            return systemsWithEmbeddings.keySet().stream();
        }

        @Override
        public boolean isMerged(Name elementName) {
            return mergedElements.contains(elementName);
        }

        @Override
        public Stream<Name> localNames(Sys component, Name elementName) {
            GraphMorphism morphism = this.systemsWithEmbeddings.get(component);
            return comprehensiveSchema.carrier().get(elementName).map(t -> morphism.allInstances(t).map(Triple::getLabel)).orElse(Stream.empty());
        }

        @Override
        public String displayName(Name name) {
            return name.print(PrintingStrategy.IGNORE_PREFIX);
//            if (namingMap.containsKey(name)) {
//                return namingMap.get(name);
//            }
//            for (Sys sys : systemsWithEmbeddings.keySet()) {
//                String s = sys.displayName(name);
//                if (s != null && !s.isEmpty()) {
//                    return s;
//                }
//            }
//            return name.print(PrintingStrategy.IGNORE_PREFIX);
        }

        @Override
        public Optional<Triple> lookup(String... path) {
            if (path.length == 0) {
                return Optional.empty();
            }
            Optional<Sys> isSystemPrefix = systemsWithEmbeddings.keySet().stream().filter(sys -> sys.schema().getName().equals(Name.identifier(path[0]))).findFirst();
            if (isSystemPrefix.isPresent()) {
                String[] lookup = new String[path.length - 1];
                for (int i = 1; i < path.length; i++) {
                    lookup[i - 1] = path[i];
                }
                return isSystemPrefix.get().lookup(lookup);
            }
            Name prefixedName = Name.identifier(path[path.length - 1]);
            if (path.length > 1) {
                for (int i = path.length - 2; i >= 0; i--) {
                    prefixedName = prefixedName.prefixWith(Name.identifier(path[i]));
                }
            }
            return comprehensiveSchema.carrier().get(prefixedName);
        }

        @Override
        public Sketch schema() {
            return comprehensiveSchema;
        }

        @Override
        public String url() {
            return url;
        }
    }

    Stream<Sys> components();

    boolean isMerged(Name elementName);

    Stream<Name> localNames(Sys component, Name elementName);

    default Stream<Key> keys() {
        return schema().diagrams().filter(diagram -> diagram instanceof Key).map(diagram -> (Key) diagram);
    }

    default Stream<MessageType> globalMessages() {
        return messages().filter(msg -> isMerged(msg.typeName()));
    }

}
