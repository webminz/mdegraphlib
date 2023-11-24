package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.names.NamePath;
import no.hvl.past.util.Multiplicity;
import no.hvl.past.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class TreeValidator implements TreeReceiver {

    public TreeValidator(TreeValidator parent, Name branchTag) {
        this.parent = parent;
        this.branchTag = branchTag;
    }

    private final TreeValidator parent;
    private final Name branchTag;
    private final List<Consumer<Pair<NamePath, Exception>>> errorSinks = new ArrayList<>();

    private int counter = 0;
    private int depth = 0;
    private boolean isActive = true;

    protected List<Consumer<Pair<NamePath, Exception>>> getErrorSinks() {
        return errorSinks;
    }

    public void registerErrorSink(Consumer<Pair<NamePath, Exception>> errorSink) {
        this.errorSinks.add(errorSink);
    }

    protected NamePath getNamePath() {
        Name n = branchTag.index(counter);
        if (parent != null) {
            return parent.getNamePath().addChild(n);
        } else {
            return new NamePath(Collections.singletonList(n));
        }
    }

    public int getCounter() {
        return counter;
    }


    @Override
    public void startTree(Name treeName)  {

    }

    @Override
    public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary)  {

    }

    @Override
    public void startRoot(Name rootName)  {

    }

    @Override
    public void startBranch(Name key, boolean isCollection) {
        if (!isActive && key.equals(branchTag)) {
            depth++;
            isActive = true;
        }
        if (isActive) {
            checkStartBranch(key, isCollection);
        }
    }

    protected void checkStartBranch(Name key, boolean isCollection) {

    }

    @Override
    public void branchType(Triple type) {
        if (isActive) {
            checkBranchType(type);
        }

    }

    protected void checkBranchType(Triple type) {
    }

    @Override
    public void endBranch()  {


        if (isActive && depth == 0) {
            watchEnded();
            depth--;
            isActive = false;
            counter = 0;
        }
    }

    protected void watchEnded() {
    }

    @Override
    public void startComplexNode()  {
        if (isActive) {
            if (depth == 0) {
                counter++;
            }
            depth++;
            checkStartComplexNode();
        }
    }

    protected void checkStartComplexNode() {
    }

    @Override
    public void nodeId(Name nodeId)  {

    }

    @Override
    public void nodeType(Name type)  {
        if (isActive) {
            checkNodeType(type);
        }
    }

    protected void checkNodeType(Name type) {

    }

    @Override
    public void endComplexNode() {
        if (isActive) {
            checkEndComplexNode();
            depth--;
        }
    }

    protected void checkEndComplexNode() {
    }

    @Override
    public void emptyLeaf() {
        if (isActive) {
            checkEmptyLeaf();
        }
    }

    protected void checkEmptyLeaf() {
    }

    @Override
    public void valueLeaf(Name value)  {
        if (isActive) {
            if (depth == 0) {
                counter++;
            }
            checkValueLeaf(value);
        }

    }

    protected void checkValueLeaf(Name value) {
    }

    @Override
    public void endRoot()  {

    }

    @Override
    public void endTree() {

    }


    public static class MultiplicityValidator extends TreeValidator {

        private final Multiplicity multiplicity;

        public MultiplicityValidator(TreeValidator parent,
                                     Name branchTag,
                                     Multiplicity multiplicity) {
            super(parent, branchTag);
            this.multiplicity = multiplicity;
        }

        @Override
        protected void watchEnded() {
            if (!this.multiplicity.isValid(getCounter())) {
                for (Consumer<Pair<NamePath, Exception>> errorSink : getErrorSinks()) {
                    errorSink.accept(new Pair<>(getNamePath(), new Multiplicity.MultiplicityViolation(multiplicity, getCounter())));
                }
            }
        }
    }

    // TODO public static class ObjectValidator extends TreeValidator {
        // mandatory fields
        // optional fields
    //}

    // TODO public static class ValueValidator extends TreeValidator {
        // attribute predicate
    //}
}
