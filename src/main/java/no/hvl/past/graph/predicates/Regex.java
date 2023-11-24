package no.hvl.past.graph.predicates;

import no.hvl.past.attributes.StringValue;
import no.hvl.past.graph.*;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.Variable;

import java.util.regex.Pattern;

/**
 * Holds if all elements in the instance fibre are string values or identifiers that match the given regex.
 */
public class Regex implements GraphPredicate {

    private final String regex;
    private final Pattern pattern;

    private Regex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

   @Override
    public String nameAsString() {
        return "[regex("+regex+")]";
    }

    @Override
    public GraphImpl arity() {
        return Universe.ONE_NODE;
    }

    @Override
    public boolean check(GraphMorphism instance) {
        return instance.allInstances(Universe.ONE_NODE_THE_NODE)
                .allMatch(t -> {
                    if (t.getLabel() instanceof StringValue) {
                        StringValue sv = (StringValue) t.getLabel();
                        return pattern.matcher(sv.getStringValue()).matches();
                    }
                    if (t.getLabel() instanceof Identifier) {
                        Identifier id = (Identifier) t.getLabel();
                        return pattern.matcher(id.toString()).matches();
                    }
                    if (t.getLabel() instanceof Variable) {
                        Variable var = (Variable) t.getLabel();
                        return pattern.matcher(var.toString()).matches();
                    }
                    return false;
                });
    }

    public static Regex getInstance(String pattern) {
        return new Regex(pattern);
    }

}
