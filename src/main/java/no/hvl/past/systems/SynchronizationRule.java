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


    // TODO conditionals for when synchronizations rules should be enforced

    public SynchronizationRule(
            Name commonalityName,
            Set<ComprSys.QName> typeTuple,
            List<ComprSys.QName> propagationHierarchy) {
        this.commonalityName = commonalityName;
        this.typeTuple = typeTuple;
        this.propagationHierarchy = propagationHierarchy;
    }


    @Override
    public Name commonality() {
        return commonalityName;
    }

    @Override
    public Stream<Name> violations(ComprData instance) {
        if (isSymmetric()) {
            return checkSymmatric(instance);
        } else {
            return checkHierarchical(instance);
        }
    }

    private Stream<Name> checkHierarchical(ComprData instance) {
        return Stream.empty(); // TODO
    }

    private Stream<Name> checkSymmatric(ComprData instance) {
        Set<Name> violations = new HashSet<>();
        for (ComprSys.QName qName : typeTuple) {
            instance.all(qName.getElementName().prefixWith(qName.getContainer().schema().getName()))
                    .forEach(elId -> {
                        if (instance.getCommonalitiesOfType(qName.getContainer(), elId, commonalityName).anyMatch(com -> {
                            for (ComprSys.QName partnerQName : typeTuple) {
                                if (!partnerQName.equals(qName) && com.getRefs().stream().noneMatch(qRef -> qRef.getOrigin().equals(partnerQName.getContainer()))) {
                                    return true;
                                }
                            }
                            return false;
                        })) {
                            System.out.println("Synchronisation violation: " + elId + " has no partner for " + commonalityName);
                            violations.add(elId);
                        }

                    });
        }


        return violations.stream(); // TODO

        // has pre-calculated trace links


        // no pre-calculated trace links
        // for each type
            // if has keys
                // all instances
                    // at least one can be evaluated --> check that exactly partner exist for every other type
            // if no key (= unconditional) --> check that no of compatible types coincide || or --> require that there is a given trace model

    }


    private boolean isSymmetric() {
        return propagationHierarchy.isEmpty();
    }
}
