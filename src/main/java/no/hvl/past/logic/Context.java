package no.hvl.past.logic;

import no.hvl.past.names.Name;
import no.hvl.past.names.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * The context for formula (often denoted by a big gamma).
 * It is basically a map with variable assignments.
 */
public class Context {

    private final Map<Variable, Name> typing;

    private final Map<Variable, Name> assignment;

    private Context(Map<Variable, Name> typing, Map<Variable, Name> assignment) {
        this.typing = typing;
        this.assignment = assignment;
    }


    public static Context nextEmpty() {
        return new Context(new HashMap<>(), new HashMap<>());
    }
}
