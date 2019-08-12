package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents a composite graph that is yielded by taking
 * the union of a set of graphs.
 */
public class GraphUnion implements AbstractGraph {

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


    private final List<AbstractGraph> members;

    private final Name name;

    public GraphUnion(List<AbstractGraph> members, Name name) {
        this.members = members;
        this.name = name;
    }

    @Override
    public boolean contains(Triple triple) {
        return this.members.stream().anyMatch(m -> m.contains(triple));
    }

    @Override
    public boolean contains(Name name) {
        return this.members.stream().anyMatch(m -> m.contains(name));
    }

    @Override
    public Set<Triple> outgoing(Name from) {
        return this.members.stream().flatMap(m -> m.outgoing(from).stream()).collect(Collectors.toSet());
    }

    @Override
    public Set<Triple> incoming(Name to) {
        return this.members.stream().flatMap(m -> m.incoming(to).stream()).collect(Collectors.toSet());
    }

    @Override
    public Iterator<Triple> iterator() {
        List<Iterator<Triple>> iterators = this.members.stream().map(AbstractGraph::iterator).collect(Collectors.toList());
        return new Iterator<Triple>() {

            private Iterator<Iterator<Triple>> i = iterators.iterator();
            private Iterator<Triple> current;

            @Override
            public boolean hasNext() {
                if (current == null) {
                    if (!i.hasNext()) {
                        return false;
                    }
                    current = i.next();
                }
                if (current.hasNext()) {
                    return true;
                }
                if (i.hasNext()) {
                    current = i.next();
                    return this.hasNext();
                }
                return false;
            }

            @Override
            public Triple next() {
                return current.next();
            }
        };
    }

    @Override
    public Spliterator<Triple> spliterator() {
        return new UnionSpliterator(this.members.stream().map(AbstractGraph::spliterator).collect(Collectors.toList()));
    }

    @Override
    public Name getName() {
        return name;
    }
}
