package no.hvl.past.graph.operations;

import com.google.common.collect.Sets;
import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.NameSet;
import no.hvl.past.util.PartitionAlgorithm;
import no.hvl.past.util.Holder;

import java.util.*;
import java.util.stream.Collectors;

public class Coequalizer implements GraphOperation {

    private static Coequalizer instance;

    private Coequalizer() {
    }

    private static final Graph OUTPUT_AR = new GraphImpl(Name.identifier("ARROW_20"), Sets.newHashSet(
            Triple.node(Universe.COEQUALIZER_MEDIATOR.getSource()),
            Triple.node(Universe.COEQUALIZER_MEDIATOR.getTarget()),
            Universe.COEQUALIZER_MEDIATOR
    ));

    private static final Graph OVERLAP_AR = new GraphImpl(Name.identifier("ARROW_20"), Sets.newHashSet(
            Triple.node(Universe.COEQUALIZER_MEDIATOR.getSource())
    ));

    private Set<Set<Name>> createClusters(GraphMorphism instance) {

        PartitionAlgorithm<Name> algorithm = new PartitionAlgorithm<>(instance.allNodeInstances(Universe.CELL_LHS.getTarget())
                .collect(Collectors.toSet()));

        instance.allSrcCoincidentInstances(Universe.CELL_LHS, Universe.CELL_RHS)
                .forEach(pair-> algorithm.relate(pair.getFirst().getTarget(), pair.getSecond().getTarget()));

        return algorithm.getResult();
    }


    @Override
    public Graph inputArity() {
        return Universe.CELL;
    }

    @Override
    public Graph overlapArity() {
        return OVERLAP_AR;
    }

    @Override
    public Graph outputArity() {
        return OUTPUT_AR;
    }

    @Override
    public GraphMorphism execute(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true).importMorphism(instance);

        Set<Set<Name>> clusters = createClusters(instance);

        for (Set<Name> cluster : clusters) {
            Name newName = new NameSet(cluster).toName();
            builders.node(newName);
            builders.map(newName, Universe.COEQUALIZER_MEDIATOR.getTarget());
            for (Name member : cluster) {
                builders.edge(member, newName.injectedFrom(member), newName);
                builders.map(newName.injectedFrom(member), Universe.COEQUALIZER_MEDIATOR.getLabel());
            }
        }

        builders.graph(getName().appliedTo(instance.domain().getName()));
        builders.codomain(arity());
        builders.morphism(getName().appliedTo(instance.getName()));
        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public boolean isExecutedCorrectly(GraphMorphism instance) {
        Set<Set<Name>> clusters = createClusters(instance);


        for (Set<Name> cluster : clusters) {
            Holder<Name> expectedTargetForCluster = new Holder<>();
            for (Name node : cluster) {
                List<Triple> collect = instance.allOutgoingInstances(Universe.COEQUALIZER_MEDIATOR, node).collect(Collectors.toList());
                if (collect.size() != 1) {
                    return false;
                } else {
                    Name target = collect.get(0).getTarget();
                    if (expectedTargetForCluster.hasValue()) {
                        if (!expectedTargetForCluster.unsafeGet().equals(target)) {
                            return false;
                        }
                    } else {
                        expectedTargetForCluster.set(target);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public GraphMorphism undo(GraphMorphism instance, ExecutionContext context) throws GraphError {
        GraphBuilders builders = new GraphBuilders(context.universe(), false, true).importMorphism(instance);

        instance.allInstances(Universe.COEQUALIZER_MEDIATOR)
                .forEach(builders::undoEdge);

        instance.allNodeInstances(Universe.COEQUALIZER_MEDIATOR.getTarget())
                .forEach(builders::undoNode);

        builders.graph(instance.domain().getName());
        builders.codomain(arity());
        builders.morphism(instance.getName());
        return builders.getResult(GraphMorphism.class);
    }

    @Override
    public String nameAsString() {
        return "<[partition]>";
    }

    @Override
    public Graph arity() {
        return Universe.COEQUALIZER_DIAGRAM;
    }

    public static Coequalizer getInstance() {
        if (instance == null) {
            instance = new Coequalizer();
        }
        return instance;
    }
}
