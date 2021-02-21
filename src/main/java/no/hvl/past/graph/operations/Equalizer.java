package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.stream.Collectors;

public class Equalizer implements GraphOperation {

    private static Equalizer instance;

    private static final Graph OUTPUT_AR = new GraphImpl(Name.identifier("ARROW_20"), Sets.newHashSet(
            Triple.node(Universe.EQUALIZER_MEDIATOR.getSource()),
            Triple.node(Universe.EQUALIZER_MEDIATOR.getTarget()),
            Universe.EQUALIZER_MEDIATOR
    ));

    private Equalizer() {
    }

    @Override
    public Graph inputArity() {
        return Universe.CELL;
    }

    @Override
    public Graph overlapArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph outputArity() {
        return OUTPUT_AR;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true).importMorphism(instance);

        instance.allNodeInstances(Universe.CELL_LHS.getSource())
                .filter(src ->
                        instance.allOutgoingInstances(Universe.CELL_LHS, src).map(Triple::getTarget).collect(Collectors.toSet()).equals(
                                instance.allOutgoingInstances(Universe.CELL_RHS, src).map(Triple::getTarget).collect(Collectors.toSet())
                        ))
                .forEach(trg -> {
                    builders.node(getName().appliedTo(trg));
                    builders.map(getName().appliedTo(trg), Universe.EQUALIZER_MEDIATOR.getSource());
                    builders.edge(getName().appliedTo(trg), trg.injectedFrom(getName().appliedTo(trg)), trg);
                    builders.map(trg.injectedFrom(getName().appliedTo(trg)), Universe.EQUALIZER_MEDIATOR.getLabel());
                });


        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(arity());
        builders.morphism(getName().appliedTo(instance.getName()));
        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.CELL_LHS.getSource())
                .filter(src ->
                        instance.allOutgoingInstances(Universe.CELL_LHS, src).map(Triple::getTarget).collect(Collectors.toSet()).equals(
                                instance.allOutgoingInstances(Universe.CELL_RHS, src).map(Triple::getTarget).collect(Collectors.toSet())
                        ))
                .allMatch(trg -> instance.allIncomingInstances(Universe.EQUALIZER_MEDIATOR, trg).count() == 1);
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true).importMorphism(instance);

        instance.allInstances(Universe.EQUALIZER_MEDIATOR)
                .forEach(edge -> {
                    builders.undoEdge(edge);
                    builders.undoNode(edge.getSource());
                });

        builders.graph(instance.domain().getName());
        builders.codomain(arity());
        builders.morphism(instance.getName());
        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[equalizer]>";
    }

    @Override
    public Graph arity() {
        return Universe.EQUALIZER_DIAGRAM;
    }

    public static Equalizer getInstance() {
        if (instance == null) {
            instance = new Equalizer();
        }
        return instance;
    }

}
