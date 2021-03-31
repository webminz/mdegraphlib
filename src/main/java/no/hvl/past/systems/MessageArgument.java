package no.hvl.past.systems;

import no.hvl.past.graph.Diagram;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;

import java.util.Optional;

public class MessageArgument implements Diagram {

    private final MessageType owner;
    private final Name argumentLabel;
    private final Name targetType;
    private final int order;
    private final boolean isOutput;

    public MessageArgument(MessageType owner, Name argumentLabel, Name targetType, int order, boolean isOutput) {
        this.owner = owner;
        this.argumentLabel = argumentLabel;
        this.targetType = targetType;
        this.order = order;
        this.isOutput = isOutput;
    }

    public MessageType message() {
        return owner;
    }

    public Triple asEdge() {
        return Triple.edge(owner.typeName(), argumentLabel, targetType);
    }

    public Name type() {
        return targetType;
    }

    @Override
    public Formula<Graph> label() {
        return Formula.top();
    }

    @Override
    public GraphMorphism binding() {
        return new GraphMorphism() {
            @Override
            public Name getName() {
                return MessageArgument.this.getName();
            }

            @Override
            public Graph domain() {
                return Universe.ARROW;
            }

            @Override
            public Graph codomain() {
                return owner.carrier();
            }

            @Override
            public Optional<Name> map(Name name) {
                if (name.equals(Universe.ARROW_SRC_NAME)) {
                    return Optional.of(owner.typeName());
                }
                if (name.equals(Universe.ARROW_LBL_NAME)) {
                    return Optional.of(argumentLabel);
                }
                if (name.equals(Universe.ARROW_TRG_NAME)) {
                    return Optional.of(type());
                }
                return Optional.empty();
            }
        };
    }

    @Override
    public Name getName() {
        return isOutput ? Name.identifier("OUT_" + order).appliedTo(argumentLabel) : Name.identifier("IN_" + order).appliedTo(argumentLabel);
    }

    public boolean isInput() {
        return !isOutput;
    }

    public boolean isOutput() {
        return isOutput;
    }

    public int argumentOrder() {
        return order;
    }


    @Override
    public Diagram substitue(GraphMorphism morphism) {
        return new MessageArgument(((MessageType)message().substitue(morphism)), morphism.map(argumentLabel).get(), morphism.map(targetType).get(), order, isOutput);
    }

    public MessageArgument substitue(GraphMorphism morphism, MessageType substitutedParent) {
        return new MessageArgument(substitutedParent, morphism.map(argumentLabel).get(), morphism.map(targetType).get(), order, isOutput);

    }
}
