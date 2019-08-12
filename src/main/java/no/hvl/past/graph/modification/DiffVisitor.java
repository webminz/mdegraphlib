package no.hvl.past.graph.modification;

public interface DiffVisitor {

    void handle(InsertDiff insertDiff);

    void handle(DeleteDiff deleteDiff);

    void handle(SplitDiff splitDiff);

    void handle(MergeDiff mergeDiff);
}
