package no.hvl.past.util;

import java.util.*;

public class URI {

    public static class URIPathElement {
        private final URIPathElement parent;
        private final String elementName;
        public URIPathElement(URIPathElement parent, String elementName) {
            this.parent = parent;
            this.elementName = elementName;
        }

        public String getFullPath() {
            return null; // TODO
        }

        public boolean isRoot() {
            return parent == null;
        }
    }

    public enum KnownShemes {
        HTTP, // Hypertext Transfer Protocol
        HTTPS, // Hypertext Transfer Protocol with Transport Layer Security
        EMAIL_TO, // E-mail address
        SSH, // Secure Shell
        URN, // Uniform Resource Name: https://tools.ietf.org/html/rfc8141
        FTP, // File Transfer Protocol
        SFTP, // Secure Shell File Transfer Protocol
        FILE, // Local File: https://tools.ietf.org/html/rfc8089
        IMAP, // Internet Message Access Protocol
        GEO, // Geoposition
        PLATFORM_BUNDLE, // Eclipse specific, access an element within a plugin-bundle
        PLATFORM_RESOURCE, // Eclipse specific, access an element within the workspace
    }

    private final String uriString;

    public URI(String uriString) {
        this.uriString = uriString;
    }

    public String getScheme() {
        return null; // TODO
    }

    public Optional<KnownShemes> getIdentifiedSchme() {
        return null; // TODO
    }

    public String getAuthority() {
        return null; // TODO
    }

    public List<URIPathElement> getPath() {
        return new ArrayList<>(); // TODO
    }

    public Map<String, String> getQuery() {
        return new HashMap<>(); // TODO
    }

    public String getFragment() {
        return null; // TODO
    }



}
