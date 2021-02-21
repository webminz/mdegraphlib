package no.hvl.past.searching;


import no.hvl.past.searching.SearchStrategy;
import no.hvl.past.searching.StateSpace;
import no.hvl.past.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A search engine provides means to perform different kinds of search algorithms.
 * A search engine has to be provided with an environment, the state space (graph), which
 * is explored.
 * @param <S> The java class of states (nodes) in the state space.
 * @param <A> The java class of actions (edges) in the state space.
 */
public class SearchEngine<S, A> {

    private static class SearchTreeNode<S, A> {

        private final int cost; // to get here
        private final A action; // that lead here
        private final S state; // that we are in now
        private final SearchTreeNode<S, A> parent; // where we have been before, null if root = start state
        private final List<SearchTreeNode<S, A>> children;

        private SearchTreeNode(
                int cost,
                A action,
                S state,
                SearchTreeNode<S, A> parent) {
            this.cost = cost;
            this.action = action;
            this.state = state;
            this.parent = parent;
            this.children = new ArrayList<>();
        }





        List<A> getTrace() {
            if (action == null) {
                return new ArrayList<>();
            } else {
                if (parent == null) {
                    return new ArrayList<>(Collections.singleton(this.action));
                } else {
                    List<A> parentTrace = parent.getTrace();
                    parentTrace.add(action);
                    return parentTrace;
                }
            }
        }

        int getCost() {
            if (parent == null) {
                return this.cost;
            } else {
                return this.cost + parent.getCost();
            }
        }

        public Pair<SearchTreeNode<S,A>, Boolean> expand(S from, A action, S to, int actionCost) {
            if (this.state.equals(from)) {
                SearchTreeNode<S, A> result = new SearchTreeNode<>(actionCost, action, to, this);
                this.children.add(result);
                return new Pair<>(result, true);
            }
            for (SearchTreeNode<S, A> child : children) {
                Pair<SearchTreeNode<S, A>, Boolean> recursiveResult = child.expand(from, action, to, actionCost);
                if (recursiveResult.getSecond()) {
                    return recursiveResult;
                }
            }
            return new Pair<>(this, false);
        }
    }

    private final StateSpace<S,A> stateSpace;

    /**
     * Creates a search engine for the given state space.
     */
    public SearchEngine(StateSpace<S, A> stateSpace) {
        this.stateSpace = stateSpace;
    }


    public Optional<S> simpleSearch(S startSpace, Predicate<S> goal) {
        return searchForGoal(SearchStrategy.Strategies.BREADTH_FIRST_GRAPH, startSpace, goal, action -> 1, action -> 1);
    }

    /**
     * Searches and tries to reach the goal using the given strategy.
     * This methods is just interested in the goal and does not keep a trace
     * where it has been and what was its cost.
     */
    public Optional<S> searchForGoal(
            SearchStrategy.Strategies strategy,
            S startState, Predicate<S> goal,
            Function<A, Integer> cost,
            Function<A, Integer> heuristics) {
        if (goal.test(startState)) {
            return Optional.of(startState);
        }
        SearchStrategy<S, A> searchStrategy = createStrategy(stateSpace,strategy, cost, heuristics);
        S current = startState;
        int currentCost = 0;
        searchStrategy.newActions(current, currentCost, stateSpace.availableActions(current));
        while (true) {
            Optional<Pair<S, A>> nextAction = searchStrategy.nextAction();
            if (!nextAction.isPresent()) {
                return Optional.empty();
            }
            Optional<S> nextState = stateSpace.applyAction(nextAction.get().getFirst(), nextAction.get().getSecond());
            if (nextState.isPresent()) {
                current = nextState.get();
                if (goal.test(current)) {
                    return Optional.of(current);
                }
                currentCost = currentCost + cost.apply(nextAction.get().getSecond());
                searchStrategy.newActions(current, currentCost, stateSpace.availableActions(current));
            }
        }
    }

    /**
     * Searches and tries to reach the goal with the given strategy.
     * While searching it keeps a trace where it has been and what was to cost of this path.
     *
     */
    public Pair<List<A>, Integer> searchWithTrace(SearchStrategy.Strategies strategy,
                              S startState,
                              Predicate<S> goal,
                              Function<A, Integer> cost,
                              Function<A, Integer> heuristics) {
        // Maybe the startState is already the goal
        if (goal.test(startState)) {
            return new Pair<>(Collections.emptyList(), 0);
        }
        SearchTreeNode<S, A> root = new SearchTreeNode<>(0, null, startState, null);
        SearchStrategy<S, A> searchStrategy = createStrategy(stateSpace, strategy, cost, heuristics);
        searchStrategy.newActions(startState, 0, stateSpace.availableActions(startState));
        while (true) {
            Optional<Pair<S, A>> nextAction = searchStrategy.nextAction();
            if (!nextAction.isPresent()) {
                return new Pair<>(Collections.emptyList(), -1);
            }
            Optional<S> nextState = stateSpace.applyAction(nextAction.get().getFirst(), nextAction.get().getSecond());
            if (nextState.isPresent()) {
                Pair<SearchTreeNode<S, A>, Boolean> expansionResult = root.expand(nextAction.get().getFirst(), nextAction.get().getSecond(), nextState.get(), cost.apply(nextAction.get().getSecond()));
                if (expansionResult.getSecond()) {
                    if (goal.test(nextState.get())) {
                        return new Pair<>(expansionResult.getFirst().getTrace(),expansionResult.getFirst().getCost());
                    }
                    searchStrategy.newActions(nextState.get(), expansionResult.getFirst().getCost(), stateSpace.availableActions(nextState.get()));
                }
            }
        }
    }

    private SearchStrategy<S, A> createStrategy(
            StateSpace<S, A> stateSpace,
            SearchStrategy.Strategies strategy,
            Function<A, Integer> cost,
            Function<A, Integer> heuristics) {
        switch (strategy) {
            case BREADTH_FIRST_TREE:
                return SearchStrategy.breadthFirst();
            case DEPTH_FIRST_TREE:
                return SearchStrategy.depthFirst();
            case DEPTH_FIRST_GRAPH:
                return SearchStrategy.depthFirstGraph(stateSpace);
            case ITERATIVE_DEEPENING:
                return SearchStrategy.iterativeDeepening();
            case UNIFORM_COST:
                return SearchStrategy.uniformCost(cost);
            case GREEDY:
                return SearchStrategy.greedy(heuristics);
            case A_STAR:
                return SearchStrategy.aStar(cost, heuristics);
            case BREADTH_FIRST_GRAPH:
            default:
                return SearchStrategy.breadthFirstGraph(stateSpace); // breath first graph
        }

    }

}
