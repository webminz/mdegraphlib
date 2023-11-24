package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.Optional;

/**
 * Composes two succinct edges with each other.
 *
 * <pre>
 *  (0)-[01]->(1)->[12]->(2)
 *
 *  induces:
 *
 *  (0)-[02 := 01;12]->(2)
 * </pre>
 *
 */
public class Compose implements GraphOperation {

    private static Compose instance;

    private static Graph OVERLAP_ARITY = new GraphImpl(
            Name.identifier("CHAIN_OUTLINE"),
            Sets.newHashSet(
                    Triple.node(Universe.CHAIN_FST.getSource()),
                    Triple.node(Universe.CHAIN_SND.getTarget())
            )
    );


    private static Graph OUTPUT_ARITY = new GraphImpl(
            Name.identifier("ARROW_02"),
            Sets.newHashSet(
                    Triple.node(Universe.TRIANGLE_HYP.getSource()),
                    Triple.node(Universe.TRIANGLE_HYP.getTarget()),
                            Universe.TRIANGLE_HYP
                    )
    );

    private Compose() {
    }

    @Override
    public Graph inputArity() {
        return Universe.CHAIN;
    }

    @Override
    public Graph overlapArity() {
        return OVERLAP_ARITY;
    }

    @Override
    public Graph outputArity() {
        return OUTPUT_ARITY;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(),false,true)
                .importMorphism(instance);

        instance.allInstances(Universe.CHAIN_FST)
                .flatMap(t -> instance.allInstances(Universe.CHAIN_SND)
                        .filter(tt -> tt.getSource().equals(t.getTarget()))
                        .map(t::compose)
                        .filter(Optional::isPresent)
                        .map(Optional::get))
                .forEach(triple -> {
                    builders.edge(triple.getSource(), triple.getLabel(), triple.getTarget());
                    builders.map(triple.getLabel(), Universe.TRIANGLE_HYP.getLabel());
                });

        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(arity());
        builders.morphism(getName().appliedTo(instance.getName()));
        return builders.getResult(GraphMorphism.class);
    }


    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        return instance.allNodeInstances(Universe.CHAIN_FST.getSource())
                .flatMap(src -> instance.allNodeInstances(Universe.CHAIN_SND.getTarget()).map(trg -> new Pair<>(src,trg)))
                .allMatch(pair -> instance.allInstances(Universe.TRIANGLE_HYP)
                        .filter(edge -> edge.getSource().equals(pair.getFirst()) && edge.getTarget().equals(pair.getSecond()))
                        .count() ==
                        instance.allOutgoingInstances(Universe.CHAIN_FST,pair.getFirst())
                        .flatMap(fst -> instance.allInstances(Universe.CHAIN_SND)
                                .filter(edge -> edge.getTarget().equals(pair.getSecond()))
                                .filter(edge -> fst.getTarget().equals(edge.getSource())))
                        .count());
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(),false,true)
                .importMorphism(instance);
        instance.allInstances(Universe.TRIANGLE_HYP).forEach(builders::undoEdge);
        builders.graph(instance.domain().getName());
        builders.codomain(Universe.TRIANGLE);
        builders.morphism(instance.getName());
        return builders.getResult(GraphMorphism.class);
    }


    public static Compose getInstance() {
        if (instance == null) {
            instance = new Compose();
        }
        return instance;
    }

    @Override
    public String nameAsString() {
            return "<[compose]>";
    }

    @Override
    public Graph arity() {
        return Universe.TRIANGLE;
    }
}
