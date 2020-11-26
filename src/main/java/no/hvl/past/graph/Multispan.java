package no.hvl.past.graph;

import com.google.common.collect.Sets;
import no.hvl.past.graph.elements.EquivalenceClass;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Basically this construction represents a relation among graph elements,
 * which is encoded by means of category theory, i.e. morphisms.
 * As graphs are usually abstract representations of models,
 * this construction can also be referred to as a multimodel, i.e.
 * coordinating multiple models and common elements among them.
 *
 */
public class Multispan implements WideSpan, Iterable<EquivalenceClass> {

    private final Name name;
    private final Graph apex;
    private final List<Graph> diagramNodes;
    private final List<GraphMorphism> diagramEdges;

    private boolean isExtensionCalculated;
    private Set<Triple> sharedElements;
    private Set<EquivalenceClass> classes;

    Multispan(
            Name name,
            Graph apex,
            List<Graph> diagramNodes,
            List<GraphMorphism> diagramEdges) {
        this.name = name;
        this.apex = apex;
        this.diagramNodes = diagramNodes;
        this.diagramEdges = diagramEdges;
        this.isExtensionCalculated = false;
        this.sharedElements = new HashSet<>();
        this.classes = new HashSet<>();
    }

    public boolean areRelated(Triple t1, Triple t2) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        return classes.stream().anyMatch(e -> e.relates(t1, t2));
    }

    public boolean areRelated(Graph g1, Triple t1, Graph g2, Triple t2) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        return classes.stream().anyMatch(e -> e.relates(g1.getName(), t1, g2.getName(), t2));
    }

    public boolean areRelated(Name n1, Name n2) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        return classes.stream().anyMatch(e -> e.relates(Triple.node(n1), Triple.node(n2)));
    }

    public boolean areRelated(Graph g1, Name n1, Graph g2, Name n2) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        return classes.stream().anyMatch(e -> e.relates(g1.getName(), Triple.node(n1), g2.getName(), Triple.node(n2)));
    }

    public Graph internalize(Name resultName) {
        Set<Triple> elements = new HashSet<>();
        this.apex.elements().map(t -> t.prefix(apex.getName())).forEach(elements::add);
        this.diagramEdges.stream().flatMap(morph ->
                this.apex.elements()
                .filter(morph::definedAt)
                .flatMap(old -> {
                    Triple applied = morph.apply(old).get();
                    return Stream.of(
                            new Triple(old.getSource().prefixWith(apex.getName()), morph.getName().prefixWith(old.getSource()).prefixWith(apex.getName()), applied.getSource().prefixWith(morph.codomain().getName())),
                            new Triple(old.getTarget().prefixWith(apex.getName()), morph.getName().prefixWith(old.getTarget()).prefixWith(apex.getName()), applied.getTarget().prefixWith(morph.codomain().getName()))
                    );
                })).forEach(elements::add);
        List<Graph> members = new ArrayList<>();
        members.add(new GraphImpl(apex.getName(), elements));
        members.addAll(this.diagramNodes.stream().map(Graph::prefix).collect(Collectors.toSet()));
        return new GraphUnion(members, resultName);
    }


    public Graph colimit(Name resultName) {
        Set<EquivalenceClass> convergedClasses = convergedEquivalenceClasses();

        Set<EquivalenceClass> nodes = convergedClasses.stream().filter(EquivalenceClass::isNodeClass).collect(Collectors.toSet());
        Set<Triple> edges = convergedClasses.stream()
                .filter(EquivalenceClass::isEdgeClass)
                .map(e -> e.properRepresentative(nodes))
                .collect(Collectors.toSet());

        List<Graph> members = new ArrayList<>();
        members.add(new GraphImpl(apex.getName(), Sets.union(edges, nodes.stream().map(EquivalenceClass::representative).collect(Collectors.toSet()))));

        for (Graph g : this.diagramNodes) {
            Set<Triple> toRemove = new HashSet<>(sharedElements.stream().filter(triple -> triple.hasPrefix(g.getName())).map(t -> t.unprefix(g.getName())).collect(Collectors.toSet())); // shared parts
            Set<Triple> toAdd = g.elements() // glued parts
                    .filter(Triple::isEddge)
                    .filter(t -> !sharedElements.contains(t.prefix(g.getName())))
                    .filter(t -> sharedElements.contains(Triple.node(t.getSource().prefixWith(g.getName()))) || sharedElements.contains(Triple.node(t.getTarget().prefixWith(g.getName()))))
                    .map(t -> {
                        toRemove.add(t);
                        Name newSrc = sharedElements.contains(Triple.node(t.getSource().prefixWith(g.getName()))) ?
                                nodes.stream().filter(eqv -> eqv.containsNode(g.getName(), t.getSource())).findFirst().get().representative().getLabel() : t.getSource().prefixWith(g.getName());
                        Name newTrg = sharedElements.contains(Triple.node(t.getTarget().prefixWith(g.getName()))) ?
                                nodes.stream().filter(eqv -> eqv.containsNode(g.getName(), t.getTarget())).findFirst().get().representative().getLabel() : t.getTarget().prefixWith(g.getName());
                        return new Triple(newSrc, t.getLabel().prefixWith(g.getName()), newTrg);
                    }).collect(Collectors.toSet());

            Superobject adding = new Superobject(Name.anonymousIdentifier(), g, Name.anonymousIdentifier()) {
                @Override
                protected Stream<Triple> inserts() {
                    return toAdd.stream();
                }
            };
            Subobject removing = new Subobject(Name.anonymousIdentifier(), adding.getResult(), g.getName()) {

                @Override
                public boolean deletes(Name name) {
                    return toRemove.stream().map(Triple::getLabel).anyMatch(name::equals);
                }
            };

            members.add(removing.getResult());

        }

        return new GraphUnion(members, resultName);
    }

    private void calculateExtension() {
        this.apex.elements().map(this::buildClassFor).forEach(this.classes::add);
        this.isExtensionCalculated = true;
    }

    private EquivalenceClass buildClassFor(Triple triple) {
        List<Triple> elements = new ArrayList<>();
        elements.add(triple.prefix(apex.getName()));
        this.diagramEdges.forEach(m -> {
            if (m.definedAt(triple)) {
                Triple target = m.apply(triple).get();
                elements.add(target.prefix(m.codomain().getName()));
            }
        });
        this.sharedElements.addAll(elements);
        return EquivalenceClass.create(this.diagramNodes.stream().map(Graph::getName).collect(Collectors.toList()), elements);
    }


    private Set<EquivalenceClass> convergedEquivalenceClasses() {
        final Set<EquivalenceClass> work = new HashSet<>();
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        work.addAll(this.classes);
        // Merging overlapping classes until it becomes stable
        boolean converged = false;
        while (!converged) {
            converged = true;
            Set<EquivalenceClass> newWork = new HashSet<>();
            boolean foundMatchInIteration = false;
            for (EquivalenceClass e1 : work) {
                for (EquivalenceClass e2 : work) {
                    if (e1.overlapsButNonIdentical(e2)) {
                        foundMatchInIteration = true;
                        converged = false;
                        EquivalenceClass toAdd = e1.merge(e2);
                        newWork.add(toAdd);
                    }
                }
                if (!foundMatchInIteration) {
                    newWork.add(e1);
                }
                foundMatchInIteration = false;
            }
            work.clear();
            work.addAll(newWork);
        }
        return work;
    }

