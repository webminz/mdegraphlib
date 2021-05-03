package no.hvl.past.systems;

import com.google.common.collect.Multimap;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.keys.Key;
import no.hvl.past.names.Name;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SynchronizationRule implements ConsistencyRule {

    private final Name commonalityName;
    private final Set<ComprSys.QName> typeTuple;
    private final List<ComprSys.QName> propagationHierarchy;
    private final boolean isUnconditional;


    // TODO conditionals for when synchronizations rules should be enforced

    public SynchronizationRule(
            Name commonalityName,
            Set<ComprSys.QName> typeTuple,
            List<ComprSys.QName> propagationHierarchy) {
        this.commonalityName = commonalityName;
        this.typeTuple = typeTuple;
        this.propagationHierarchy = propagationHierarchy;
        this.isUnconditional = false;
    }


    @Override
    public Name commonality() {
        return commonalityName;
    }

    @Override
    public Stream<Name> violations(ComprData instance) {
        if (isUnconditional) {
            return doUnconditionalCheck(instance);
        } else {
            return doConditionalCheck(instance);
        }

    }

    private Stream<Name> doConditionalCheck(ComprData instance) {
        return instance.getCommonalities().iterate(commonalityName).filter(commonality -> {
            if (propagationHierarchy.isEmpty()) {
                for (ComprSys.QName typeQName : typeTuple) {
                    if (commonality.getProjections().stream().noneMatch(qualifiedName -> qualifiedName.getSystem().equals(Name.identifier(typeQName.getContainer().url())))) {
                        return true;
                    }
                }
            } else {
                return false; // TODO hierarchy check
            }
            return false;
        }).flatMap(commonality -> {
            return commonality.getProjections().stream().map(QualifiedName::getElement);
        });
    }

    private Stream<Name> doUnconditionalCheck(ComprData instance) {
        return Stream.empty(); // TODO
    }


}
