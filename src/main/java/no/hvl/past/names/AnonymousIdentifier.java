package no.hvl.past.names;

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
    public Optional<String> getAuthority() {
        return Optional.empty();
    }

    @Override
    public byte[] serialize() {
        byte[] result = new byte[4];
        int originalHashCode = System.identityHashCode(this);
        result[0] = (byte) originalHashCode;
        result[1] = (byte) (originalHashCode >> 8);
        result[2] = (byte) (originalHashCode >> 16);
        result[3] = (byte) (originalHashCode >> 24);
        return result;
    }
}
