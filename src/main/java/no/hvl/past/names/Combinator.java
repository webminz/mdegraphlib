package no.hvl.past.names;


/**
 * Abstract superclass of all names combinators,
 * i.e. names that are produced by combining one or more
 * existing names in an operation.
 */
public abstract class Combinator extends Name {

    @Override
    public final boolean isDerived() {
        return true;
    }



}
