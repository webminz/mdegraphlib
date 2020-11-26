package no.hvl.past.util;

import java.util.List;
import java.util.Optional;

/**
 * A state space is the basis for searching algorithms.
 * State spaces can be given by pretty much everything, e.g.
 * transitions in a system, road networks, etc.
 * @param <S> The java type that represents states in the state space.
 * @param <A> The java type that represents actions in the state space.
 */
public interface StateSpace<S, A> {

    /**
     * Provides a list with all the actions possible in the current state.
     */
    List<A> availableActions(S current);

    /**
     * Applies the action in the current state, possibly resulting in a new state.
     * If the action is not applicable in the current state it returns Optional.empty().
     */
    Optional<S> applyAction(S current, A action);

    /**
     * Returns true if the state space is infinite or effectively infinite.
     */
    boolean isInfinite();

}
