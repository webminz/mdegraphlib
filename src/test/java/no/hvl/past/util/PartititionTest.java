package no.hvl.past.util;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;


import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PartititionTest {


    @Test
    public void testCollectAllSimple1() {
        HashSet<String> input = Sets.newHashSet("A", "B", "C", "D");
        PartitionAlgorithm<String> alg = new PartitionAlgorithm<>(input);
        alg.relate("A", "B");
        alg.relate("B", "C");
        alg.relate("C", "D");
        Set<Set<String>> result = alg.getResult();
        assertEquals(1, result.size());
        assertEquals(input, result.iterator().next());
    }
}
