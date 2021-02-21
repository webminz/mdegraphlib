package no.hvl.past.graph.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TransitiveClosure implements GraphOperation {

    private static TransitiveClosure instance;

    private TransitiveClosure() {
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
    public Graph outputArity() {
        return Universe.LOOP;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);
        Set<Triple> added = new HashSet<>();
        Set<Triple> all = new HashSet<>();
        instance.allInstances(Universe.LOOP_THE_LOOP)
                .forEach(all::add);
        do {
            added.clear();
            for (Triple e1 : all) {
                instance.allOutgoingInstances(Universe.LOOP_THE_LOOP, e1.getTarget())
                        .map(e1::compose)
                        .map(Optional::get)
                        .filter(t -> all.stream().noneMatch(tt -> t.getSource().equals(tt.getSource()) && t.getTarget().equals(tt.getTarget())))
                        .forEach(added::add);
            }
            for (Triple add : added) {
                all.add(add);
                b.edge(add.getSource(), add.getLabel(), add.getTarget());
                b.map(add.getLabel(), Universe.LOOP_THE_LOOP.getLabel());
            }
        } while (!added.isEmpty());

        b.graph(getName().appliedTo(instance.domain().getName()));
        b.codomain(Universe.LOOP);
        b.morphism(getName().appliedTo(instance.getName()));
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allInstances(Universe.LOOP_THE_LOOP)
                .flatMap(e1 -> instance.allInstances(Universe.LOOP_THE_LOOP)
                        .filter(e2 -> e1.getTarget().equals(e2.getSource()))
                        .map(e2 -> e1.compose(e2).get()))
                .allMatch(t -> instance.allOutgoingInstances(Universe.LOOP_THE_LOOP, t.getSource())
                        .filter(tt -> t.getTarget().equals(tt.getTarget()))
                        .count() == 1);
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders b = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);
        instance.allInstances(Universe.LOOP_THE_LOOP)
                .filter(t ->            instance
                                        .allOutgoingInstances(Universe.LOOP_THE_LOOP, t.getSource())
                                        .anyMatch(e1 -> instance
                                                .allOutgoingInstances(Universe.LOOP_THE_LOOP, e1.getTarget())
                                                .anyMatch(e2 -> e2.getTarget().equals(t.getTarget()))))
                .forEach(b::undoEdge);

        b.graph(instance.domain().getName());
        b.codomain(Universe.LOOP);
        b.morphism(instance.getName());
        return b.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[transitiveClosure]>";
    }

    @Override
    public Graph arity() {
        return Universe.LOOP;
    }

    public static TransitiveClosure getInstance() {
        if (instance == null) {
            instance = new TransitiveClosure();
        }
        return instance;
    }
}
