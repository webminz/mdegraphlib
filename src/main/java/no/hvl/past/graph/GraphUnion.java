package no.hvl.past.graph;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a composite graph that is yielded by taking
 * the union of a set of graphs.
 */
public class GraphUnion implements Graph {

    @Override
    public boolean isInfinite() {
        return members.stream().anyMatch(Graph::isInfinite);
    }

    private static class UnionSpliterator implements Spliterator<Triple> {

        private List<Spliterator<Triple>> memberSpliterators;
        private Iterator<Spliterator<Triple>> i;
        private Spliterator<Triple> current;

        UnionSpliterator(List<Spliterator<Triple>> memberSpliterators) {
            this.memberSpliterators = memberSpliterators;
            this.i = memberSpliterators.iterator();
        }

        @Override
        public boolean tryAdvance(Consumer<? super Triple> action) {
            if (current == null) {
                if (!i.hasNext()) {
                    return false;
                }
                current = i.next();
            }
            boolean wasSthmPresent = current.tryAdvance(action);
            if (wasSthmPresent) {
                return true;
            } else {
                current = null;
                return tryAdvance(action);
            }
        }

        @Override
        public Spliterator<Triple> trySplit() {
            if (memberSpliterators.size() < 2) {
                return null;
            }
            int mid = memberSpliterators.size() / 2;
            List<Spliterator<Triple>> first = new ArrayList<>();
            List<Spliterator<Triple>> second = new ArrayList<>();
            for (int i = 0; i < memberSpliterators.size(); i++) {
                if (i < mid) {
                    first.add(memberSpliterators.get(i));
                } else {
                    second.add(memberSpliterators.get(i));
                }
            }
            this.memberSpliterators = first;
            this.i = first.iterator();
            return new UnionSpliterator(second);
        }

        @Override
        public long estimateSize() {
            return this.memberSpliterators.stream().mapToLong(Spliterator::estimateSize).sum();
        }

        @Override
        public int characteristics() {
            return NONNULL | SIZED | SUBSIZED;
        }
    }

    private final List<Graph> members;
    private final Name name;

    public GraphUnion(List<Graph> members, Name name) {
        this.members = members;
        this.name = name;
    }

    @Override
    public Stream<Triple> elements() {
        if (members.isEmpty()) {
            return Stream.empty();
        }
        Iterator<Graph> i = this.members.iterator();
        Stream<Triple> result = i.next().prefix().elements();
        while (i.hasNext()) {
            result = Stream.concat(result, i.next().prefix().elements());
        }
        return result;
    }

    @Override
    public boolean contains(Triple triple) {
        return this.members.stream().anyMatch(m -> m.contains(triple.mapName(Name::unprefixTop)));
    }

    @Override
    public boolean mentions(Name name) {
        return this.members.stream().anyMatch(m -> m.mentions(name.unprefixTop()));
    }

    @Override
    public Stream<Triple> outgoing(Name from) {
        return this.members.stream().flatMap(m -> m.outgoing(from.unprefixTop()));
    }

    @Override
    public Stream<Triple> incoming(Name to) {
        return this.members.stream().flatMap(m -> m.incoming(to.unprefixTop()));
    }

    @Override
    public Optional<Triple> get(Name label) {
        for (Graph g : members) {
            Optional<Triple> triple = g.get(label);
            if (triple.isPresent()) {
                return triple;
            }
        }
        return Optional.empty();
    }


    public Optional<GraphMorphism> inclusionOf(Name graph) {
        return this.members.stream()
                .filter(g -> g.getName().equals(graph))
                .findFirst()
                .map(member -> new GraphMorphism() {
                        @Override
                        public Graph domain() {
                            return member;
                        }

                        @Override
                        public Graph codomain() {
                            return GraphUnion.this;
                        }

                        @Override
                        public Optional<Name> map(Name name) {
                            return Optional.of(name.prefixWith(member.getName()));
                        }

                        @Override
                        public Optional<Triple> apply(Triple from) {
                            return Optional.of(
                                    Triple.edge(
                                            from.getSource().prefixWith(member.getName()),
                                            from.getLabel().prefixWith(member.getName()),
                                            from.getTarget().prefixWith(member.getName())
                                    )
                            );
                        }

                        @Override
                        public Name getName() {
                            return member.getName().subTypeOf(GraphUnion.this.name);
                        }
                    });
    }

//    @Override
//    public Iterator<Triple> iterator() {
//        List<Iterator<Triple>> iterators = this.members.stream().map(Graph::iterator).collect(Collectors.toList());
//        return new Iterator<Triple>() {
//
//            private Iterator<Iterator<Triple>> i = iterators.iterator();
//            private Iterator<Triple> current;
//
//            @Override
//            public boolean hasNext() {
//                if (current == null) {
//                    if (!i.hasNext()) {
//                        return false;
//                    }
//                    current = i.next();
//                }
//                if (current.hasNext()) {
//                    return true;
//                }
//                if (i.hasNext()) {
//                    current = i.next();
//                    return this.hasNext();
//                }
//                return false;
//            }
//
//            @Override
//            public Triple next() {
//                return current.next();
//            }
//        };
//    }
//
//    @Override
//    public Spliterator<Triple> spliterator() {
//        return new UnionSpliterator(this.members.stream().map(Graph::spliterator).collect(Collectors.toList()));
//    }

    @Override
    public Name getName() {
        return name;
    }



}
