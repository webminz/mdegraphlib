package no.hvl.past.techspace;

import no.hvl.past.plugin.ExtensionPoint;

/**
 * Represents some technological space (this can pretty much be everything)
 * that can be interpreted in the sketch framework.
 */
public interface TechSpace extends ExtensionPoint {

    /**
     * Every technological space should be uniquely identifiable by a string.
     */
    String ID();

}
