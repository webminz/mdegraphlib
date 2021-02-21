package no.hvl.past.util;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

public class IOStreamTests {

    @Test
    public void testWritingAndReading() throws IOException {
        assertEquals("Hello, World", IOStreamUtils.outputStreamClosure(outputStream -> {
            IOStreamUtils.copyOver(IOStreamUtils.stringAsInputStream("Hello, World"), outputStream);
        }));
    }


}
