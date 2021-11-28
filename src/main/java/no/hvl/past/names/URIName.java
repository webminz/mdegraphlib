package no.hvl.past.names;

import jdk.nashorn.internal.runtime.options.Option;
import no.hvl.past.names.Identifier;
import no.hvl.past.names.PrintingStrategy;
import no.hvl.past.util.ByteUtils;
import no.hvl.past.util.ShouldNotHappenException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
public class URIName extends Identifier {

    /**
     * The regex String that was proposed to parse URIs in rfc2396.
     */
    private static final String URI_REGEX = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?";


    private final String uriValue;

    private URI uriObject;

    public URIName(String uriValue) {
        this.uriValue = uriValue;
    }

    public URI asURIObject() {
        if (uriObject == null) {
            try {
                this.uriObject = new URI(uriValue);
            } catch (URISyntaxException e) {
                throw new ShouldNotHappenException(URIName.class, e);
            }
        }
        return uriObject;
    }

    public URL asURLObject() throws MalformedURLException {
        return asURIObject().toURL();
    }

    public int getPort() {
        return asURIObject().getPort();
    }

    public String getAuthority() {
        return asURIObject().getAuthority();
    }

    public String getQuery() {
        return asURIObject().getQuery();
    }

    public String getFragment() {
        return asURIObject().getFragment();
    }

    public String getHost() {
        return asURIObject().getHost();
    }

    public String getPath() {
        return asURIObject().getPath();
    }

    public String getUserInfo() {
        return asURIObject().getUserInfo();
    }

    public String getSchemeSpecificPart() {
        return asURIObject().getSchemeSpecificPart();
    }

    @Override
    public byte[] getValue() {
        return ByteUtils.prefix(Name.URI_IDENTIFIER_BYTE, uriValue.getBytes(StandardCharsets.UTF_8));
    }


}
