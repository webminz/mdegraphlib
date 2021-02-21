package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

public class CoproductUniversalProp implements GraphOperation {

    private static CoproductUniversalProp instance;


    private static final Graph OUTPUT_AR = new GraphImpl(Name.identifier("ARROW_30"), Sets.newHashSet(
            Triple.node(Universe.CoPRODUCT_MEDIATOR_EDGE.getSource()),
            Triple.node(Universe.CoPRODUCT_MEDIATOR_EDGE.getTarget()),
            Universe.CoPRODUCT_MEDIATOR_EDGE
    ));

    private static final Graph INPUT_AR = new GraphImpl(Name.identifier("TWO_COPRODUCT_CONES"),
            Sets.newHashSet(
                    Triple.node(Universe.COSPAN_LEFT_LEG.getTarget()),
                    Triple.node(Universe.COSPAN_LEFT_LEG.getSource()),
                    Triple.node(Universe.COSPAN_RIGHT_LEG.getSource()),
                    Triple.node(Universe.CoPRODUCT_MEDIATOR_EDGE.getTarget()),
                    Universe.COSPAN_LEFT_LEG,
                    Universe.COSPAN_RIGHT_LEG,
                    Universe.COPRODUCT_LEFT_COMPARATOR_EDGE,
                    Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE
            ));

    private CoproductUniversalProp() {
    }

    @Override
    public Graph inputArity() {
        return INPUT_AR;
    }

    @Override
    public Graph overlapArity() {
        return INPUT_AR;
    }

    @Override
    public Graph outputArity() {
        return OUTPUT_AR;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true).importMorphism(instance);

        instance.allInstances(Universe.COPRODUCT_LEFT_COMPARATOR_EDGE)
                .forEach(edge -> {
                    Optional<Name> src = instance.allOutgoingInstances(Universe.COSPAN_LEFT_LEG, edge.getSource()).map(Triple::getTarget).findFirst();
                    if (src.isPresent()) {
                        b.edge(src.get(), edge.getLabel().copied(), edge.getTarget());
                        b.map(edge.getLabel().copied(), Universe.CoPRODUCT_MEDIATOR_EDGE.getLabel());
                    }
                });

        instance.allInstances(Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE)
                .forEach(edge -> {
                    Optional<Name> src = instance.allOutgoingInstances(Universe.COSPAN_RIGHT_LEG, edge.getSource()).map(Triple::getTarget).findFirst();
                    if (src.isPresent()) {
                        b.edge(src.get(), edge.getLabel().copied(), edge.getTarget());
                        b.map(edge.getLabel().copied(), Universe.CoPRODUCT_MEDIATOR_EDGE.getLabel());
                    }
                });


        b.graph(getName().appliedTo(instance.domain().getName()));
        b.codomain(arity());
        b.morphism(getName().appliedTo(instance.getName()));
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allInstances(Universe.COPRODUCT_LEFT_COMPARATOR_EDGE)
                .allMatch(edge -> instance.allIncomingInstances(Universe.CoPRODUCT_MEDIATOR_EDGE, edge.getTarget())
                        .filter(mediator -> instance.allInstances(Universe.COSPAN_LEFT_LEG).anyMatch(ll -> ll.getSource().equals(edge.getSource()) && ll.getTarget().equals(mediator.getSource())))
                        .count() == 1)
                &&
                instance.allInstances(Universe.COPRODUCT_RIGHT_COMPARATOR_EDGE)
                        .allMatch(edge -> instance.allIncomingInstances(Universe.CoPRODUCT_MEDIATOR_EDGE, edge.getTarget())
                                .filter(mediator -> instance.allInstances(Universe.COSPAN_RIGHT_LEG).anyMatch(rl -> rl.getSource().equals(edge.getSource()) && rl.getTarget().equals(mediator.getSource())))
                                .count() == 1);
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true).importMorphism(instance);

        instance.allInstances(Universe.CoPRODUCT_MEDIATOR_EDGE)
                .forEach(b::undoEdge);

        b.graph(instance.domain().getName());
        b.codomain(arity());
        b.morphism(instance.getName());
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[case]>";
    }

    @Override
    public Graph arity() {
        return Universe.COPRODUCT_MEDIATOR_DIAGRAM;
    }

    public static CoproductUniversalProp getInstance() {
        if (instance == null) {
            instance = new CoproductUniversalProp();
        }
        return instance;
    }
}
