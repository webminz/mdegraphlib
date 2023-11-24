package no.hvl.past.graph.trees;

import no.hvl.past.names.Name;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class NormalNode implements Node {

    private Name elementName;
    private Name parentNodeName;
    private Name typeName;
    private Boolean isComplex;
    private final List<Branch> children;

    NormalNode(Name element) {
        this.elementName = element;
        this.children = new ArrayList<>();
    }

    public void addBranch(Branch branch) {
        this.children.add(branch);
    }

    protected void setElementName(Name elementName) {
        this.elementName = elementName;
    }

    protected void setParentNodeName(Name parentNodeName) {
        this.parentNodeName = parentNodeName;
    }

    protected void setTypeName(Name typeName) {
        this.typeName = typeName;
    }

    protected void setComplex(Boolean complex) {
        isComplex = complex;
    }

    protected List<Branch> getChildren() {
        return children;
    }

    @Override
    public Name elementName() {
        return elementName;
    }

    @Override
    public Optional<Name> nodeType() {
        return Optional.ofNullable(typeName);
    }

    @Override
    public Stream<Branch> children() {
        return children.stream();
    }

    @Override
    public Optional<Name> parentNode() {
        return Optional.ofNullable(parentNodeName);
    }

    @Override
    public boolean isComplex() {
        return Optional.ofNullable(isComplex).orElseGet(Node.super::isComplex);
    }
}
