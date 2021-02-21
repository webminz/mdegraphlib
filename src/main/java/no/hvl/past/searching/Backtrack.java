package no.hvl.past.searching;

import no.hvl.past.util.Pair;

import java.util.*;

/**
 * A generic implementation of a backtracking algorithm.
 * It is basically a search algorithm that tries different
 * choices, applying them if applicable and if necessary going back
 * to an earlier state if a taken choice was not successful.
 * @param <C> The type of Choices that the algorithm can make.
 * @param <R> The result type that the algorithm should produce in the end.
 */
public class Backtrack<C, R> {

    private final Set<R> results = new HashSet<>();

    private final List<BacktrackProcess> processes = new ArrayList<>();

    private final Set<Integer> visited = new HashSet<>();

    /**
     * Different strategies or heuristics that can be used to possibly speed up the process.
     */
    public enum Strategies {

        STATE_HASHING
    }

    /**
     * Encodes the respective domain specificness of a Backtrack run.
     * It checks whether a choice is reasonable and apply it.
     * @param <C> The type of choices.
     * @param <R> The type of result.
     */
    public interface BacktrackState<C, R> {

        /**
         * Returns true if the given choice is applicable in this state.
         */
        boolean canApply(C choice);

        /**
         * Applies a given choice.
         * The result is an updated state an a new set of choices that
         * are possible in this state.
         */
        Pair<Set<C>, BacktrackState<C, R>> apply(C choice);

        /**
         * Returns true if this state represents a successful terminated run,
         * i.e. there are no more choices to take and the result is available.
         */
        boolean isDone();

        /**
         * The number of visited states during a backtrack run can be really big
         * a state can provide a hash that indicates whether this state has already been visited.
         */
        int hash();

        /**
         * When this state is successfully terminated this method can be called to retrieve the result.
         */
        R getResult();

    }

    class BacktrackProcess {

        private final BacktrackState<C, R> state;

        private final Set<C> choices;

        public BacktrackProcess(BacktrackState<C, R> state, Set<C> choices) {
            this.state = state;
            this.choices = choices;
        }

        void perform(Set<Strategies> strategies) {
           // System.out.println(this.state);
            if (strategies.contains(Strategies.STATE_HASHING)) {
                visited.add(this.state.hash());
            }

            if (choices.isEmpty() || state.isDone()) {
                if (state.isDone()) {
                    results.add(state.getResult());
                }
                return;
            }
            for (C choice : choices) {
                if (state.canApply(choice)) {
                    Pair<Set<C>, BacktrackState<C, R>> apply = state.apply(choice);
                    processes.add(new BacktrackProcess(apply.getSecond(), apply.getFirst()));
                }
            }
        }

        public Integer hash() {
            return this.state.hash();
        }
    }

    public Set<R> backtrackAllResults(BacktrackState<C, R> startState, Set<C> startChoices, Set<Strategies> strategies) {
        processes.add(new BacktrackProcess(startState, startChoices));
        while (!this.processes.isEmpty()) {
            Set<BacktrackProcess> copy = new HashSet<>();
            copy.addAll(this.processes);
            this.processes.clear();
            for (BacktrackProcess p : copy) {
                if (strategies.contains(Strategies.STATE_HASHING) && this.visited.contains(p.hash())) {

                }
                p.perform(strategies);
            }
        }
        return results;
    }

    public Optional<R> backtrackFirstResult(BacktrackState<C, R> startState, Set<C> startChoices,  Set<Strategies> strategies) {
        processes.add(new BacktrackProcess(startState, startChoices));
        while (!this.processes.isEmpty()) {
            Set<BacktrackProcess> copy = new HashSet<>();
            copy.addAll(this.processes);
            this.processes.clear();
            for (BacktrackProcess p : processes) {
                p.perform(strategies);
                if (!results.isEmpty()) {
                    return Optional.of(results.iterator().next());
                }
            }
        }
        return Optional.empty();
    }



}
