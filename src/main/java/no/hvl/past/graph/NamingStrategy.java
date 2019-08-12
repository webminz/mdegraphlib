package no.hvl.past.graph;

import no.hvl.past.graph.names.Merge;
import no.hvl.past.graph.names.Name;
import no.hvl.past.graph.names.Prefix;
import no.hvl.past.graph.names.PrintingStrategy;


import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
public interface NamingStrategy {

    Name name(List<Name> toRename);

    NamingStrategy LEAVE_UNCHANGED = new NamingStrategy() {
        @Override
        public Name name(List<Name> toRename) {
            return new Merge(toRename);
        }
    };

    static NamingStrategy concatenate(PrintingStrategy printingStrategy) {
        return new NamingStrategy() {
            @Override
            public Name name(List<Name> toRename) {
                return Name.identifier(printingStrategy.merge(toRename.stream().map(p -> p.print(printingStrategy)).collect(Collectors.toList())));
            }
        };
    }

    static NamingStrategy givePrecedenceToPrefix(Name prefix) {
        return new NamingStrategy() {
            @Override
            public Name name(List<Name> toRename) {
                return toRename.stream()
                        .filter(n -> n.getPrefix().isPresent())
                        .filter(n -> n.getPrefix().get().equals(prefix))
                        .findFirst()
                        .map(p -> p.unprefix(prefix)).orElse(LEAVE_UNCHANGED.name(toRename));
            }

        };
    }


}
