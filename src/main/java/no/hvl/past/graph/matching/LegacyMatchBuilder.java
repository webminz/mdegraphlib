package no.hvl.past.graph.matching;

import no.hvl.past.graph.GraphImpl;
import no.hvl.past.graph.GraphMorphismImpl;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.searching.Backtrack;
import no.hvl.past.util.Pair;

import java.util.*;
import java.util.function.Supplier;

class LegacyMatchBuilder implements Backtrack.BacktrackState<Pair<Triple, Triple>, GraphMorphismImpl> {

    private final Supplier<Name> nameGen;
    private final GraphImpl fromGraph;
    private final GraphImpl matchGraph;
    private final Map<Name, Name> binding;
    private final Set<Triple> unmatched;
    private final boolean bindInjectiveOnly;

    LegacyMatchBuilder(Supplier<Name> nameGen,
                       GraphImpl fromGraph,
                       GraphImpl matchGraph,
                       Map<Name, Name> binding,
                       Set<Triple> unmatched,
                       boolean bindInjectiveOnly) {
        this.nameGen = nameGen;
        this.fromGraph = fromGraph;
        this.matchGraph = matchGraph;
        this.binding = binding;
        this.unmatched = unmatched;
        this.bindInjectiveOnly = bindInjectiveOnly;
    }

    @Override
    public boolean canApply(Pair<Triple, Triple> choice) {
        if (!unmatched.contains(choice.getFirst())) {
            return false; // already matched or not existing
        }
        if (!matchGraph.contains(choice.getSecond())) {
            return false; // target binding is actually not valid
        }
        if (binding.containsKey(choice.getFirst().getLabel())) {
            return false; // edge already bound
        }
        if (bindInjectiveOnly && binding.values().contains(choice.getSecond().getLabel())) {
            return false; // matching is done injectively but target already bound
        }
        Name src = choice.getFirst().getSource();
        Name trg = choice.getFirst().getTarget();
        if (binding.containsKey(src) && !binding.get(src).equals(choice.getSecond().getSource())) {
            return false; // source node was already bound earlier but differently
        }
        if (binding.containsKey(trg) && !binding.get(trg).equals(choice.getSecond().getTarget())) {
            return false; // target node was already bound earlier but differently
        }

        return true; // All good then
    }

    @Override
    public Pair<Set<Pair<Triple, Triple>>, Backtrack.BacktrackState<Pair<Triple, Triple>, GraphMorphismImpl>> apply(Pair<Triple, Triple> choice) {
        Map<Name, Name> newBinding = new HashMap<>();
        newBinding.putAll(this.binding);
        newBinding.put(choice.getFirst().getSource(), choice.getSecond().getSource());
        newBinding.put(choice.getFirst().getLabel(), choice.getSecond().getLabel());
        newBinding.put(choice.getFirst().getTarget(), choice.getSecond().getTarget());
        Set<Triple> newUnmatched = new HashSet<>();
        newUnmatched.addAll(unmatched);
        newUnmatched.remove(choice.getFirst());
        newUnmatched.removeIf(t -> t.isNode() && newBinding.containsKey(t.getLabel()));
        Set<Pair<Triple, Triple>> newChoices = new HashSet<>();
        for (Triple t1 : newUnmatched) {
            if (t1.isEddge()) {
                for (Triple t2 : matchGraph.getEdges()) {
                    if (!bindInjectiveOnly || !binding.values().contains(t2.getLabel())) {
                        newChoices.add(new Pair<>(t1, t2));
                    }
                }
            }
        }
        if (newChoices.isEmpty()) {
            // we might have to check for isolated nodes. But we do this in the end
            for (Triple t1 : newUnmatched) {
                if (t1.isNode()) {
                    for (Name t2 : matchGraph.getNodes()) {
                        if (!bindInjectiveOnly || !binding.values().contains(t2)) {
                            newChoices.add(new Pair<>(t1, Triple.node(t2)));
                        }
                    }
                }
            }
        }
        LegacyMatchBuilder builder = new LegacyMatchBuilder(
                nameGen,
                fromGraph,
                matchGraph,
                newBinding,
                newUnmatched,
                bindInjectiveOnly
        );
        return new Pair<>(newChoices, builder);
    }

    @Override
    public boolean isDone() {
        return this.unmatched.isEmpty();
    }

    @Override
    public int hash() {
        return Objects.hash(unmatched, binding);
    }

    @Override
    public GraphMorphismImpl getResult() {
        return new GraphMorphismImpl(nameGen.get(), fromGraph, matchGraph, binding);
    }

    /**
     * Searches for all possible matches of the other graphs in this graph.
     */
    public Set<GraphMorphismImpl> findMatches(GraphImpl thisG, GraphImpl other, boolean injective) {
        Supplier<Name> nameGen = new Supplier<Name>() {

            @Override
            public Name get() {
                return Name.identifier("match#" + (counter++));
            }

            private int counter = 0;

        };
        LegacyMatchBuilder startState = new LegacyMatchBuilder(
                nameGen,
                thisG,
                other,
                new HashMap<>(),
                thisG.getElements(),
                injective
        );
        Set<Pair<Triple, Triple>> initialChoices = new HashSet<>();
        if (!thisG.getEdges().isEmpty()) {
            Triple first = thisG.getEdges().iterator().next();
            for (Triple otherTriple : other.getEdges()) {
                initialChoices.add(new Pair<>(first, otherTriple));
            }
            return new Backtrack<Pair<Triple, Triple>, GraphMorphismImpl>().backtrackAllResults(
                    startState,
                    initialChoices,
                    Collections.singleton(Backtrack.Strategies.STATE_HASHING)
            );
        } else if (!thisG.getNodes().isEmpty()) {
            Triple first = Triple.node(thisG.getNodes().iterator().next());
            for (Name otherName : other.getNodes()) {
                initialChoices.add(new Pair<>(first, Triple.node(otherName)));
            }
            return new Backtrack<Pair<Triple, Triple>, GraphMorphismImpl>().backtrackAllResults(
                    startState,
                    initialChoices,
                    Collections.singleton(Backtrack.Strategies.STATE_HASHING)
            );
        } else {
            return Collections.emptySet();
        }
    }

}
