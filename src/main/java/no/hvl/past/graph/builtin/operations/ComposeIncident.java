package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.ShouldNotHappenException;

import java.util.Optional;

/**
 * Composes two edges which are adjacent (direction of the second edge) is reversed.
 *
 * <pre>
 *  (1)-[10]->(0)<-[20]-(2)
 *
 *  induces:
 *
 *  (1)-[12 := 10;20^-1]->(2)
 * </pre>
 *
 */
public class ComposeIncident implements GraphOperation {

    private static ComposeIncident instance;

    public static GraphOperation getInstance() {
        if (instance == null) {
            instance = new ComposeIncident();
        }
        return instance;
    }

    private ComposeIncident() {
    }

    @Override
    public GraphImpl inputArity() {
        return Universe.COSPAN;
    }

    @Override
    public Graph overlapArity() {
        try {
            return new GraphBuilders()
                    .node(Universe.COSPAN_LEFT_LEG.getSource())
                    .node(Universe.COSPAN_RIGHT_LEG.getSource())
                    .graph(Name.identifier("COSPAN_OUTLINE"))
                    .getResult(Graph.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "overlapArity", graphError);
        }
    }

    @Override
    public Graph outputArity() {
        return Universe.ARROW;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) {
        GraphBuilders builders = new GraphBuilders().importGraph(instance.domain());
        instance.allInstances(Universe.COSPAN_LEFT_LEG).flatMap(f ->
                instance.allInstances(Universe.COSPAN_RIGHT_LEG)
                        .map(Triple::inverse)
                        .filter(s -> s.getSource().equals(f.getTarget()))
                        .map(f::compose)
                        .filter(Optional::isPresent)
                        .map(Optional::get))
                .forEach(t -> {
                    builders.edge(t.getSource(), t.getLabel(), t.getTarget());
                    builders.map(t.getLabel(), Universe.INCIDENCE_TRIANGLE_HYP.getLabel());
                });
        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(Universe.INCIDENCE_TRIANGLE);
        builders.morphism(getName().appliedTo(instance.getName()));
        try {
            return builders.getResult(GraphMorphism.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "execute", graphError);
        }
    }


    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return false; // TODO implement
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) {
        return instance;
    }


    @Override
    public String nameAsString() {
        return "<composeIncident>";
    }

    @Override
    public Graph arity() {
        return Universe.INCIDENCE_TRIANGLE;
    }
}
