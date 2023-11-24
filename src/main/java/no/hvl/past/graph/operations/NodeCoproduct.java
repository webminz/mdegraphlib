package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

public class NodeCoproduct implements GraphOperation {

    private static final Graph INPUT_AR = new GraphImpl(Name.identifier("PAIR_1"), Sets.newHashSet(
            Triple.node(Name.identifier("1")),
            Triple.node(Name.identifier("2"))
    ));

    private static  NodeCoproduct instance;

    private NodeCoproduct() {
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
        return Universe.COSPAN;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);


        instance.allNodeInstances(Universe.COSPAN_LEFT_LEG.getSource())
                .forEach(node -> {
                    Name to = node.copied().addSuffix(Name.identifier("1"));
                    b.node(to);
                    b.edge(node, node.injectedFrom(Name.identifier("1")), to);
                    b.map(node.injectedFrom(Name.identifier("1")), Universe.COSPAN_LEFT_LEG.getLabel());
                    b.map(to, Universe.COSPAN_RIGHT_LEG.getTarget());
                });
        instance.allNodeInstances(Universe.COSPAN_RIGHT_LEG.getSource())
                .forEach(node -> {
                    Name to = node.copied().addSuffix(Name.identifier("2"));
                    b.node(to);
                    b.edge(node, node.injectedFrom(Name.identifier("2")), to);
                    b.map(node.injectedFrom(Name.identifier("2")), Universe.COSPAN_RIGHT_LEG.getLabel());
                    b.map(to, Universe.COSPAN_RIGHT_LEG.getTarget());
                });



        b.graph(getName().appliedTo(instance.domain().getName()));
        b.codomain(Universe.COSPAN);
        b.morphism(getName().appliedTo(instance.getName()));

        return b.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        long expectedSize = instance.allNodeInstances(Universe.COSPAN_LEFT_LEG.getSource()).count() +
                instance.allNodeInstances(Universe.COSPAN_RIGHT_LEG.getSource()).count();
        return instance.allNodeInstances(Universe.COSPAN_RIGHT_LEG.getTarget()).count() == expectedSize &&
                instance.allNodeInstances(Universe.COSPAN_LEFT_LEG.getSource()).count() == instance.allInstances(Universe.COSPAN_LEFT_LEG).count() &&
                instance.allNodeInstances(Universe.COSPAN_RIGHT_LEG.getSource()).count() == instance.allInstances(Universe.COSPAN_RIGHT_LEG).count();
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {

        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);


        instance.allInstances(Universe.COSPAN_LEFT_LEG)
                .forEach(b::undoEdge);
        instance.allInstances(Universe.COSPAN_RIGHT_LEG)
                .forEach(b::undoEdge);
        instance.allNodeInstances(Universe.COSPAN_RIGHT_LEG.getTarget())
                .forEach(b::undoNode);

        b.graph(instance.domain().getName());
        b.codomain(Universe.COSPAN);
        b.morphism(instance.getName());

        return b.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[coproduct]>";
    }

    @Override
    public Graph arity() {
        return Universe.COSPAN;
    }

    public static NodeCoproduct getInstance() {
        if (instance == null) {
            instance = new NodeCoproduct();
        }
        return instance;
    }
}
