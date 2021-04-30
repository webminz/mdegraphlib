package no.hvl.past.techspace;

import no.hvl.past.names.Name;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Tech space directives allow to specify implicit
 * rules for aligning multiple models of the same tech-space
 */
public interface TechSpaceDirective {

    // TODO maybe the default data types must be changed into a collection ?!

    /**
     * The name of the string data type in this technology (if exists).
     */
    Optional<Name> stringDataType();

    /**
     * The name of the bool data type in this technology (if exists).
     */
    Optional<Name> boolDataType();

    /**
     * The name of the integer data type in this technology (if exists).
     */
    Optional<Name> integerDataType();

    /**
     * The name of the floating point number data type in this technology (if exists).
     */
    Optional<Name> floatingPointDataType();

    /**
     * More names of types that always should be identified by their name.
     */
    Stream<Name> implicitTypeIdentities();

}
