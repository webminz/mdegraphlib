package no.hvl.past.graph;

import no.hvl.past.graph.names.Name;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Basically this construction represents a relation among graph elements,
 * which is encoded by means of category theory, i.e. morphisms.
 * As graphs are usually abstract representations of models,
 * this construction can also be referred to as a multimodel, i.e.
 * coordinating multiple models and common elements among them.
 *
 */
public class Multispan implements Element, Iterable<EquivalenceClass> {

    private final Name name;

    private final AbstractGraph apex;

    private final List<AbstractGraph> diagramNodes;

    private final List<AbstractMorphism> diagramEdges;

    private boolean isExtensionCalculated;
    private Set<Triple> sharedElements;
    private Set<EquivalenceClass> classes;

    Multispan(Name name, AbstractGraph apex, List<AbstractGraph> diagramNodes, List<AbstractMorphism> diagramEdges) {
        this.name = name;
        this.apex = apex;
        this.diagramNodes = diagramNodes;
        this.diagramEdges = diagramEdges;
        this.isExtensionCalculated = false;
        this.sharedElements = new HashSet<>();
    }

    public boolean areRelated(Triple t1, Triple t2) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        Optional<EquivalenceClass> cls = this.classes.stream().filter(e -> e.contains(t1)).findFirst();
        return cls.map(e -> e.contains(t2)).orElse(false);
    }

    public boolean areRelated(Name n1, Name n2) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }
        Optional<EquivalenceClass> cls = this.classes.stream().filter(e -> e.contains(Triple.fromNode(n1))).findFirst();
        return cls.map(e -> e.contains(Triple.fromNode(n2))).orElse(false);
    }

    public AbstractGraph colimit(Name resultName, NamingStrategy strategy) {
        if (!isExtensionCalculated) {
            calculateExtension();
        }

        Set<EquivalenceClass> nodes = this.classes.stream().filter(EquivalenceClass::isNodeClass).collect(Collectors.toSet());

        Set<Triple> shared = this.classes.stream().map(eqv -> eqv.name(strategy).prefix(apex.getName())).collect(Collectors.toSet());

        List<AbstractGraph> members = new ArrayList<>();
        members.add(new Graph(apex.getName(), shared));

        for (AbstractGraph g : this.diagramNodes) {
            Set<Triple> toRemove = new HashSet<>(); // shared parts
            toRemove.addAll(sharedElements.stream().filter(triple -> triple.hasPrefix(g.getName())).map(t -> t.unprefix(g.getName())).collect(Collectors.toSet()));
            Set<Triple> toAdd = new HashSet<>(); // glued parts
            toAdd.addAll(StreamSupport.stream(g.spliterator(), false)
                    .filter(Triple::isEddge)
                    .filter(t -> !sharedElements.contains(t.prefix(g.getName())))
                    .filter(t -> sharedElements.contains(Triple.fromNode(t.getSource().prefix(g.getName()))) || sharedElements.contains(Triple.fromNode(t.getTarget().prefix(g.getName()))))
                    .map(t -> {
                        toRemove.add(t);
                        Triple nu = new Triple(renameNode(t.getSource(), nodes, strategy, g),
                                t.getLabel().prefix(g.getName()),
                                renameNode(t.getTarget(), nodes, strategy, g));
                        return nu;
                    }).collect(Collectors.toSet()));

            members.add(GraphModification.create(g, toAdd, toRemove, g.getName()));
        }

        return new GraphUnion(members, resultName);
    }

    private void calculateExtension() {
        final Set<EquivalenceClass> work = new HashSet<>();

        // Building the direct equivalence classes arising from the morphisms containing two elements
        diagramEdges.forEach(m -> {
            StreamSupport.stream(getApex().spliterator(), false)
                    .filter(m::definedAt)
                    .forEach(t -> {
                        sharedElements.add(t.prefix(m.getDomain().getName()));
                        Triple mt = m.apply(t).get().prefix(m.getCodomain().getName());
                        sharedElements.add(mt);
                        EquivalenceClass clazz = new EquivalenceClass(new HashSet<>(Arrays.asList(t.prefix(m.getDomain().getName()), mt)));
                        work.add(clazz);
                    });

        });

        // Merging overlapping classes until it becomes stable
        boolean converged = false;
        while (!converged) {
            converged = true;
            Set<EquivalenceClass> newWork = new HashSet<>();
            boolean foundMatchInIteration = false;
            for (EquivalenceClass e1 : work) {
                for (EquivalenceClass e2 : work) {
                    if (e1.overlaps(e2)) {
                        converged = false;
                        newWork.add(e1.merge(e2));
                        foundMatchInIteration = true;
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

        this.classes = work;
        this.isExtensionCalculated = true;
    }


//    public Graph internalize(Name resultName) {
//        Set<Triple> result = new HashSet<>();
//        result.addAll(this.apex.prefix().getElements());
//        this.diagramEdges.forEach(m -> {
//            result.addAll(this.apex.prefix().getNodes().stream()
//                    .filter(node -> m.definedAt(node.unprefixAll()))
//                    .map(node -> new Triple(
//                            node,
//                            node.unprefixAll().composeSequentially(m.getName()),
//                            m.apply(node.unprefixAll().prefix(m.getCodomain().getName())).get())
//                    )
//                    .collect(Collectors.toSet()));
//            result.addAll(this.apex.getEdges().stream()
//                    .filter(m::definedAt)
//                    .map(triple -> new Triple(
//                            m.apply(triple.getSource()).get(),
//                            triple.getLabel().prefix(this.apex.getName()),
//                            m.apply(triple.getTarget()).get()
//                    ))
//                    .collect(Collectors.toSet()));
//        });
//
//        this.diagramNodes.forEach(g -> result.addAll(g.prefix().getElements()));
//
//        return new Graph(resultName, result);
//    }

    private Name renameNode(Name node, Set<EquivalenceClass> nodes, NamingStrategy strategy, AbstractGraph src) {
        Optional<EquivalenceClass> x = nodes.stream().filter(e -> e.containsLabelName(node.prefix(src.getName()))).findFirst();
        if (x.isPresent()) {
            return x.get().name(strategy).getLabel().prefix(apex.getName());
        } else {
            return node.prefix(src.getName());
        }
    }


    public AbstractGraph getApex() {
        return apex;
    }

    public List<AbstractGraph> getDiagramNodes() {
        return this.diagramNodes;
    }

    public List<AbstractMorphism> getDiagramEdges() {
        return this.diagramEdges;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    public Graph toGraph(Name containerName) {
        HashSet<Triple> elements = new HashSet<>();
        elements.add(Triple.fromNode(this.apex.getName()));
        elements.addAll(this.diagramNodes.stream().map(g -> Triple.fromNode(g.getName())).collect(Collectors.toSet()));
        elements.addAll(this.diagramEdges.stream()
                .map(m -> new Triple(m.getDomain().getName(), m.getName(), m.getCodomain().getName()))
                .collect(Collectors.toList()));
        return new Graph(containerName, elements);
    }

    @Override
    public void sendTo(OutputPort<?> port) {
        port.beginMultispan(
                getName(),
                this.diagramNodes.stream().map(AbstractGraph::getName).collect(Collectors.toList()),
                this.diagramEdges.stream().map(AbstractMorphism::getName).collect(Collectors.toList()));
        this.diagramEdges.forEach(m -> m.sendTo(port));
        port.endMultispan();
    }


    private static Set<Triple> checkForIllFormedElements(Graph apex, List<AbstractGraph> diagramNodes, List<AbstractMorphism> diagramEdges) {
        Set<Triple> result = new HashSet<>();
        result.addAll(diagramNodes.stream()
                .filter(g -> diagramEdges.stream().noneMatch(m -> m.getCodomain().equals(g)))
                .map(g -> Triple.fromNode(g.getName()))
                .collect(Collectors.toSet()));
        result.addAll(diagramEdges.stream()
                .filter(m -> !m.getDomain().equals(apex) || !diagramNodes.contains(m.getCodomain()))
                .map(m -> new Triple(m.getDomain().getName(), m.getName(), m.getCodomain().getName()))
                .collect(Collectors.toSet()));
        return result;
    }

    public static Multispan create(Name name, Graph apex, List<AbstractGraph> diagramNodes, List<AbstractMorphism> diagramEdges) throws GraphError {
        Set<Triple> ill = checkForIllFormedElements(apex, diagramNodes, diagramEdges);
        if (!ill.isEmpty()) {
            throw new GraphError(GraphError.ERROR_TYPE.ILL_FORMED, ill);
        }
        return new Multispan(name, apex, diagramNodes, diagramEdges);
    }

    public AbstractMorphism getMorphismWithCodomain(AbstractGraph sourceArity) {
        return this.diagramEdges.stream().filter(m -> m.getCodomain().getName().equals(sourceArity.getName())).findFirst().get();
    }

    @Override
    public Iterator<EquivalenceClass> iterator() {
        if (this.isExtensionCalculated) {
            this.calculateExtension();
        }
        HashSet<EquivalenceClass> result = new HashSet<>();
     //   result.addAll(this.localElements.stream().map(EquivalenceClass::fromTriple).collect(Collectors.toSet()));
        result.addAll(this.classes);
        return result.iterator();
    }
}
