package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.attributes.DataOperation;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.Optional;

/**
 * Evaluates a binary base type operation on two attributes.
 * Expects that the instance of the two input attribute edges
 * have the multiplicity one.
 */
public class EvaluateBinaryAttributeOperation implements GraphOperation {

    private static final Graph OUTPUT_ARITY = new GraphImpl(Name.identifier("ARROW_3"), Sets.newHashSet(
            Triple.node(Universe.SPAN_3_EDGE.getSource()),
            Triple.node(Universe.SPAN_3_EDGE.getTarget()),
            Universe.SPAN_3_EDGE
    ));

    private final DataOperation operation;

    private EvaluateBinaryAttributeOperation(DataOperation operation) {
        this.operation = operation;
    }

    @Override
    public Graph outputArity() {
        return OUTPUT_ARITY;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true);
        builders.importMorphism(instance);

        instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getSource())
                .forEach(node -> {
                    Value[] arguments = new Value[2];
                    arguments[0] = instance.allOutgoingInstances(Universe.SPAN_LEFT_LEG, node)
                            .findFirst()
                            .map(Triple::getTarget)
                            .filter(t -> t instanceof Value)
                            .map(t -> (Value)t)
                            .orElse(ErrorValue.INSTANCE);
                    arguments[1] = instance.allOutgoingInstances(Universe.SPAN_RIGHT_LEG, node)
                            .findFirst()
                            .map(Triple::getTarget)
                            .filter(t -> t instanceof Value)
                            .map(t -> (Value)t)
                            .orElse(ErrorValue.INSTANCE);
                    Value result = operation.apply(arguments);
                    if (!result.equals(ErrorValue.INSTANCE)) {
                        builders.edge(node, getName().appliedTo(node), result);
                        builders.map(result, Universe.multiSpanEdge(3).getTarget());
                        builders.map(getName().appliedTo(node), Universe.multiSpanEdge(3).getLabel());
                    }
                });

        builders.graph(getName().appliedTo(instance.getName()));
        builders.codomain(arity());
        builders.morphism(getName().appliedTo(instance.getName()));

        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getSource())
                .allMatch(node -> {
                    Value[] arguments = new Value[2];
                    arguments[0] = instance.allOutgoingInstances(Universe.SPAN_LEFT_LEG, node)
                            .findFirst()
                            .map(Triple::getTarget)
                            .filter(t -> t instanceof Value)
                            .map(t -> (Value) t)
                            .orElse(ErrorValue.INSTANCE);
                    arguments[1] = instance.allOutgoingInstances(Universe.SPAN_RIGHT_LEG, node)
                            .findFirst()
                            .map(Triple::getTarget)
                            .filter(t -> t instanceof Value)
                            .map(t -> (Value) t)
                            .orElse(ErrorValue.INSTANCE);
                    Value result = operation.apply(arguments);
                    if (!result.equals(ErrorValue.INSTANCE)) {
                        Optional<Triple> first = instance.allOutgoingInstances(Universe.multiSpanEdge(3), node).findFirst();
                        return first.isPresent() && first.get().getTarget().equals(result);
                    } else{
                        return true;
                    }
                });

    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true);
        builders.importMorphism(instance);

        instance.allInstances(Universe.multiSpanEdge(3))
                .forEach(builders::undoEdge);
        builders.graph(instance.domain().getName());
        builders.codomain(arity());
        builders.morphism(instance.getName());
        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[eval("+ operation.name() + ")]>";
    }

    @Override
    public Graph inputArity() {
        return Universe.SPAN;
    }

    @Override
    public Graph overlapArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph arity() {
        return Universe.SPAN_3;
    }

    public static EvaluateBinaryAttributeOperation getInstance(DataOperation operation) {
        return new EvaluateBinaryAttributeOperation(operation);
    }
}
