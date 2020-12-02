package no.hvl.past.graph.builtin.operations;

import no.hvl.past.graph.*;
import no.hvl.past.util.ShouldNotHappenException;

public class EvaluateBaseTypeOperation implements GraphOperation {
    private final String op;
    private final int arity;

    private EvaluateBaseTypeOperation(String op, int arity) {
        this.op = op;
        this.arity = arity;
    }

    @Override
    public GraphImpl outputArity() {
        return null; // TODO
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) {
        return null;
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return false;
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) {
        return instance;
    }


    @Override
    public String nameAsString() {
        return "<eval("+ op +")>";
    }

    @Override
    public GraphImpl inputArity() {
        return null; // TODO
    }

    @Override
    public Graph overlapArity() {
        try {
            return new GraphBuilders().node("0").node("1").graph("TWO_NODES").getResult(Graph.class);
        } catch (GraphError graphError) {
            throw new ShouldNotHappenException(getClass(), "overlapArity", graphError);
        }
    }

    @Override
    public Graph arity() {
        return null;
    }
}
