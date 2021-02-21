package no.hvl.past.logic;

import java.util.stream.Stream;

public interface Matcher<Sig extends Signature> {

    /**
     * Finds all possible variable assignments (i.e. models) in the given host graph.
     */
    Stream<Model<Sig>> allOccurrences(Sig variables, Model<Sig> host);

}
