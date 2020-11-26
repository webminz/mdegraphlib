package no.hvl.past.util;

import java.util.*;
import java.util.function.Function;

public interface SearchStrategy<S, A> {


    /**
     * Different search strategies.
     * If you are unfamiliar with search algorithms, stick to the following heuristics:
     *
     * If you have heuristics: use A_STAR!
     * If you have a finite state space, no heuristics and also costs do not matter:
     * use BREADTH_FIRST_GRAPH.
     * If you have no heuristics and a big or even infinite state space:
     * you may consider ITERATIVE_DEEPENING.
     *
     */
    enum Strategies {

        BREADTH_FIRST_TREE,

        BREADTH_FIRST_GRAPH,

        DEPTH_FIRST_TREE,

        DEPTH_FIRST_GRAPH,

        UNIFORM_COST,

        ITERATIVE_DEEPENING,

        GREEDY,

        A_STAR

        // In the future: iterative deepening A* and recursive breadth first search

    }

    /**
     * Informs about new available actions.
     */
    void newActions(S current, int currentStateCost, List<A> actions);

    /**
     * Provides the next action that should be performed according to this strategy
     * and the respective state in which the action should be performed.
     */
    Optional<Pair<S, A>> nextAction();


    static <S,A> SearchStrategy<S,A> breadthFirst() {
        return new SearchStrategy<S, A>() {
            private final Deque<Pair<S, A>> deque = new LinkedList<>();

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    deque.push(new Pair<>(current, action));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (deque.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(deque.pop());
            }

        };
    }

    static <S,A> SearchStrategy<S,A> depthFirst() {
        return new SearchStrategy<S, A>() {
            private final Stack<Pair<S, A>> stack = new Stack<>();

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    stack.push(new Pair<>(current, action));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (stack.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(stack.pop());
            }

        };
    }

    static <S,A> SearchStrategy<S,A> breadthFirstGraph(StateSpace<S, A> stateSpace) {
        return new SearchStrategy<S, A>() {
            private final Deque<Pair<S, A>> deque = new LinkedList<>();
            private final Set<S> alreadyVisited = new HashSet<>();
            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                this.alreadyVisited.add(current);
                for (A action : actions) {
                    deque.push(new Pair<>(current, action));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (deque.isEmpty()) {
                    return Optional.empty();
                }
                Pair<S, A> result = deque.pop();
                Optional<S> next = stateSpace.applyAction(result.getFirst(), result.getSecond());
                if (next.isPresent() && !alreadyVisited.contains(next.get())) {
                    return Optional.of(result);
                }
                return nextAction();
            }

        };
    }

    static <S,A> SearchStrategy<S,A> depthFirstGraph(StateSpace<S, A> stateSpace) {
        return new SearchStrategy<S, A>() {
            private final Stack<Pair<S, A>> stack = new Stack<>();
            private final Set<S> alreadyVisited = new HashSet<>();

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                this.alreadyVisited.add(current);
                for (A action : actions) {
                    stack.push(new Pair<>(current, action));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (stack.isEmpty()) {
                    return Optional.empty();
                }
                Pair<S, A> next = stack.pop();
                Optional<S> nextState = stateSpace.applyAction(next.getFirst(), next.getSecond());
                if (nextState.isPresent() && alreadyVisited.contains(nextState.get())) {
                    return Optional.of(next);
                }
                return Optional.of(stack.pop());
            }

        };
    }

    static <S,A> SearchStrategy<S,A> depthLimited(int depthLimit) {
        return new SearchStrategy<S, A>() {
            private final Stack<Pair<S, A>> stack = new Stack<>();
            private int currentDepth = 0;

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    stack.push(new Pair<>(current, action));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (stack.isEmpty()) {
                    return Optional.empty();
                }
                if (currentDepth < depthLimit) {
                    currentDepth++;
                    return Optional.of(stack.pop());
                }
                return Optional.empty();
            }


        };
    }

    static <S, A> SearchStrategy<S,A> uniformCost(Function<A, Integer> cost) {
        return new SearchStrategy<S, A>() {

            private SortedSet<Pair<Integer, Pair<S, A>>> frontier = new TreeSet<>((p1, p2) -> p1.getFirst().compareTo(p2.getFirst()));

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    frontier.add(new Pair<>(currentStateCost + cost.apply(action), new Pair<>(current, action)));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (frontier.isEmpty()) {
                    return Optional.empty();
                }
                Pair<Integer, Pair<S, A>> result = frontier.first();
                frontier.remove(result);
                return Optional.of(result.getSecond());
            }


        };
    }

    static <S, A> SearchStrategy<S, A> iterativeDeepening() {
        return new SearchStrategy<S, A>() {
            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                // TODO implement correctly
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                return Optional.empty(); // TODO implement correctly
            }


        };
    }


    static <S,A> SearchStrategy<S,A> depthLimited(Function<A, Integer> heuristics) {
        return new SearchStrategy<S, A>() {

            private SortedSet<Pair<Integer, Pair<S, A>>> frontier = new TreeSet<>((p1, p2) -> p1.getFirst().compareTo(p2.getFirst()));

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    frontier.add(new Pair<>(heuristics.apply(action), new Pair<>(current, action)));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (frontier.isEmpty()) {
                    return Optional.empty();
                }
                Pair<Integer, Pair<S, A>> result = frontier.first();
                frontier.remove(result);
                return Optional.of(result.getSecond());
            }


        };
    }

    static <S,A> SearchStrategy<S,A> aStar(Function<A, Integer> cost, Function<A, Integer> heuristics) {
        return new SearchStrategy<S, A>() {

            private List<Pair<Integer, Pair<S, A>>> frontier = new ArrayList<>();
            private Set<Pair<S, A>> explored = new HashSet<>();

            private void insert(S current, A action, int totalCost) {
                for (int i = 0;  i < frontier.size(); i++) {
                    if (totalCost < frontier.get(i).getFirst()) {
                        frontier.add(i, new Pair<>(totalCost, new Pair<>(current, action)));
                        return;
                    }
                }
                frontier.add(new Pair<>(totalCost, new Pair<>(current, action)));
            }

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    if (!explored.contains(new Pair<>(current, action))) {
                        insert(current, action, heuristics.apply(action) + currentStateCost + cost.apply(action));
                    }
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (frontier.isEmpty()) {
                    return Optional.empty();
                }
                Pair<Integer, Pair<S, A>> result = frontier.get(0);
                frontier.remove(0);
                explored.add(result.getSecond());
                return Optional.of(result.getSecond());
            }

        };
    }

    static <S,A> SearchStrategy<S,A> greedy(Function<A, Integer> heuristics) {
        return new SearchStrategy<S, A>() {

            private List<Pair<Integer, Pair<S, A>>> frontier = new ArrayList<>();

            private void insert(S current, A action, int totalCost) {
                for (int i = 0;  i < frontier.size(); i++) {
                    if (totalCost <= frontier.get(i).getFirst()) {
                        frontier.add(i, new Pair<>(totalCost, new Pair<>(current, action)));
                        return;
                    }
                }
                frontier.add(new Pair<>(totalCost, new Pair<>(current, action)));
            }

            @Override
            public void newActions(S current, int currentStateCost, List<A> actions) {
                for (A action : actions) {
                    insert(current, action, heuristics.apply(action));
                }
            }

            @Override
            public Optional<Pair<S, A>> nextAction() {
                if (frontier.isEmpty()) {
                    return Optional.empty();
                }
                Pair<Integer, Pair<S, A>> result = frontier.get(0);
                frontier.remove(0);
                return Optional.of(result.getSecond());
            }

        };
    }


}
