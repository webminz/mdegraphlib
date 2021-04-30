package no.hvl.past.systems;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import no.hvl.past.keys.Key;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class ComprData implements Data {

    public static final class Builder {
        private final Multimap<QRef, Commonality> participations = HashMultimap.create();
        private final Multimap<Name, Commonality> commonalitiesKeys = HashMultimap.create();
        private final Map<Name, Data> distributedData = new LinkedHashMap<>();
        private final ComprSys comprSys;

        public Builder(ComprSys comprSys) {
            this.comprSys = comprSys;
        }

        public Builder addCommonality(Commonality commonality) {
            Commonality copy = getOrCreateCommonality(commonality.commonalityType, commonality.getName());
            for (QRef ref : commonality.refs) {
                copy.refs.add(ref);
                participations.put(ref, copy);
            }
            return this;
        }

        public Builder addDataSource(Data data) {
            distributedData.put(data.origin().schema().getName(), data);
            comprSys.relationKeys().forEach(k -> {
                if (k.sourceSystem().equals(data.origin())) {
                    // TODO can make it faster by evaluating all keys on the same type simultaneously!

                    data.evaluate(k).forEach(pair -> {
                        boolean typeCorrect = comprSys.projection(data.origin(), k.targetType())
                                .map(tt -> tt.equals(data.typeOf(pair.getRight())))
                                .orElse(false);
                        if (typeCorrect) {
                            Commonality commonality = getOrCreateCommonality(k.targetType(), pair.getFirst());
                            QRef qRef = new QRef(data.origin(), pair.getSecond());
                            commonality.getRefs().add(qRef);
                            participations.put(qRef, commonality);
                        }

                    });
                }
            });

            return this;
        }

        private Commonality getOrCreateCommonality(Name targetType, Name key) {
            Collection<Commonality> commonalities = commonalitiesKeys.get(key);
            if (commonalities.isEmpty()) {
                return makeNew(targetType, key);
            } else if (commonalities.size() == 1) {
                Commonality next = commonalities.iterator().next();
                if (next.commonalityType.equals(targetType)) {
                    return next;
                } else {
                    return makeNew(targetType, key);
                }
            } else {
                Iterator<Commonality> iterator = commonalities.iterator();
                while (iterator.hasNext()) {
                    Commonality next = iterator.next();
                    if (next.commonalityType.equals(targetType)) {
                        return next;
                    }
                }
                return makeNew(targetType, key);
            }
        }

        @NotNull
        private Commonality makeNew(Name targetType, Name key) {
            Commonality commonality = new Commonality(key, targetType);
            commonalitiesKeys.put(key, commonality);
            return commonality;
        }

        public ComprData build() {
            return new ComprData(participations, new ArrayList<>(commonalitiesKeys.values()), distributedData, comprSys);
        }

    }



    public static final class QRef {
        private final Sys origin;
        private final Name elementId;

        public QRef(Sys origin, Name elementId) {
            this.origin = origin;
            this.elementId = elementId;
        }

        public Sys getOrigin() {
            return origin;
        }

        public Name getElementId() {
            return elementId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            QRef qRef = (QRef) o;
            return origin.equals(qRef.origin) && elementId.equals(qRef.elementId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(origin, elementId);
        }
    }

    // TODO for identification, we need something similar called cluster that aggregates everything together

    public static final class Commonality {

        private final Name name;
        private final Name commonalityType;
        private final Set<QRef> refs;

        public Commonality(Name name, Name commonalityType) {
            this.name = name;
            this.commonalityType = commonalityType;
            this.refs = new HashSet<>();
        }

        public Name getName() {
            return name;
        }

        public Name getCommonalityType() {
            return commonalityType;
        }

        public Set<QRef> getRefs() {
            return refs;
        }
    }

    // Maybe outsource to an effective KV-based NoSQL database if we have to handle really big instances.

    private final Multimap<QRef, Commonality> participations;
    private final List<Commonality> commonalities;
    private final Map<Name, Data> distributedData;
    private final ComprSys comprSys;

    public ComprData(Multimap<QRef, Commonality> participations,
                     List<Commonality> commonalities,
                     Map<Name, Data> distributedData,
                     ComprSys comprSys) {
        this.participations = participations;
        this.commonalities = commonalities;
        this.distributedData = distributedData;
        this.comprSys = comprSys;
    }


    @Override
    public Sys origin() {
        return comprSys;
    }

    @Override
    public Stream<Name> all(Name type) {
        if (comprSys.isMerged(type)) {
            return Stream.empty(); // TODO do correctly
        }
        if (distributedData.containsKey(type.firstPart())) {
            return distributedData.get(type.firstPart()).all(type.unprefixTop());
        }
        return commonalities.stream().filter(c -> c.commonalityType.equals(type.unprefixTop())).map(Commonality::getName);
    }

    @Override
    public Stream<Name> properties(Name elementId, Name propertyName) {
        if (comprSys.isMerged(propertyName)) {
            return Stream.empty(); // TODO do correctly
        }
        if (distributedData.containsKey(propertyName.firstPart())) {
            return distributedData.get(propertyName.firstPart()).properties(elementId, propertyName.unprefixTop());
        }
        if (propertyName.unprefixAll().isProjection()) {
            Name src = propertyName.unprefixAll().firstPart();
            Name trg = propertyName.unprefixAll().secondPart();
            return commonalities.stream().filter(com -> com.commonalityType.equals(src)).flatMap(com -> com.refs.stream().filter(qref -> qref.origin.schema().getName().equals(trg)).map(QRef::getElementId));
        }
        return Stream.empty();
    }

    @Override
    public Stream<Pair<Name, Name>> evaluate(Key k) {
        return Stream.empty(); // TODO do correctly
    }

    @Override
    public Name typeOf(Name element) {
        for (Commonality c : commonalities) {
            if (c.name.equals(element)) {
                return c.commonalityType;
            }
        }
        for (Data d : distributedData.values()) {
            Name name = d.typeOf(element);
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    public Stream<Commonality> getCommonalities(Sys origin, Name elementId) {
        return participations.get(new QRef(origin, elementId)).stream();
    }

    public Stream<Commonality> getCommonalitiesOfType(Sys origin, Name elementId, Name commonalitiesType) {
        return getCommonalities(origin, elementId).filter(com -> commonalitiesType.equals(com.commonalityType));
    }

    public Stream<Name> getRelatedElementsInSysForCommonality(Sys origin, Name elementId, Name commonalitiesType, Sys destination) {
        return getCommonalitiesOfType(origin, elementId, commonalitiesType).flatMap(com -> com.getRefs().stream().filter(r -> r.origin.equals(destination)).map(QRef::getElementId));
    }



}
