package no.hvl.past.graph.predicates;

import no.hvl.past.attributes.DataOperation;
import no.hvl.past.graph.*;
import no.hvl.past.names.Name;
import no.hvl.past.names.Value;

/**
 * Holds if the given base type predicate on all attribute values in the instance fibre evaluates to `true'.
 * Thus, connecting the logic on base types with graphs.
 */
public class AttributePredicate implements GraphPredicate {

    private final DataOperation operation;

    /**
     * Expects an opperation with arity one and result type boolean, i.e. a predicate.
     */
    private AttributePredicate(DataOperation operation) {
        this.operation = operation;
    }

    @Override
    public String nameAsString() {
        return "[valuePredicate(" + operation.name() + ")]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ARROW;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ARROW_THE_ARROW)
                .allMatch(t -> {
                    if (t.getTarget() instanceof Value) {
                        Value[] args = new Value[1];
                        args[0] = (Value) t.getTarget();
                        Value result = operation.apply(args);
                        return result.equals(Name.trueValue());
                    }
                    return false;
                });
    }

    public static AttributePredicate getInstance(DataOperation operation) {
        return new AttributePredicate(operation);
    }



}
