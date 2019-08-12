package no.hvl.past.graph.operations;

import no.hvl.past.graph.*;
import no.hvl.past.graph.names.Name;
import no.hvl.past.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Predefined {


    static Map<String, Predicate> predefinedPredicates;
    static Map<String, GraphOperation> predefinedOperations;

    public static final String SINGLETON_PRED_NAME = "[singleton]";
    public static final Predicate SINGLETON = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(SINGLETON_PRED_NAME)
                    .node("n")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            return instance.select(Triple.fromNode(Name.identifier("n"))).size() == 1;
        }
    };

    public static final String ABSTRACT_PRED_NAME = "[abstract]";
    public static final Predicate ABSTRACT = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(ABSTRACT_PRED_NAME)
                    .node("a")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            return instance.select(Triple.fromNode(Name.identifier("a"))).isEmpty();
        }

    };

    public static final String INJECTIVE_PRED_NAME = "[injective]";
    public static final Predicate INJECTIVE = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(INJECTIVE_PRED_NAME)
                    .edge("a","f", "b")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            Set<Triple> select = instance.select(new Triple(Name.identifier("a"), Name.identifier("f"), Name.identifier("b")));
            List<Name> targets = select.stream().map(Triple::getTarget).collect(Collectors.toList());
            Set<Name> targetsset = new HashSet<>(targets);
            return targets.size() == targetsset.size();
        }
    };

    public static final String SURJECTIVE_PRED_NAME = "[surjective]";
    public static final Predicate SURJECTIVE = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(SURJECTIVE_PRED_NAME)
                    .edge("a","f", "b")
                    .build();
        }
        @Override
        public boolean check(AbstractMorphism instance) {
            Set<Triple> triples = instance.select(new Triple(Name.identifier("a"), Name.identifier("f"), Name.identifier("b")));
            Set<Name> targets = instance.select(Triple.fromNode(Name.identifier("b"))).stream().map(Triple::getLabel).collect(Collectors.toSet());
            return targets.stream().allMatch(n -> triples.stream().anyMatch(triple -> triple.getTarget().equals(n)));
        }
    };

    public static final String FUNCTION_PRED_NAME = "[function]";
    public static final Predicate FUNCTION = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(FUNCTION_PRED_NAME)
                    .edge("a","f", "b")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            Set<Triple> select = instance.select(new Triple(Name.identifier("a"), Name.identifier("f"), Name.identifier("b")));
            List<Name> sources = select.stream().map(Triple::getSource).collect(Collectors.toList());
            Set<Name> sourcesset = new HashSet<>(sources);
            return sources.size() == sourcesset.size();
        }
    };

    public static final String NON_NULL_PRED_NAME = "[nonNull]";
    // aka total
    public static final Predicate NON_NULL = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(NON_NULL_PRED_NAME)
                    .edge("a","f", "b")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            Set<Triple> triples = instance.select(new Triple(Name.identifier("a"), Name.identifier("f"), Name.identifier("b")));
            Set<Name> sources = instance.select(Triple.fromNode(Name.identifier("a"))).stream().map(Triple::getLabel).collect(Collectors.toSet());
            return sources.stream().allMatch(n -> triples.stream().anyMatch(triple -> triple.getSource().equals(n)));
        }
    };

    public static final String INVERSE_PRED_NAME = "[inverse]";
    public static final Predicate INVERSE = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(INVERSE_PRED_NAME)
                    .edge("a","i", "b")
                    .edge("b", "ii", "a")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            Set<Triple> there = instance.select(new Triple(Name.identifier("a"), Name.identifier("i"), Name.identifier("b")));
            Set<Triple> back = instance.select(new Triple(Name.identifier("b"), Name.identifier("ii"), Name.identifier("a")));
            return there.stream().allMatch(t1 -> back.stream().filter(t -> t.getSource().equals(t1.getTarget())).allMatch(t2 -> t2.getTarget().equals(t1.getSource()))) &&
                    back.stream().allMatch(t1 -> there.stream().filter(t -> t.getSource().equals(t1.getTarget())).allMatch(t2 -> t2.getTarget().equals(t1.getSource())));
        }
    };


    public static final String COMMUTES_PRED_NAME = "[commutes]";
    public static final Predicate COMMUTES = new Predicate() {
        @Override
        public Graph arity() {
            return new Graph.Builder(COMMUTES_PRED_NAME)
                    .edge("a","i", "b")
                    .edge("b", "ii", "c")
                    .edge("a", "iii", "c")
                    .build();
        }

        @Override
        public boolean check(AbstractMorphism instance) {
            Set<Triple> fst = instance.select(new Triple(Name.identifier("a"), Name.identifier("i"), Name.identifier("b")));
            Set<Triple> snd = instance.select(new Triple(Name.identifier("b"), Name.identifier("ii"), Name.identifier("c")));
            Set<Triple> trd = instance.select(new Triple(Name.identifier("a"), Name.identifier("iii"), Name.identifier("c")));

            return trd.stream().allMatch(t ->
                    fst.stream()
                            .filter(f -> f.getSource().equals(t.getSource()))
                            .anyMatch(ft ->
                                    snd.stream().filter(st -> st.getTarget().equals(t.getTarget())).anyMatch(st -> ft.getTarget().equals(st.getSource()))
                            )) &&
                    fst.stream().flatMap(ft ->
                            snd.stream().filter(st -> st.getSource().equals(ft.getTarget())).map(st -> new Pair<Name, Name>(ft.getSource(), st.getTarget())))
                            .anyMatch(p -> trd.stream().anyMatch(t -> t.getSource().equals(p.getFirst()) && t.getTarget().equals(p.getSecond())));
        }
    };

    public static final String INHERITANCE_OP_NAME = "[inheritance>";
    public static final GraphOperation INHERITANCE = new GraphOperation() {

        public Graph arity() {
            return new Graph.Builder(INHERITANCE_OP_NAME)
                    .edge("i", "s", "t")
                    .build();
        }

        @Override
        public GraphModification execute(AbstractMorphism binding, List<Application> otherOperationApplications) {
            Set<Triple> otherSupers = otherOperationApplications
                    .stream()
                    .filter(app -> app.getType().arity().getName().equals(Name.identifier(INHERITANCE_OP_NAME)))
                    .map(app -> app.getBinding().apply(new Triple(Name.identifier("i"), Name.identifier("s"), Name.identifier("t"))).get())
                    .collect(Collectors.toSet());

            Triple superEdge = binding.apply(new Triple(Name.identifier("i"), Name.identifier("s"), Name.identifier("t"))).get();
            Set<Triple> newEdges = new HashSet<>();
            newEdges.addAll(binding.getCodomain().incoming(superEdge.getTarget())
                    .stream()
                    .filter(triple -> !triple.isNode())
                    .filter(triple -> !otherSupers.contains(triple))
                    .map(i -> new Triple(
                            i.getSource(),
                            i.getLabel().prefix(superEdge.getLabel()),
                            superEdge.getSource()
            )).collect(Collectors.toSet()));
            newEdges.addAll(binding.getCodomain().outgoing(superEdge.getTarget())
                    .stream()
                    .filter(triple -> !triple.isNode())
                    .filter(triple -> !otherSupers.contains(triple))
                    .map(o -> new Triple(
                            superEdge.getSource(),
                            o.getLabel().prefix(superEdge.getLabel()),
                            o.getTarget()
            )).collect(Collectors.toSet()));
            return GraphModification.create(binding.getCodomain(), newEdges, Collections.emptySet());
        }
    };



    static {
        predefinedPredicates = new HashMap<>();
        predefinedPredicates.put(SINGLETON_PRED_NAME, SINGLETON);
        predefinedPredicates.put(ABSTRACT_PRED_NAME, ABSTRACT);
        predefinedPredicates.put(INJECTIVE_PRED_NAME, INJECTIVE);
        predefinedPredicates.put(SURJECTIVE_PRED_NAME, SURJECTIVE);
        predefinedPredicates.put(FUNCTION_PRED_NAME, FUNCTION);
        predefinedPredicates.put(NON_NULL_PRED_NAME, NON_NULL);
        predefinedPredicates.put(INVERSE_PRED_NAME, INVERSE);
        predefinedPredicates.put(COMMUTES_PRED_NAME, COMMUTES);
        predefinedOperations = new HashMap<>();
        predefinedOperations.put(INHERITANCE_OP_NAME, INHERITANCE);
    }

    /**
     * Pure library class, therefore no constructor.
     */
    private Predefined() {
    }


}
