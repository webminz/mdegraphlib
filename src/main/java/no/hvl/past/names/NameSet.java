package no.hvl.past.names;

import com.google.common.collect.Sets;
import no.hvl.past.logic.Model;
import no.hvl.past.logic.Signature;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class NameSet implements Signature, Model<NameSet> {

    private final Set<Name> names;

    public NameSet(Collection<Name> names) {
        this.names = new HashSet<>(names);
    }

    public NameSet(Name... names) {
        this.names = Sets.newHashSet(names);
    }

    public boolean contains(Name name) {
        return names.contains(name);
    }

    public boolean subsetOf(NameSet nameSet) {
        for (Name name : names) {
            if (!nameSet.contains(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isSyntacticallyCorrect(Model<? extends Signature> model) {
        if (model instanceof NameSet) {
            NameSet instance = (NameSet) model;
            return instance.subsetOf(this);
        }
        return false;
    }

    public Name toName() {
        return Name.merge(names.stream().sorted((n1, n2) -> {
            return Integer.compare(Arrays.hashCode(n1.getValue()), Arrays.hashCode(n2.getValue()));
        }).collect(Collectors.toList()));
    }
}
