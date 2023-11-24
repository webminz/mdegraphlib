package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

public class ProductUniversalProp implements GraphOperation {

    private static final Graph OUTPUT_AR = new GraphImpl(Name.identifier("ARROW_02"), Sets.newHashSet(
            Triple.node(Universe.PRODUCT_MEDIATOR_EDGE.getSource()),
            Triple.node(Universe.PRODUCT_MEDIATOR_EDGE.getTarget()),
            Universe.PRODUCT_MEDIATOR_EDGE
    ));

    private static final Graph INPUT_AR = new GraphImpl(Name.identifier("TWO_PRODUCT_CONES"),
            Sets.newHashSet(
                    Triple.node(Universe.SPAN_LEFT_LEG.getSource()),
                    Triple.node(Universe.SPAN_LEFT_LEG.getTarget()),
                    Triple.node(Universe.SPAN_RIGHT_LEG.getTarget()),
                    Triple.node(Universe.PRODUCT_MEDIATOR_EDGE.getSource()),
                    Universe.SPAN_LEFT_LEG,
                    Universe.SPAN_RIGHT_LEG,
                    Universe.PRODUCT_LEFT_COMPARATOR_EDGE,
                    Universe.PRODUCT_RIGHT_COMPARATOR_EDGE
            ));

    private static ProductUniversalProp instance;

    private ProductUniversalProp() {
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
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);

        instance.allSrcCoincidentInstances(Universe.PRODUCT_LEFT_COMPARATOR_EDGE, Universe.PRODUCT_RIGHT_COMPARATOR_EDGE)
                .forEach(pair -> {
                                instance.allNodeInstances(Universe.PRODUCT_MEDIATOR_EDGE.getTarget())
                                        .filter(src -> instance.allInstances(Universe.SPAN_LEFT_LEG).anyMatch(ll -> ll.getTarget().equals(pair.getFirst().getTarget()) && ll.getSource().equals(src)) &&
                                                instance.allInstances(Universe.SPAN_RIGHT_LEG).anyMatch(rr -> rr.getTarget().equals(pair.getSecond().getTarget()) && rr.getSource().equals(src)))
                                        .forEach(node -> {
                                            builders.edge(pair.getFirst().getSource(), pair.getLeft().getLabel().pair(pair.getRight().getLabel()), node);
                                            builders.map(pair.getLeft().getLabel().pair(pair.getRight().getLabel()), Universe.PRODUCT_MEDIATOR_EDGE.getLabel());
                                        });
                            });

        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(arity());
        builders.morphism(getName().appliedTo(instance.getName()));

        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allSrcCoincidentInstances(Universe.PRODUCT_LEFT_COMPARATOR_EDGE, Universe.PRODUCT_RIGHT_COMPARATOR_EDGE)
                .allMatch(pair -> instance.allOutgoingInstances(Universe.PRODUCT_MEDIATOR_EDGE, pair.getFirst().getSource())
                        .filter(mediator -> instance.allInstances(Universe.SPAN_LEFT_LEG).anyMatch(ll -> ll.getTarget().equals(pair.getFirst().getTarget()) && ll.getSource().equals(mediator.getTarget()))
                                && instance.allInstances(Universe.SPAN_RIGHT_LEG).anyMatch(rr -> rr.getTarget().equals(pair.getSecond().getTarget()) && rr.getSource().equals(mediator.getTarget())))
                .count() == 1);
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);


        instance.allInstances(Universe.PRODUCT_MEDIATOR_EDGE)
                .forEach(builders::undoEdge);


        builders.graph(instance.domain().getName());
        builders.codomain(arity());
        builders.morphism(instance.getName());

        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[synchronize]>";
    }

    @Override
    public Graph arity() {
        return Universe.PRODUCT_MEDIATOR_DIAGRAM;
    }

    public static ProductUniversalProp getInstance() {
        if (instance == null) {
            instance = new ProductUniversalProp();
        }
        return instance;
    }
}
