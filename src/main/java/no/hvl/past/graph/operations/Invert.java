package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

/**
 * Inverts an edge.
 */
public class Invert implements GraphOperation {

    private static final Graph OUTPUT = new GraphImpl(Name.identifier("ARROW").inverse(), Sets.newHashSet(
            Triple.node(Universe.CYCLE_BWD.getSource()),
            Triple.node(Universe.CYCLE_BWD.getTarget()),
            Universe.CYCLE_BWD
    ));

    private static Invert instance;

    private Invert() {
    }

    @Override
    public Graph outputArity() {
        return OUTPUT;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true).importMorphism(instance);
        instance.allInstances(Universe.CYCLE_FWD)
                .forEach(edge -> {
                                    b
                                    .edge(edge.getTarget(), edge.getLabel().inverse(), edge.getSource())
                                    .map(edge.getLabel().inverse(), Universe.CYCLE_BWD.getLabel());
                });
        b.graph(getName().appliedTo(instance.domain().getName()))
                .codomain(Universe.CYCLE)
                .morphism(getName().appliedTo(instance.getName()));
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance
                .allNodeInstances(Universe.CYCLE_FWD.getSource())
                .allMatch(node -> {
                    if (instance.allOutgoingInstances(Universe.CYCLE_FWD, node).count() == 0) {
                        return instance.allIncomingInstances(Universe.CYCLE_BWD, node).count() == 0;
                    } else {
                        return instance.allOutgoingInstances(Universe.CYCLE_FWD, node)
                                .allMatch(outEdge -> {
                                    return instance.allOutgoingInstances(Universe.CYCLE_BWD, outEdge.getTarget())
                                            .filter(inEdge -> inEdge.getTarget().equals(node))
                                            .count() != 0;

                                });
                    }
                });
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true).importMorphism(instance);
        instance.allInstances(Universe.CYCLE_BWD)
                .forEach(b::undoEdge);
        return b.graph(instance.domain().getName())
                .codomain(Universe.CYCLE)
                .morphism(instance.getName())
                .getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[invert]>";
    }

    @Override
    public Graph inputArity() {
        return Universe.ARROW;
    }

    @Override
    public Graph overlapArity() {
        return Universe.PAIR;
    }

    @Override
    public Graph arity() {
        return Universe.CYCLE;
    }

    public static Invert getInstance() {
        if (instance == null) {
            instance = new Invert();
        }
        return instance;
    }


}
