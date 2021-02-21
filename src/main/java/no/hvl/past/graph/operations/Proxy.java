package no.hvl.past.graph.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

/**
 * Creates a proxy for a node, i.e. turns a single node to an edge (to a copy of its own).
 */
public class Proxy implements GraphOperation {

    private static Proxy instance;

    private Proxy() {
    }

    @Override
    public GraphImpl outputArity() {
        return Universe.ARROW;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true).importMorphism(instance);
        instance.allNodeInstances(Universe.ARROW_SRC_NAME)
                .map(node -> Triple.edge(
                        node,
                        getName().appliedTo(node).addSuffix(Universe.ARROW_LBL_NAME),
                        getName().appliedTo(node).addSuffix(Universe.ARROW_TRG_NAME))
                ).forEach(edge -> {
            b.node(edge.getTarget())
                    .edge(edge.getSource(), edge.getLabel(), edge.getTarget())
                    .map(edge.getLabel(), Universe.ARROW_LBL_NAME)
                    .map(edge.getTarget(), Universe.ARROW_TRG_NAME);
        });
        return  b.graph(getName().appliedTo(instance.domain().getName()))
        .codomain(Universe.ARROW)
        .morphism(getName().appliedTo(instance.getName()))
                .getResult(GraphMorphism.class);
    }


    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.ARROW_SRC_NAME)
                .allMatch(node -> instance.allOutgoingInstances(Universe.ARROW_THE_ARROW, node).count() == 1);
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) {
        return instance;
    }


    @Override
    public String nameAsString() {
        return "<proxy>";
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
        return Universe.ARROW;
    }

    public static Proxy getInstance() {
        if (instance == null) {
            instance = new Proxy();
        }
        return instance;
    }
}
