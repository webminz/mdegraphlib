package no.hvl.past.names;

public class NameDeserializer {

    public NameSet deserialize(byte[] bytes) {
        NameSet result = new NameSet();
        byte magicByte = bytes[0];
        switch (magicByte) {
            case Name.ANONYMOUS_IDENTIFIER_BYTE:
                parseAnonymousId(result, bytes);
                break;
            case Name.URI_IDENTIFIER_BYTE:
                parseURIid(result, bytes);
                break;
            // TODO implement

            default:
                break;
        }

        return result;
    }

    private void parseURIid(NameSet result, byte[] bytes) {
        // TODO implement

    }

    private void parseAnonymousId(NameSet result, byte[] bytes) {
        // TODO implement

    }

}
