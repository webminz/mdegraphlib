package no.hvl.past.util;

import java.util.*;
import java.util.function.Predicate;

public class SearchEngine<S> {

    private static final int SEARCH_LIMIT = 1_000_000;

    private final StateSpace<S> stateSpace;

    private final SearchStrategy searchStrategy;

    public SearchEngine(StateSpace<S> stateSpace, SearchStrategy searchStrategy) {
        this.stateSpace = stateSpace;
        this.searchStrategy = searchStrategy;
    }

    public Optional<S> search(S startState, Predicate<S> criteria) {
        // Maybe the startState is already good
        if (criteria.test(startState)) {
            return Optional.of(startState);
        }

        List<S> queue = new ArrayList<>();
        queue.add(startState);

        if (this.stateSpace.isInfinite()) {
            return searchInfiniteStateSpace(criteria, queue);
        } else {
            return searchFiniteStateSpace(criteria, queue);
        }
    }

    private Optional<S> searchFiniteStateSpace(Predicate<S> criteria, List<S> queue) {
        Set<S> visited = new HashSet<>();
        boolean converged = false;

        while (!converged && !queue.isEmpty()) {
            S current = queue.get(0);
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            List<S> neighbours = this.stateSpace.step(current);
            converged = true;
            for (int i = 0; i < neighbours.size(); i++) {
                S neighbour = neighbours.get(i);
                if (criteria.test(neighbour)) {
                    return Optional.of(neighbour);
                } else {
                    if (!visited.contains(neighbour)) {
                        converged = false;
                        visited.add(neighbour);
                        switch (this.searchStrategy) {
                            case EXPLORATION:
                                queue.add(neighbour);
                                break;
                            case EXPLOITATION:
                                queue.add(i, neighbour);
                                break;
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }


    private Optional<S> searchInfiniteStateSpace(Predicate<S> criteria, List<S> queue) {
        Set<S> visited = new HashSet<>();

        while (visited.size() < SEARCH_LIMIT && !queue.isEmpty()) {
            S current = queue.get(0);
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
            List<S> neighbours = this.stateSpace.step(current);
            for (int i = 0; i < neighbours.size(); i++) {
                S neighbour = neighbours.get(i);
                if (criteria.test(neighbour)) {
                    return Optional.of(neighbour);
                } else {
                    if (!visited.contains(neighbour)) {
                        visited.add(neighbour);
                        switch (this.searchStrategy) {
                            case EXPLORATION:
                                queue.add(neighbour);
                                break;
                            case EXPLOITATION:
                                queue.add(i, neighbour);
                                break;
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
