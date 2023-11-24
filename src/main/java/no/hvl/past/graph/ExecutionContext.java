package no.hvl.past.graph;

import no.hvl.past.names.Name;

import java.util.Properties;
import java.util.Random;

/**
 * Execution context for the application of an operation.
 * To make executions traceable and reproducable,
 * all kinds of information provided through the
 * environment (names of new elements, system time etc.)
 * must be provided through this interface.
 */
public interface ExecutionContext {

    /**
     * Provides the name of a new node, which is going to be created.
     */
    Name generateNewNodeName();

    /**
     * Provides the name for a new edge, which is going to be created.
     */
    Name generateNewEdgeLabel();

    /**
     * Provides a java random seed.
     */
    Random randomGenerator();

    /**
     * The system time (epoch, i.e. milliseconds since 1970-01-01)
     * at the time of execution.
     */
    long systemTime();

    /**
     * A key-value map with string-only entries to encode some arbitrary meta-information.
     */
    Properties metaInformation();

    /**
     * The universe of things around.
     */
    Universe universe();
}
