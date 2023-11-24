package no.hvl.past.names;


import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class URINames {

    @Test
    public void testHTTPURI() throws URISyntaxException {
        URIName uri = Name.uri("http://www.w3.org/2001/XMLSchema-instance");
        assertEquals("www.w3.org", uri.getAuthority());
        assertEquals("/2001/XMLSchema-instance", uri.getPath());
    }

    @Test
    public void testHTTPSURI() throws URISyntaxException {
        URIName uri = Name.uri("https://docs.gradle.org/6.8.2/userguide/command_line_interface.html#sec:command_line_warnings");
        assertEquals("docs.gradle.org", uri.getAuthority());
        assertEquals("docs.gradle.org", uri.getHost());
        assertEquals("/6.8.2/userguide/command_line_interface.html", uri.getPath());
        assertEquals("sec:command_line_warnings", uri.getFragment());
    }

    @Test
    public void testMailtoURI() throws URISyntaxException {
        URIName uri = Name.uri("mailto:past@hvl.no");
        assertEquals("past@hvl.no", uri.getSchemeSpecificPart());
    }

    @Test
    public void testFileURI() throws URISyntaxException {
        URIName uri = Name.uri("file://localhost/Users/past/home/Documents/aFile.txt");
        assertEquals("localhost", uri.getHost());
        assertEquals("/Users/past/home/Documents/aFile.txt", uri.getPath());
        assertEquals("localhost", uri.getAuthority());

    }

    @Test
    public void testSSHURI() throws URISyntaxException {
        URIName uri = Name.uri("ssh://past@192.168.0.23:12345/home/root");
        assertEquals("/home/root", uri.getPath());
        assertEquals(12345, uri.getPort());
        assertEquals("192.168.0.23", uri.getHost());
        assertEquals("past@192.168.0.23:12345", uri.getAuthority());
        assertEquals("past", uri.getUserInfo());
    }

    @Test
    public void testPlatformURI() throws URISyntaxException {
        URIName uri = Name.uri("platform:/resource/Families/model/Families.ecore");
        assertEquals("/resource/Families/model/Families.ecore", uri.getPath());
        assertEquals("/resource/Families/model/Families.ecore", uri.getSchemeSpecificPart());
    }

}
