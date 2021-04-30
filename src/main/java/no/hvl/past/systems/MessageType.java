package no.hvl.past.systems;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.logic.Formula;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import no.hvl.past.util.ShouldNotHappenException;

import java.util.*;
import java.util.stream.Stream;

public interface MessageType extends Diagram {

    class Builder {

        private final List<MessageArgument> arguments;
        private int inArgCounter = 0;
        private int outArgCounter = 0;
        private final Impl result;
        private final Sys.Builder parentBuilder;

        Builder(Graph carrier, Name type, Sys.Builder parentBuilder) {
            this.arguments = new ArrayList<>();
            this.result = new Impl(carrier, type, arguments);
            this.parentBuilder = parentBuilder;
        }

        public Builder(Graph carrier, Name type) {
            this.arguments = new ArrayList<>();
            this.result = new Impl(carrier, type, arguments);
            this.parentBuilder = null;
        }

        public Builder input(Name edgeLabel) throws GraphError {
            Optional<Triple> triple = result.carrier.get(edgeLabel);
            if (!triple.isPresent()) {
                throw new GraphError(Collections.singletonList(new Pair<>(edgeLabel, GraphError.ERROR_TYPE.UNKNOWN_MEMBER)));
            }
            this.arguments.add(new MessageArgument(result, edgeLabel, triple.get().getTarget(), inArgCounter++, false));
            return this;
        }

        public Builder output(Name edge) throws GraphError {
            Optional<Triple> triple = result.carrier.get(edge);
            if (!triple.isPresent()) {
                throw new GraphError(Collections.singletonList(new Pair<>(edge, GraphError.ERROR_TYPE.UNKNOWN_MEMBER)));
            }
            this.arguments.add(new MessageArgument(result, edge, triple.get().getTarget(), inArgCounter++, true));
            return this;
        }

        public MessageType build() {
            return result;
        }

        public Sys.Builder endMessage() {
            if (this.parentBuilder != null) {
                this.parentBuilder.addMessage(build());
            }
            return parentBuilder;
        }


    }


    class Impl implements MessageType {
        private final Graph carrier;
        private final Name typeName;
        private final Collection<MessageArgument> arguments;

        public Impl(Graph carrier, Name typeName, Collection<MessageArgument> arguments) {
            this.carrier = carrier;
            this.typeName = typeName;
            this.arguments = arguments;
        }

        @Override
        public Stream<MessageArgument> arguments() {
            return arguments.stream();
        }

        @Override
        public Graph carrier() {
            return carrier;
        }

        @Override
        public Name typeName() {
            return typeName;
        }
    }


    Stream<MessageArgument> arguments();

    default Stream<MessageArgument> inputs() {
        return arguments().filter(MessageArgument::isInput);
    };

    default Stream<MessageArgument> outputs() {
        return arguments().filter(MessageArgument::isOutput);
    }

    Graph carrier();

    Name typeName();

    @Override
    default Formula<Graph> label() {
        return Formula.top();
    }

    @Override
    default GraphMorphism binding() {
        return new GraphMorphism() {
            @Override
            public Graph domain() {
                return Universe.ONE_NODE;
            }

            @Override
            public Graph codomain() {
                return carrier();
            }

            @Override
            public Optional<Name> map(Name name) {
                if (name.equals(Universe.ONE_NODE_THE_NODE)) {
                    return Optional.of(typeName());
                }
                return Optional.empty();
            }

            @Override
            public Name getName() {
                return MessageType.this.getName().absolute();
            }
        };
    }

    @Override
    default Name getName() {
        return Name.identifier("Message");
    }


    @Override
    default MessageType substitue(GraphMorphism morphism) {
        return new MessageType() {
            @Override
            public Stream<MessageArgument> arguments() {
                return MessageType.this.arguments().map(arg -> (MessageArgument) arg.substitue(morphism, this));
            }

            @Override
            public Graph carrier() {
                return morphism.codomain();
            }

            @Override
            public Name typeName() {
                return morphism.map(MessageType.this.typeName()).get();
            }
        };
    }
}