//    /**
//     * Verifies whether this multispan is consistent with a respective type multispan.
//     */
//    public boolean isStructurallyConsistent(Multispan typeCorr, List<GraphMorphism> typings) {
//        for (GraphMorphism leg : this.diagramEdges) {
//            if (!leg.verify()) {
//                return false;
//            }
//        }
//        if (typings.isEmpty()) {
//            return true; // We have no further typing thus we are done
//        }
//        if (this.arity() == typeCorr.arity() && (typings.size() == arity() + 1)) {
//            for (int i = 0; i < arity(); i++) {
//                final int currentI =i;
//                GraphMorphism zeroType = typings.get(0);
//                GraphMorphism type = typings.get(i + 1);
//                if (!typeCorr.getDiagramEdge(i).flatMap(typeLeg ->
//                        getDiagramEdge(currentI).map(leg -> {
//                            GraphMorphism oneWay = leg.compose(type);
//                            GraphMorphism theOtherWay = zeroType.compose(typeLeg);
//                            return this.apex.elements().allMatch(x -> oneWay.apply(x).equals(theOtherWay.apply(x)));
//                        })).orElse(false)) {
//                    return false;
//                }
//
//            }
//            return true;
//        }
//        return false; // Typing and type span does not much
//    }

    public GraphMorphism getDiagramEdge(int i) {
        return this.diagramEdges.get(i);
    }


    public Graph getApex() {
        return apex;
    }

    @Override
    public int size() {
        return diagramNodes.size();
    }

    @Override
    public Stream<Triple> witnesses(Triple... elements) {
        return null;
    }

    @Override
    public Sketch apex() {
        return null;
    }

    @Override
    public Optional<Sketch> component(int i) {
        return Optional.empty();
    }

    @Override
    public Optional<SketchMorphism> projection(int i) {
        return Optional.empty();
    }


    @Override
    public Name getName() {
        return this.name;
    }


    @Override
    public boolean verify() {
        return false;
    }


    private static Set<Triple> checkForIllFormedElements(Graph apex, List<Graph> diagramNodes, List<GraphMorphism> diagramEdges) {
        Set<Triple> result = new HashSet<>();
        result.addAll(diagramNodes.stream()
                .filter(g -> diagramEdges.stream().noneMatch(m -> m.codomain().equals(g)))
                .map(g -> Triple.node(g.getName()))
                .collect(Collectors.toSet()));
        result.addAll(diagramEdges.stream()
                .filter(m -> !m.domain().equals(apex) || !diagramNodes.contains(m.codomain()))
                .map(m -> new Triple(m.domain().getName(), m.getName(), m.codomain().getName()))
                .collect(Collectors.toSet()));
        return result;
    }

    public static Multispan create(Name name, Graph apex, List<Graph> diagramNodes, List<GraphMorphism> diagramEdges) throws GraphError {
        Set<Triple> ill = checkForIllFormedElements(apex, diagramNodes, diagramEdges);
        if (!ill.isEmpty()) {
            throw new GraphError(GraphError.ERROR_TYPE.ILL_FORMED, ill);
        }
        return new Multispan(name, apex, diagramNodes, diagramEdges);
    }



    @Override
    public Iterator<EquivalenceClass> iterator() {
        if (this.isExtensionCalculated) {
            this.calculateExtension();
        }
        return this.classes.iterator();
    }
}
