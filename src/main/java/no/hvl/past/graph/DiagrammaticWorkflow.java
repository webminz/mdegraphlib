package no.hvl.past.graph;


import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiagrammaticWorkflow implements GraphMorphism {

    private final Name name;
    private final GraphMorphism morphism;
    private final Sketch codomain;
    private final Map<Diagram, DiagramActivityNode> domainDiagrams;
    private final Map<Name, Name> newMappings;
    private final Set<Triple> newElements;
    private final ExecutionContext executionContext;
    private boolean isExecuted = false;

    public boolean executedCorrectly(Sketch sketch) {
        if (isExecuted) {
            return sketch.carrier().equals(codomain()) &&
                    sketch.diagrams().allMatch(diag -> domainDiagrams.containsKey(diag) && domainDiagrams.get(diag).isValid());
        }
        return false;
    }


    private static class DiagramActivityNode {
        private final Diagram type;
        private boolean valid;

        DiagramActivityNode(Diagram type) {
            this.type = type;
            this.valid = false;
        }

        public Diagram execute(DiagrammaticWorkflow target) throws GraphError {
            Pair<GraphMorphism, GraphMorphism> pullback = this.type.binding().pullback(target);

            // TODO work around the tuple na
            GraphMorphism toCheck = pullback.getFirst();

            this.valid = type.label().isSatisfied(toCheck);

            // if it is an operation and not already satisfied we have to execute it
            if (!this.valid && type.label() instanceof GraphOperation) {
                GraphOperation op = (GraphOperation) this.type.label();
                GraphMorphism result = op.fix(toCheck, target.executionContext);
                op.outputArity()
                        .elements()
                        .filter(t -> !op.inputArity().contains(t))
                        .forEach(triple -> {
                            result.allInstances(triple).forEach(inst -> {
                                if (inst.isEddge()) {
                                    Name src = pullback.getSecond().map(inst.getSource()).orElse(inst.getSource());
                                    Name trg = pullback.getSecond().map(inst.getTarget()).orElse(inst.getTarget());
                                    Triple edge = Triple.edge(src, inst.getLabel(), trg);
                                    target.newElements.add(edge);
                                    target.newMappings.put(inst.getLabel(), type.binding().map(triple.getLabel()).get());
                                } else {
                                    if (!pullback.getRight().definedAt(inst)) {
                                        // only if it is truly new!
                                        target.newElements.add(inst);
                                        target.newMappings.put(inst.getLabel(), type.binding().map(triple.getLabel()).get());
                                    }
                                }
                            });
                        });
                this.valid = true; // Fixing produces valid results
            }

            return type;
        }


        boolean isValid() {
            return valid;
        }
    }

    public boolean isExecuted() {
        return isExecuted;
    }

    public DiagrammaticWorkflow(Name name, GraphMorphism instance, Sketch sketch, ExecutionContext executionContext) {
        this.name = name;
        this.morphism = instance;
        this.codomain = sketch;
        this.executionContext = executionContext;
        this.domainDiagrams = new HashMap<>();
        this.newMappings = new HashMap<>();
        this.newElements = new HashSet<>();
    }

    public DiagrammaticWorkflow execute() throws GraphError {
        List<DiagramActivityNode> workflowNet = buildWorkflowNet();

        for (DiagramActivityNode node : workflowNet) {
            this.domainDiagrams.put(node.execute(this), node);
        }

        this.isExecuted = true;
        return this;
    }

    private List<DiagramActivityNode> buildWorkflowNet() {
        List<DiagramActivityNode> nodes = new ArrayList<>();
        Set<Diagram> typeDiagrams = codomain.diagrams().collect(Collectors.toSet());
        while (!typeDiagrams.isEmpty()) {
            List<Diagram> copy = new ArrayList<>(typeDiagrams);
            boolean found = false;
            int idx = 0;

            while (!found) {
                found = true;
                for (Diagram d : copy) {
                    if (!copy.get(idx).equals(d) && copy.get(idx).directlyDependsOn(d)) {
                        found = false;
                    }
                }
                if (!found) {
                    idx++;
                }
            }
            nodes.add(new DiagramActivityNode(copy.get(idx)));
            typeDiagrams.remove(copy.get(idx));
        }
        return nodes;
    }


    @Override
    public Graph domain() {
        return new Superobject(morphism.domain().getName().subTypeOf(name),morphism.domain(),name) {

            @Override
            protected Stream<Triple> inserts() {
                return DiagrammaticWorkflow.this.newElements.stream();
            }
        }.getResult();
    }

    @Override
    public Graph codomain() {
        return codomain.carrier();
    }

    public Optional<Name> map(Name name) {
        if (this.morphism.definedAt(name)) {
            return this.morphism.map(name);
        } else {
            if (this.newMappings.containsKey(name)) {
                return Optional.of(this.newMappings.get(name));
            } else {
                return Optional.empty();
            }
        }
    }

    @Override
    public Name getName() {
        return name;
    }


}
