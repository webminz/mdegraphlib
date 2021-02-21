package no.hvl.past.graph.trees;

import no.hvl.past.names.Name;

public class TreeImpl implements Tree {

    private final Name treeName;
    private final Node root;

    public TreeImpl(Name treeName, Node root) {
        this.treeName = treeName;
        this.root = root;
    }


    @Override
    public Node root() {
        return root;
    }

    @Override
    public Name getName() {
        return treeName;
    }
}
