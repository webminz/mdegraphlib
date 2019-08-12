package no.hvl.past.util;

import java.util.List;

public interface StateSpace<S> {

    List<S> step(S current);

    boolean isInfinite();

}
