package no.hvl.past.names;

import jdk.nashorn.internal.runtime.options.Option;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.PrintingStrategy;

import java.util.Map;
import java.util.Optional;

/**
 * A custom implementation Uniform Resource Identificators according to RFC 2396.
 * The Eclipse Modeling framework makes heavy use of them (for element identification).
 *
 * A URI consists of five parts:
 *  - a schema (optional) in the beginning
 *  - an authority (optional) after the double colon and a double slash
 *  - a path after a slash
 *  - a query (optional) after a question mark
 *  - a fragment (optional) after a hash mark
 *
 */
public class URI extends Identifier {

    /**
     * The regex String that was proposed to parse URIs in rfc2396.
     */
    private static final String URI_REGEX = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";

    private static final String EMF_SPEC = "http://www.eclipse.org/emf/2002/Ecore";
    private static final String XMI_SPEC = "http://www.omg.org/XMI";
    private static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

    public enum URIScheme {
        HTTP {
            @Override
            public boolean isGlobal() {
                return true;
            }
        },
        HTTPS {
            @Override
            public boolean isGlobal() {
                return true;
            }
        },
        FILE {
            @Override
            public boolean isGlobal() {
                return false;
            }
        },
        MAILTO {
            @Override
            public boolean isGlobal() {
                return true;
            }
        },
        PLATFORM {
            @Override
            public boolean isGlobal() {
                return false;
            }
        };

        public abstract boolean isGlobal();
    }

    public static class URIFragment {

        private final String value;

        public URIFragment(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public boolean isRoot() {
            return value.startsWith("//");
        }

        public URIFragment getParent() {
            return new URIFragment(value.substring(0, value.lastIndexOf('/')));
        }

        public String getNodeValue() {
            return value.substring(value.lastIndexOf('/'));
        }
    }

    public static class URIQuery {

        private final Map<String, String> paramMap;

        public URIQuery(Map<String, String> paramMap) {
            this.paramMap = paramMap;
        }
    }

    private final String uriValue;

    public URI(String uriValue) {
        this.uriValue = uriValue;
    }

    /**
     * The scheme part of a URI usually refers to a protocol being used to retrieve a respective resource
     */
    public Option<URIScheme> getScheme() {
        return null; // TODO
    }

    /**
     * The optional authority part contains a globally unique identifier of an entity
     * responsible for the respective resource.
     * Often an IP-Address or DNS hostname.
     */
    public Optional<String> getAuthority() {
        return null; // TODO
    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }

    /**
     * The only mandatory part of a URI.
     * A path is a hierarchy of names that in a certain context uniquely refer to a resource.
     */
    public String getPath() {
        return null; // TODO
    }

    public Option<URIQuery> getQuery() {
        return null; // TODO
    }

    public Option<URIFragment> getFragment() {
        return null; // TODO
    }

}
