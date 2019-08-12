package no.hvl.past.graph.techspace;

import java.util.HashMap;
import java.util.Map;

public class TechnologicalSpaceRegistry {

    private final Map<String, TechnologicalSpace> registry = new HashMap<>();

    private static TechnologicalSpaceRegistry instance;

    private TechnologicalSpaceRegistry() {
    }

    public static TechnologicalSpaceRegistry getInstance() {
        if (instance == null) {
            instance = new TechnologicalSpaceRegistry();
        }
        return instance;
    }

    public static void register() {
        // TODO implement via reflection
    }
}
