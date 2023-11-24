package no.hvl.past.names;

import no.hvl.past.util.ByteUtils;

import java.util.Optional;

/**
 * Represents an internal identifier, i.e. it is not visible from
 * the outside and thus two elements with internal identifiers
 * could only be distinguished from the outside via their context.
 * Anonymous identifiers are especially useful when performing
 * categorical constructions that freely create elements.
 * Because the system is not able to give a meaningful name then, it may just consider
 * give them an anonymous identifier.
 */
public class AnonymousIdentifier extends Identifier {


    @Override
    public String toString() {
        return "";
    }

    @Override
    public byte[] getValue() {
        int originalHashCode = System.identityHashCode(this);
        return ByteUtils.prefix(Name.ANONYMOUS_IDENTIFIER_BYTE, ByteUtils.intToByteArray(originalHashCode, true));
    }
}
