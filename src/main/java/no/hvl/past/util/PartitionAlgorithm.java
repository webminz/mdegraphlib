package no.hvl.past.util;

import java.util.*;

/**
 * An abstract implementation for a typical partition algorithms
 * as they are e.g. really common in colimit or coequalizer computations.
 * They are a concrete instance of a fixpoint algorithm.
 */
public class PartitionAlgorithm<X> {

    private final Map<X, Gather<X>> result;

    private static class Gather<X> {
        private final Set<X> elements;
        private Gather<X> parent;

        public Gather(X element) {
            this(Collections.singleton(element));
        }

        public Gather(Set<X> elements) {
            this.elements = elements;
        }

        boolean isAtomic() {
            return elements.size() == 1;
        }

        private void addParent(Gather<X> parent) {
            this.parent = parent;
        }

        public void merge(Gather<X> with, X... addAlso) {
            Gather<X> lhs = this.getBiggestParent();
            Gather<X> rhs = with.getBiggestParent();
            Set<X> result = new HashSet<>();
            result.addAll(lhs.elements);
            result.addAll(rhs.elements);
            result.addAll(Arrays.asList(addAlso));
            Gather<X> newParent = new Gather<>(result);
            lhs.addParent(newParent);
            rhs.addParent(newParent);
        }

        public Gather<X> getBiggestParent() {
            if (parent == null) {
                return this;
            } else {
                return parent.getBiggestParent();
            }
        }

        public Set<X> allElements() {
            if (parent == null) {
                return elements;
            } else {
                return parent.allElements();
            }
        }
    }

    public PartitionAlgorithm(Collection<X> toCollect) {
        this.result = new HashMap<>();
        for (X x : toCollect) {
            this.result.put(x, new Gather<>(x));
        }
    }

    public PartitionAlgorithm relate(X left, X right, X... addAlso) {
        if (!left.equals(right)) {
            this.result.get(left).merge(this.result.get(right), addAlso);
        }
        return this;
    }


    public Set<Set<X>> getResult() {
        Set<Set<X>> result = new HashSet<>();
        for (X x : this.result.keySet()) {
            result.add(this.result.get(x).allElements());
        }
        return result;
    }




}
