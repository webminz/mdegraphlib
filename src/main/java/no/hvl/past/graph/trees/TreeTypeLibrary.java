package no.hvl.past.graph.trees;

import no.hvl.past.attributes.DataTypeDescription;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.predicates.DataTypePredicate;
import no.hvl.past.graph.predicates.TargetMultiplicity;
import no.hvl.past.names.Name;
import no.hvl.past.util.Multiplicity;

import java.util.Optional;

/**
 * Provides all the functionality, which is needed to label a tree structure with typing information.
 */
public interface TreeTypeLibrary {

    /**
     * Provides the type name of the root element.
     */
    Name rootTyping();

    /**
     * Provides the type information (if existent) for the current child element based on the type
     * of the current parent element and the label of the current branch (property).
     * The result is given as a {@link Triple} which simultaneously provides the type of the branch (edge label)
     * and the child (edge target).
     */
    Optional<Triple> childTyping(Name parentType, Name childBranchLabel);

    Optional<Multiplicity> branchMultiplicity(Name parentType, Name childBranchLabel);

    Optional<DataTypeDescription> branchDataType(Name parentType, Name childBranchLabel);


    static TreeTypeLibrary fromGraphForRootElement(String rootElement, Graph graph) {
        return new TreeTypeLibrary() {
            @Override
            public Name rootTyping() {
                return Name.identifier(rootElement);
            }

            @Override
            public Optional<Triple> childTyping(Name parentType, Name childBranchLabel) {
                if (parentType == null) {
                    return graph.get(childBranchLabel);
                } else {
                    return graph.get(childBranchLabel.prefixWith(parentType));
                }
            }

            @Override
            public Optional<Multiplicity> branchMultiplicity(Name parentType, Name childBranchLabel) {
                return Optional.empty();
            }

            @Override
            public Optional<DataTypeDescription> branchDataType(Name parentType, Name childBranchLabel) {
                return Optional.empty();
            }
        };
    }

    static TreeTypeLibrary fromSketchForRootElement(String rootElement, Sketch sketch) {
        return new TreeTypeLibrary() {
            @Override
            public Name rootTyping() {
                return Name.identifier(rootElement);
            }

            @Override
            public Optional<Triple> childTyping(Name parentType, Name childBranchLabel) {
                if (parentType == null) {
                    return sketch.carrier().get(childBranchLabel);
                } else {
                    return sketch.carrier().get(childBranchLabel.prefixWith(parentType));
                }
            }

            @Override
            public Optional<Multiplicity> branchMultiplicity(Name parentType, Name childBranchLabel) {
                return childTyping(parentType,childBranchLabel).flatMap(t ->
                        sketch.diagramsOn(t)
                                .filter(d -> d.label() instanceof TargetMultiplicity)
                                .map(d -> (TargetMultiplicity) d.label())
                                .findFirst()
                                .map(m -> m.multiplicity())
                );
            }

            @Override
            public Optional<DataTypeDescription> branchDataType(Name parentType, Name childBranchLabel) {
                return childTyping(parentType,childBranchLabel).flatMap(t ->
                        sketch.diagramsOn(Triple.node(t.getTarget()))
                                .filter(d -> d.label() instanceof DataTypePredicate)
                                .map(d -> (DataTypePredicate) d.label())
                                .findFirst()
                                .map(d -> d.description())
                );
            }
        };
    }


}
