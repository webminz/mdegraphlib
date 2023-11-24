package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.stream.Collectors;

public class NodeProduct implements GraphOperation {

    private static NodeProduct instance;

    private static final Graph INPUT_AR = new GraphImpl(Name.identifier("PAIR_1"), Sets.newHashSet(
            Triple.node(Name.identifier("1")),
            Triple.node(Name.identifier("2"))
    ));

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
        return Universe.SPAN;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);

        instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getTarget())
                .flatMap(l -> instance.allNodeInstances(Universe.SPAN_RIGHT_LEG.getTarget()).map(r -> new Pair<>(l,r)))
                .forEach(pair -> {
                    Name combined = pair.getLeft().pair(pair.getRight());
                    b.edge(combined, combined.projectionOn(Name.identifier("1")), pair.getLeft());
                    b.edge(combined, combined.projectionOn(Name.identifier("2")), pair.getRight());
                    b.node(combined);
                    b.map(combined, Universe.SPAN_LEFT_LEG.getSource());
                    b.map(combined.projectionOn(Name.identifier("1")), Universe.SPAN_LEFT_LEG.getLabel());
                    b.map(combined.projectionOn(Name.identifier("2")), Universe.SPAN_RIGHT_LEG.getLabel());
                });
        b.graph(getName().appliedTo(instance.domain().getName()));
        b.codomain(Universe.SPAN);
        b.morphism(getName().appliedTo(instance.getName()));
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        long expectedNoOfElements = instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getTarget()).count() * instance.allNodeInstances(Universe.SPAN_RIGHT_LEG.getTarget()).count();
        return instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getSource()).count() == expectedNoOfElements &&
                (expectedNoOfElements == 0 ||
                (instance.allInstances(Universe.SPAN_LEFT_LEG)
                .map(Triple::getTarget)
                .collect(Collectors.toSet())
                .containsAll(instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getTarget()).collect(Collectors.toSet()))
                && instance.allInstances(Universe.SPAN_RIGHT_LEG)
                .map(Triple::getTarget)
                .collect(Collectors.toSet())
                .containsAll(instance.allNodeInstances(Universe.SPAN_RIGHT_LEG.getTarget()).collect(Collectors.toSet()))));
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);

        instance.allInstances(Universe.SPAN_LEFT_LEG)
                .forEach(b::undoEdge);
        instance.allInstances(Universe.SPAN_RIGHT_LEG)
                .forEach(b::undoEdge);
        instance.allNodeInstances(Universe.SPAN_RIGHT_LEG.getSource())
                .forEach(b::undoNode);

        b.graph(instance.domain().getName());
        b.codomain(Universe.SPAN);
        b.morphism(instance.getName());
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[product]>";
    }

    @Override
    public Graph arity() {
        return Universe.SPAN;
    }

    public static NodeProduct getInstance() {
        if (instance == null) {
            instance = new NodeProduct();
        }
        return instance;
    }
}
