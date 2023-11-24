package no.hvl.past.graph.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

/**
 * Computes the transitive close of a loop edge.
 */
public class ReflexiveClosure implements GraphOperation {

    private static ReflexiveClosure instance;

    private ReflexiveClosure() {
    }

    @Override
    public Graph outputArity() {
        return Universe.LOOP;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);
        instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .map(n -> Triple.edge(n.getLabel(), getName().appliedTo(n.getLabel()), n.getLabel()))
                .forEach(edge -> {
                    b.edge(edge.getSource(), edge.getLabel(), edge.getTarget());
                    b.map(edge.getLabel(), Universe.LOOP_THE_LOOP.getLabel());
                });
        b.graph(getName().appliedTo(instance.domain().getName()));
        b.codomain(Universe.LOOP);
        b.morphism(getName().appliedTo(instance.getName()));
        return b.getResult(GraphMorphism.class);
    }


    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.LOOP_THE_LOOP.getSource())
                .allMatch(node -> instance.allOutgoingInstances(Universe.LOOP_THE_LOOP, node).findFirst().map(t -> t.getTarget().equals(node)).orElse(false));
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);
        instance.allInstances(Universe.LOOP_THE_LOOP)
                .forEach(b::undoEdge);
        b.graph(instance.domain().getName());
        b.codomain(Universe.LOOP);
        b.morphism(instance.getName());
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[reflexiveClosure]>";
    }

    @Override
    public Graph inputArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph overlapArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph arity() {
        return Universe.LOOP;
    }

    public static ReflexiveClosure getInstance() {
        if (instance == null) {
            instance = new ReflexiveClosure();
        }
        return instance;
    }
}
