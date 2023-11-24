package no.hvl.past.util;


import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class StreamExtensionsTest {

    @Test
    public void testUniquenessCheck() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        assertTrue(StreamExt.isUnique(Stream.of(o1, o2, o3)));
        assertFalse(StreamExt.isUnique(Stream.of(o1, o2, o1, o3)));

    }
}
