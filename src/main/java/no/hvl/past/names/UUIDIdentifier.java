package no.hvl.past.names;

import com.fasterxml.uuid.Generators;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

public class UUIDIdentifier extends Identifier {

    public enum Version {

        /**
         * Time based
         */
        VERSION_1,

        /**
         * Time based (DCE security).
         * Not supported!
         */
        VERSION_2,

        /**
         * Namespace based, using MD5 as hash algorithm.
         */
        VERSION_3,

        /**
         * Random
         */
        VERSION_4,

        /**
         * Namespace based, using SHA-1 as hash algorithm.
         */
        VERSION_5

    }

    private final UUID uuid;

    private UUIDIdentifier(UUID uuid) {
        this.uuid = uuid;
    }


    @Override
    public byte[] getValue() {
        byte[] result = new byte[17];
        result[0] = Name.UUID_IDENTIFIER_BYTE;
        result[1] = (byte) (uuid.getMostSignificantBits() >> 56);
        result[2] = (byte) (uuid.getMostSignificantBits() >> 48);
        result[3] = (byte) (uuid.getMostSignificantBits() >> 40);
        result[4] = (byte) (uuid.getMostSignificantBits() >> 32);
        result[5] = (byte) (uuid.getMostSignificantBits() >> 24);
        result[6] = (byte) (uuid.getMostSignificantBits() >> 16);
        result[7] = (byte) (uuid.getMostSignificantBits() >> 8);
        result[8] = (byte) (uuid.getMostSignificantBits());
        result[9] = (byte) (uuid.getLeastSignificantBits() >> 56);
        result[10] = (byte) (uuid.getLeastSignificantBits() >> 48);
        result[11] = (byte) (uuid.getLeastSignificantBits() >> 40);
        result[12] = (byte) (uuid.getLeastSignificantBits() >> 32);
        result[13] = (byte) (uuid.getLeastSignificantBits() >> 24);
        result[14] = (byte) (uuid.getLeastSignificantBits() >> 16);
        result[15] = (byte) (uuid.getLeastSignificantBits() >> 8);
        result[16] = (byte) (uuid.getLeastSignificantBits());
        return result;
    }

    public Version getVersion() {
        switch (this.uuid.version()) {
            case 1:
                return Version.VERSION_1;
            case 2:
                return Version.VERSION_2;
            case 3:
                return Version.VERSION_3;
            case 4:
                return Version.VERSION_4;
            case 5:
                return Version.VERSION_5;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return this.uuid.toString();
    }

    static UUIDIdentifier createRandom() {
        return new UUIDIdentifier(UUID.randomUUID());
    }

    static UUIDIdentifier createTimeBased() {
        return new UUIDIdentifier(Generators.timeBasedGenerator().generate());
    }

    static UUIDIdentifier createNamespaceBased(UUIDIdentifier namespace, byte[] value) {
        try {
            return new UUIDIdentifier(Generators.nameBasedGenerator(namespace.uuid, MessageDigest.getInstance("SHA-1")).generate(value));
        } catch (NoSuchAlgorithmException e) {
            // Should not happen!
            e.printStackTrace();
            return null;
        }
    }

}
