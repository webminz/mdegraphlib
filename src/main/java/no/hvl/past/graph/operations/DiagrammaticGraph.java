package no.hvl.past.graph.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.names.Name;

import java.util.*;

public class DiagrammaticGraph {

    public static class Builder {

        private final Graph base;

        private int predCounter = 0;
        private int opCounter = 0;

        private final List<Application> predicates;
        private final List<Application> operations;

        public Builder(Graph base) {
            this.base = base;
            this.predicates = new ArrayList<>();
            this.operations = new ArrayList<>();
        }

        public PredicateBuilder predicate(String predicateName) {
            Predicate predicate = Predefined.predefinedPredicates.get(predicateName); // TODO error handling -> Registry
            Name n = Name.identifier(predicateName + '#' + (++predCounter));
            return new PredicateBuilder(n, this, predicate);
        }

        public OperationBuilder operation(String operationName) {
            GraphOperation op = Predefined.predefinedOperations.get(operationName);
            Name n = Name.identifier(operationName + '#' + (++opCounter));
            return new OperationBuilder(n, this, op);
        }


        public DiagrammaticGraph build() {
            return new DiagrammaticGraph(base, this.predicates, this.operations);
        }

    }


    public static class PredicateBuilder {

        private final Name name;
        private final Builder parent;
        private final Predicate predicate;
        private final Set<Tuple> binding;

        private PredicateBuilder(Name name, Builder parent, Predicate predicate) {
            this.name = name;
            this.parent = parent;
            this.predicate = predicate;
            this.binding = new HashSet<>();
        }

        public PredicateBuilder bind(Name from, Name to) {
            this.binding.add(new Tuple(from, to));
            return this;
        }

        public Builder apply() {
            try {
                Morphism m = Morphism.create(name, predicate.arity(), parent.base, binding);
                Application app = new Application(predicate, m);
                parent.predicates.add(app);
            } catch (GraphError error) {
            }
            return parent;
        }
    }

    public static class OperationBuilder {

        private final Name name;
        private final Builder parent;
        private final GraphOperation extension;
        private final Set<Tuple> binding;

        private OperationBuilder(Name name, Builder parent, GraphOperation extension) {
            this.name = name;
            this.parent = parent;
            this.extension = extension;
            this.binding = new HashSet<>();
        }

        public OperationBuilder bind(Name from, Name to) {
            this.binding.add(new Tuple(from, to));
            return this;
        }

        public OperationBuilder bind(String from, String to) {
            this.binding.add(new Tuple(Name.identifier(from), Name.identifier(to)));
            return this;
        }

        public Builder apply() {
            try {
                Morphism m = Morphism.create(name, extension.arity(), parent.base, binding);
                Application app = new Application(extension, m);
                parent.operations.add(app);
            } catch (GraphError error) {
            }
            return parent;
        }
    }

    private final AbstractGraph graph;

    private final List<Application> predicateApplications;

    private final List<Application> extensionOperationApplications;

    public DiagrammaticGraph(AbstractGraph graph,
                             List<Application> predicateApplications,
                             List<Application> extensionOperationApplications) {
        this.graph = graph;
        this.predicateApplications = predicateApplications;
        this.extensionOperationApplications = extensionOperationApplications;
    }


    public Name getName() {
        return this.graph.getName();
    }

    public AbstractGraph effectiveGraph() {
        if (this.extensionOperationApplications.isEmpty()) {
            return this.graph;
        }
        GraphModification result = GraphModification.create(this.graph, Collections.emptySet(), Collections.emptySet());
        for (Application app : this.extensionOperationApplications) {
            GraphOperation op = (GraphOperation) app.getType();
            result = result.merge(op.execute(app.getBinding(), this.extensionOperationApplications));
        }
        return result;
    }

    public boolean verify(AbstractMorphism instance) {
        return this.predicateApplications.stream().allMatch(
                pred -> {
                    Multispan pullback = pred.getBinding().pullback(instance.getName().query(pred.getType().arity().getName()),
                            instance,
                            instance.getDomain().getName().query(pred.getType().arity().getName()),
                            NamingStrategy.givePrecedenceToPrefix(instance.getDomain().getName()));
                    AbstractMorphism adjustedInstance = pullback.getMorphismWithCodomain(pred.getType().arity());
                    Predicate p = (Predicate) pred.getType();
                    return p.check(adjustedInstance);
                }
        );
    }


}
