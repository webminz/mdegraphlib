package no.hvl.past.graph.trees;


/**
 * Query trees are special trees that are used to filter elements.
 */
public interface QueryTree  {

    /**
     * The root of the query.
     */
    QueryNode root();


    /**
     * Textual representation of the query.
     */
    String textualRepresentation();


}
