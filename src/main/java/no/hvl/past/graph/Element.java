package no.hvl.past.graph;

import no.hvl.past.names.Name;

/**
 * The abstract supertype of every element in the mdegraphlib framework.
 */
public interface Element {

    /**
     * Every element is uniquely identified by its name.
     * Uniquely means up to certain boundaries, via prefixing
     * it with names of the associated boundaries it can be
     * made globally unique.
     */
    Name getName();

    /**
     * Every element can be traversed by a visitor.
     */
    void accept(Visitor visitor);


    /**
     * Checks the internal structural validity of this element.
     */
    boolean verify();

}
