package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.io.IOException;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;


public interface TreeElementNamingStrategy extends TreeReceiver {

    Optional<Name> proposeName();

    void nameAccepted(Name elementName);


    class AnonymousNames implements TreeElementNamingStrategy {

        @Override
        public void startTree(Name treeName) {
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {
        }

        @Override
        public Optional<Name> proposeName() {
            return Optional.of(Name.anonymousIdentifier());
        }

        @Override
        public void nameAccepted(Name elementName) {

        }

        @Override
        public void startRoot(Name id) {

        }

        @Override
        public void nodeId(Name id)  {

        }

        @Override
        public void nodeType(Name type) {

        }

        @Override
        public void startBranch(Name key, boolean isCollection) {

        }

        @Override
        public void branchType(Triple type) {

        }

        @Override
        public void endBranch() {

        }

        @Override
        public void startComplexNode() {

        }

        @Override
        public void endComplexNode() {

        }

        @Override
        public void emptyLeaf() {

        }

        @Override
        public void valueLeaf(Name value) {

        }

        @Override
        public void endRoot() {

        }

        @Override
        public void endTree() {

        }
    }


    class AttributeBased implements TreeElementNamingStrategy {

        private Name current;
        private final Name attributeName;
        private boolean active = false;

        public AttributeBased(Name attributeName) {
            this.attributeName = attributeName;
        }


        @Override
        public void startTree(Name treeName) throws Exception {
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {

        }

        @Override
        public Optional<Name> proposeName() {
            return Optional.ofNullable(current);
        }

        @Override
        public void nameAccepted(Name elementName) {
            this.current = null;
        }

        @Override
        public void startRoot(Name rootName) {
        }

        @Override
        public void nodeId(Name id) throws IOException {

        }

        @Override
        public void nodeType(Name type) throws IOException {

        }

        @Override
        public void startBranch(Name key, boolean isCollection) {
            if (!isCollection && key.equals(attributeName)) {
                this.active = true;
            }
        }

        @Override
        public void endBranch() {
            this.active = false;
        }


        @Override
        public void branchType(Triple type) {

        }

        @Override
        public void startComplexNode() {

        }

        @Override
        public void endComplexNode() {

        }

        @Override
        public void emptyLeaf() {

        }

        @Override
        public void valueLeaf(Name value) {
            if (active) {
                this.current = value;
            }
        }

        @Override
        public void endRoot() {

        }

        @Override
        public void endTree() {

        }
    }

    class ParentBased implements TreeElementNamingStrategy {

        private final BiFunction<Name, Name, Name> combinator;
        private final Stack<Name> keyStack = new Stack<>();
        private final Stack<Integer> idxStack = new Stack<>();
        private final Stack<Name> parentStack = new Stack<>();

        public ParentBased(BiFunction<Name, Name, Name> combinator) {
            this.combinator = combinator;
        }


        @Override
        public void startTree(Name treeName) throws Exception {
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {
        }

        @Override
        public Optional<Name> proposeName() {
            if (parentStack.size() == 1 && keyStack.empty()) {
                return Optional.of(parentStack.peek());
            } else if (!keyStack.empty() && !parentStack.empty()) {
                Name n = keyStack.peek();
                if (idxStack.peek() >= 0) {
                    n =  n.index(idxStack.peek());
                }
                return Optional.of(combinator.apply(parentStack.peek(), n));
            } else {
                return Optional.empty();
            }

        }

        @Override
        public void nameAccepted(Name elementName) {
            this.parentStack.push(elementName);
        }

        @Override
        public void startRoot(Name rootName) {
            this.parentStack.push(rootName);
        }

        @Override
        public void nodeId(Name id) {
            if (!this.parentStack.isEmpty()) {

                this.parentStack.pop();
                this.parentStack.push(id);
            }

        }

        @Override
        public void nodeType(Name type) {

        }

        @Override
        public void startBranch(Name key, boolean isCollection) {
            keyStack.push(key);
            idxStack.push(isCollection ? 0 : -1);
        }

        @Override
        public void branchType(Triple type) throws Exception {
        }

        @Override
        public void endBranch() {
            keyStack.pop();
            idxStack.pop();
        }

        @Override
        public void startComplexNode() {
            if (!idxStack.empty()) {
                if (idxStack.peek() >= 0) {
                    int idx = idxStack.pop();
                    idx++;
                    idxStack.push(idx);
                }
            }
        }

        @Override
        public void endComplexNode() {
            if (!parentStack.empty()) {
                this.parentStack.pop();
            }
        }

        @Override
        public void emptyLeaf() {

        }

        @Override
        public void valueLeaf(Name value) {
            if (idxStack.peek() >= 0) {
                int idx = idxStack.pop();
                idx++;
                idxStack.push(idx);
            }
        }

        @Override
        public void endRoot() {

        }

        @Override
        public void endTree() {

        }
    }

}
