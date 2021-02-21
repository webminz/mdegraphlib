package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.attributes.DataOperation;
import no.hvl.past.attributes.ErrorValue;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Applies a multi-ary operation on an attribute.
 * Thus it interprets the instance-fibre of the input edge as an argument list (if your
 * operation is not commutative, you should therefore consider adding the ordered predicate).
 * The operation is evaluated for every node.
 *
 */
public class EvaluateMultiAryAttributeOperation implements GraphOperation {

    private static final Graph OUTPUT = new GraphImpl(Name.identifier("ARROW_2"), Sets.newHashSet(
            Triple.node(Universe.SPAN_RIGHT_LEG.getSource()),
            Triple.node(Universe.SPAN_RIGHT_LEG.getTarget()),
            Universe.SPAN_RIGHT_LEG
    ));

    private final DataOperation operation;

    private EvaluateMultiAryAttributeOperation(DataOperation operation) {
        this.operation = operation;
    }

    @Override
    public Graph inputArity() {
        return Universe.ARROW;
    }

    @Override
    public Graph overlapArity() {
        return Universe.ONE_NODE;
    }

    @Override
    public Graph outputArity() {
        return OUTPUT;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true)
                .importMorphism(instance);
        instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getSource())
                .forEach(node -> {
                    List<Value> argList = instance.allOutgoingInstances(Universe.SPAN_LEFT_LEG, node)
                            .sorted()
                            .map(Triple::getTarget)
                            .map(n -> n.isMultipart() ? n.secondPart() : n)
                            .filter(Name::isValue)
                            .map(t -> (Value)t)
                            .collect(Collectors.toList());

                    Value[] args = new Value[argList.size()];
                    Value result = operation.apply(argList.toArray(args));
                    if (!result.equals(ErrorValue.INSTANCE)) {
                        builders.node(result);
                        builders.edge(node, getName().appliedTo(node), result);
                        builders.map(getName().appliedTo(node), Universe.SPAN_RIGHT_LEG.getLabel());
                        builders.map(result, Universe.SPAN_RIGHT_LEG.getTarget());
                    }
                });

        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(Universe.SPAN);
        builders.morphism(getName().appliedTo(instance.getName()));

        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return  instance.allNodeInstances(Universe.SPAN_LEFT_LEG.getSource())
                .allMatch(node -> {
                    List<Value> argList = instance.allOutgoingInstances(Universe.SPAN_LEFT_LEG, node)
                            .sorted()
                            .map(Triple::getTarget)
                            .map(n -> n.isMultipart() ? n.secondPart() : n)
                            .filter(Name::isValue)
                            .map(t -> (Value)t)
                            .collect(Collectors.toList());
                    Value[] args = new Value[argList.size()];
                    Value result = operation.apply(argList.toArray(args));
                    if (!result.equals(ErrorValue.INSTANCE)) {
                        Optional<Triple> first = instance.allOutgoingInstances(Universe.SPAN_RIGHT_LEG, node).findFirst();
                        return first.isPresent() && first.get().getTarget().equals(result);
                    } else {
                        return true;
                    }
                });
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true);
        builders.importMorphism(instance);

        instance.allInstances(Universe.SPAN_RIGHT_LEG)
                .forEach(builders::undoEdge);
        builders.graph(instance.domain().getName());
        builders.codomain(Universe.ARROW);
        builders.morphism(instance.getName());
        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[eval(" + operation.name() + ")]>";
    }

    @Override
    public Graph arity() {
        return Universe.SPAN;
    }

    public static EvaluateMultiAryAttributeOperation getInstance(DataOperation operation) {
        return new EvaluateMultiAryAttributeOperation(operation);
    }
}
