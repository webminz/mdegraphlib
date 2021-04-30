package no.hvl.past.graph.trees;

public interface QueryBranch extends TypedBranch {

    QueryNode parent();

    @Override
    QueryNode child();

    boolean isProjection();

    boolean isSelection();

    interface Projection extends QueryBranch {
        @Override
        default boolean isProjection() {
            return true;
        }

        @Override
        default boolean isSelection() {
            return false;
        }
    }

    interface Selection extends QueryBranch { // TODO generic selection based on GraphDiagrams
        @Override
        default boolean isProjection() {
            return false;
        }

        @Override
        default boolean isSelection() {
            return true;
        }
    }



}
