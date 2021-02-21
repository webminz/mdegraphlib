package no.hvl.past.graph;


import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A functional interface to perform consequent renaming of graph elements.
 */
public abstract class Isomorphism extends AbstractModification {

    public Isomorphism(Name isomorphismName, Graph base, Name resultGraphName) {
        super(isomorphismName, base, resultGraphName, true);
    }

    public Isomorphism(Name name, Graph base, Graph result) {
        super(name, base, result, true);
    }

    @Override
    public boolean isMonic() {
        return true;
    }

    @Override
    public boolean isEpic() {
        return true;
    }

    /**
     * Specifies how the renaming from domain to codomain should be performed.
     * If the element shall not be renamed, this function is the identity.
     */
    public abstract Name doRename(Name base);

    /**
     * Checks whether something has been renamed with this strategy.
     * It is used to possibly undo the rename.
     * If rename undoing is not supported this method can just return false.
     */
    public abstract boolean hasBeenRenamed(Name name);

    /**
     * Performs the undo of the renaming.
     */
    public abstract Name undoRename(Name renamed);


    @Override
    public Graph domain() {
        return getBase();
    }

    @Override
    public Graph codomain() {
        return getResult();
    }

    @Override
    public Optional<Name> map(Name name) {
        return Optional.of(doRename(name));
    }

    public boolean mentions(Name name) {
        if (hasBeenRenamed(name)) {
            return getBase().mentions(undoRename(name));
        } else {
            return false;
        }
    }

    @Override
    public Optional<Triple> apply(Triple from) {
        if (getBase().contains(from)) {
            return super.apply(from);
        }
        return Optional.empty();
    }

    @Override
    public Stream<Triple> elements() {
        return getBase().elements().map(this::rename);
    }

    @Override
    public boolean contains(Triple triple) {
        return getBase().contains(undoRename(triple));
    }

    private Triple rename(Triple triple) {
        return new Triple(doRename(triple.getSource()), doRename(triple.getLabel()),doRename(triple.getTarget()));
    }

    private boolean hasBeenRenamed(Triple triple) {
        return hasBeenRenamed(triple.getSource()) && hasBeenRenamed(triple.getLabel()) && hasBeenRenamed(triple.getTarget());
    }

    private Triple undoRename(Triple triple) {
        return new Triple(undoRename(triple.getSource()), undoRename(triple.getLabel()), undoRename(triple.getTarget()));
    }

//    private boolean containsStrict(Predicate<Name> contains, Name element) {
//        if (strategy.hasBeenRenamed(element)) {
//            return contains.test(strategy.undoRename(element));
//        } else {
//            return false;
//        }
//    }
//
//    private boolean containsLax(Predicate<Name> contains, Name element) {
//        if (strategy.hasBeenRenamed(element)) {
//            return contains.test(strategy.undoRename(element));
//        } else {
//            return contains.test(element);
//        }
//    }
//
//    private boolean containsStrict(Predicate<Triple> contains, Triple triple) {
//        if (strategy.hasBeenRenamed(triple.getSource()) && strategy.hasBeenRenamed(triple.getLabel()) && strategy.hasBeenRenamed(triple.getTarget())) {
//            return contains.test(new Triple(strategy.undoRename(triple.getSource()), strategy.undoRename(triple.getLabel()), strategy.undoRename(triple.getTarget())));
//        } else {
//            return false;
//        }
//    }
//
//    private boolean containsLax(Predicate<Triple> contains, Triple triple) {
//        if (strategy.hasBeenRenamed(triple.getSource()) && strategy.hasBeenRenamed(triple.getLabel()) && strategy.hasBeenRenamed(triple.getTarget())) {
//            return contains.test(new Triple(strategy.undoRename(triple.getSource()), strategy.undoRename(triple.getLabel()), strategy.undoRename(triple.getTarget())));
//        } else {
//            return contains.test(triple);
//        }
//    }
//
//    private Iterator<Triple> iterator(Iterator<Triple> base, Predicate<Triple> deleteIf) {
//        return new Iterator<Triple>() {
//
//            Triple lookahead = getNext();
//
//            private Triple getNext() {
//                if (base.hasNext()) {
//                    Triple n = base.next();
//                    if (deleteIf.test(n)) {
//                        return getNext();
//                    }
//                    return rename(n);
//                }
//                return null;
//            }
//
//            @Override
//            public boolean hasNext() {
//                return lookahead != null;
//            }
//
//            @Override
//            public Triple next() {
//                Triple result = lookahead;
//                lookahead = getNext();
//                return result;
//            }
//        };
//    }
//
//    private Optional<Triple> filter(Name in, Function<Name,Optional<Triple>> function) {
//        if (strategy.hasBeenRenamed(in)) {
//            return function.apply(strategy.undoRename(in));
//        } else {
//            return Optional.empty();
//        }
//    }

}
