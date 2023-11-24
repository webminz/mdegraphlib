package no.hvl.past.util;


import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class IOStreamTests {

    @Test
    public void testWritingAndReading() throws IOException {
        assertEquals("Hello, World", IOStreamUtils.outputStreamClosure(outputStream -> {
            IOStreamUtils.copyOver(IOStreamUtils.stringAsInputStream("Hello, World"), outputStream);
        }));
    }


}
