package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.names.Name;
import no.hvl.past.util.ShouldNotHappenException;

import java.util.Optional;

/**
 * Composes two succinct edges with each other.
 *
 * <pre>
 *  (0)-[01]->(1)->[12]->(2)
 *
 *  induces:
 *
 *  (0)-[02 := 01;12]->(2)
 * </pre>
 *
 */
public class Compose implements GraphOperation {

    private static Compose instance;

    private Compose() {
    }

    @Override
    public Graph inputArity() {
        return Universe.CHAIN;
    }

    @Override
    public Graph overlapArity() {
        try {
            return new GraphBuilders()
                    .node(Universe.CHAIN_FST.getSource())
                    .node(Universe.CHAIN_SND.getTarget())
                    .graph(Name.identifier("CHAIN_OUTLINE"))
                    .getResult(Graph.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "overlapArity", graphError);
        }
    }

    @Override
    public GraphImpl outputArity() {
        return Universe.ARROW;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) {
        GraphBuilders builders = new GraphBuilders().importGraph(instance.domain());

        instance.allInstances(Universe.CHAIN_FST)
                .flatMap(t -> instance.allInstances(Universe.CHAIN_SND)
                        .filter(tt -> tt.getSource().equals(t.getTarget()))
                        .map(t::compose)
                        .filter(Optional::isPresent)
                        .map(Optional::get))
                .forEach(triple -> {
                    builders.edge(triple.getSource(), triple.getLabel(), triple.getTarget());
                    builders.map(triple.getLabel(), Universe.TRIANGLE_HYP.getLabel());
                });
        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(arity());
        builders.morphism(getName().appliedTo(instance.getName()));
        try {
            return builders.getResult(GraphMorphism.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "execute", graphError);
        }
    }


    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return false;
    } // TODO implement

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) {
        return instance;
    }


    public static Compose getInstance() {
        if (instance == null) {
            instance = new Compose();
        }
        return instance;
    }

    @Override
    public String nameAsString() {
            return "<compose>";
    }

    @Override
    public Graph arity() {
        return Universe.TRIANGLE;
    }
}
