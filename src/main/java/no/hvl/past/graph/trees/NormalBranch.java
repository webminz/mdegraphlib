package no.hvl.past.graph.trees;

import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

public class NormalBranch implements Branch {

    private Name parent;
    private Name key;
    private Node child;
    private Long index;
    private Triple type;

    protected Node getChild() {
        return child;
    }

    protected Name getParent() {
        return parent;
    }


    public NormalBranch(Name parent, Name key, Node child, Long index, Triple type) {
        this.parent = parent;
        this.key = key;
        this.child = child;
        this.index = index;
        this.type = type;
    }

    protected void setParent(Name parent) {
        this.parent = parent;
    }

    protected void setIndex(Long index) {
        this.index = index;
    }

    protected void setType(Triple type) {
        this.type = type;
    }

    @Override
    public Optional<Triple> type() {
        return Optional.ofNullable(type);
    }

    @Override
    public Name parentNode() {
        return parent;
    }

    @Override
    public Name key() {
        return key;
    }

    @Override
    public Node child() {
        return child;
    }

    @Override
    public boolean isCollection() {
        return index != null;
    }

    @Override
    public long index() {
        return index == null ? -1 : index;
    }

}
