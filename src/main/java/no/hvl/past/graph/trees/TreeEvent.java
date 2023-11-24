package no.hvl.past.graph.trees;

import com.google.common.base.Objects;
import no.hvl.past.graph.Graph;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.AnonymousIdentifier;
import no.hvl.past.names.Name;
import no.hvl.past.names.PrintingStrategy;

public abstract class TreeEvent {

    public abstract void accept(TreeReceiver receiver) throws Exception;

    public static StartTree startTree(Name treeName) {
        return new StartTree(treeName);
    }

    public static final class StartTree extends TreeEvent {

        private final Name treeName;

        StartTree(Name treeName) {
            this.treeName = treeName;
        }

        public Name getTreeName() {
            return treeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StartTree startTree = (StartTree) o;
            return Objects.equal(treeName, startTree.treeName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(treeName);
        }

        @Override
        public String toString() {
            return "«START_TREE '" + treeName.print(PrintingStrategy.DETAILED)+ "' »";
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.startTree(treeName);
        }
    }

    public static EndTree endTree() {
        return new EndTree();
    }

    public static final class EndTree extends TreeEvent {

        EndTree() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EndTree;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getClass());
        }

        @Override
        public String toString() {
            return "«END_TREE»";
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.endTree();
        }
    }

    public static StartRoot startRoot(Name rootName) {
        return new StartRoot(rootName);
    }


    public static final class StartRoot extends TreeEvent {
        private final Name rootName;

        StartRoot(Name rootName) {
            super();
            this.rootName = rootName;
        }

        public Name getRootName() {
            return rootName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StartRoot startRoot = (StartRoot) o;
            if (rootName instanceof AnonymousIdentifier) {
                return startRoot.rootName instanceof AnonymousIdentifier;
            }
            return Objects.equal(rootName, startRoot.rootName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(rootName);
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.startRoot(rootName);
        }

        @Override
        public String toString() {
            return "«START_ROOT '" + rootName.print(PrintingStrategy.DETAILED) +"' »";
        }
    }

    public static EndRoot endRoot() {
        return new EndRoot();
    }

    public static final class EndRoot extends TreeEvent {

        EndRoot() {
            super();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EndRoot;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getClass());
        }

        @Override
        public void accept(TreeReceiver creator) throws Exception {
            creator.endRoot();
        }

        @Override
        public String toString() {
            return "«END_ROOT»";
        }
    }

    public static TreeType treeType(Graph typeGraph, TreeTypeLibrary typeLibrary) {
        return new TreeType(typeGraph, typeLibrary);
    }

    public static final class TreeType extends TreeEvent{
        private final Graph graph;
        private final TreeTypeLibrary typeLibrary;

        TreeType(Graph graph, TreeTypeLibrary typeLibrary) {
            super();
            this.graph = graph;
            this.typeLibrary = typeLibrary;
        }

        public Graph getGraph() {
            return graph;
        }

        public TreeTypeLibrary getTypeLibrary() {
            return typeLibrary;
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.treeType(graph, typeLibrary);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TreeType treeType = (TreeType) o;
            return Objects.equal(graph.getName(), treeType.graph.getName()); // TODO investigate what was my original plan: && Objects.equal(typeLibrary, treeType.typeLibrary);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(graph, typeLibrary);
        }

        @Override
        public String toString() {
            return "«TREE_TYPE '" + graph.getName().print(PrintingStrategy.DETAILED) + "' »";
        }
    }

    public static StartBranch startBranch(Name key, boolean isCollection) {
        return new StartBranch(key, isCollection);
    }

    public static final class StartBranch extends TreeEvent {
        private final Name key;
        private final boolean isCollection;

        StartBranch(Name key, boolean isCollection) {
            this.key = key;
            this.isCollection = isCollection;
        }

        public Name getKey() {
            return key;
        }

        public boolean isCollection() {
            return isCollection;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StartBranch that = (StartBranch) o;
            return isCollection == that.isCollection && Objects.equal(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key, isCollection);
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.startBranch(key, isCollection);
        }

        @Override
        public String toString() {
            return "«START_BRANCH '" + key.print(PrintingStrategy.DETAILED) + (isCollection ? "' * " : "' ") + "»" ;
        }
    }

    public static EndBranch endBranch() {
        return new EndBranch();
    }

    public static final class EndBranch extends TreeEvent {

        EndBranch() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EndBranch;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getClass());
        }


        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.endBranch();
        }

        @Override
        public String toString() {
            return "«END_BRANCH»";
        }
    }

    public static BranchType branchType(Triple type) {
        return new BranchType(type);
    }

    public static final class BranchType extends TreeEvent {

        private final Triple type;

        public BranchType(Triple type) {
            this.type = type;
        }

        public Triple getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BranchType that = (BranchType) o;
            return Objects.equal(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type);
        }


        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.branchType(type);
        }

        @Override
        public String toString() {
            return "«BRANCH_TYPE '" + type.toString() + "' »";
        }

    }

    public static StartComplexNode startComplexNode() {
        return new StartComplexNode();
    }

    public static final class StartComplexNode extends TreeEvent {

        StartComplexNode() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StartComplexNode;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getClass());
        }


        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.startComplexNode();
        }

        @Override
        public String toString() {
            return "«START_COMPLEX_NODE»";
        }
    }

    public static EndComplexNode endComplexNode() {
        return new EndComplexNode();
    }


    public static final class EndComplexNode extends TreeEvent {

        EndComplexNode() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EndComplexNode;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getClass());
        }


        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.endComplexNode();
        }

        @Override
        public String toString() {
            return "«END_COMPLEX_NODE»";
        }
    }

    public static NodeId nodeId(Name id) {
        return new NodeId(id);
    }

    public static final class NodeId extends TreeEvent {
        private final Name id;

        public NodeId(Name id) {
            this.id = id;
        }

        public Name getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeId nodeId = (NodeId) o;
            return Objects.equal(id, nodeId.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.nodeId(id);
        }

        @Override
        public String toString() {
            return "«NODE_ID '" + id.print(PrintingStrategy.DETAILED)+"' »";
        }
    }

    public static NodeType nodeType(Name type) {
        return new NodeType(type);
    }

    public static final class NodeType extends TreeEvent {
        private final Name type;

        public NodeType(Name type) {
            this.type = type;
        }

        public Name getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeType nodeType = (NodeType) o;
            return Objects.equal(type, nodeType.type);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type);
        }

        @Override
        public String toString() {
            return "«NODE_TYPE '" + type.print(PrintingStrategy.DETAILED) +"' »";
        }


        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.nodeType(type);
        }
    }

    public static ValueLeaf valueLeaf(Name value) {
        return new ValueLeaf(value);
    }

    public static final class ValueLeaf extends TreeEvent {
        private final Name value;

        public ValueLeaf(Name value) {
            this.value = value;
        }

        public Name getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValueLeaf valueLeaf = (ValueLeaf) o;
            return Objects.equal(value, valueLeaf.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return "«LEAF '" + value.print(PrintingStrategy.DETAILED) + "' »";
        }

        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.valueLeaf(value);
        }
    }

    public static EmptyLeaf emptyLeaf() {
        return new EmptyLeaf();
    }

    public static final class EmptyLeaf extends TreeEvent {

        EmptyLeaf() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EmptyLeaf;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getClass());
        }


        @Override
        public void accept(TreeReceiver receiver) throws Exception {
            receiver.emptyLeaf();
        }

        @Override
        public String toString() {
            return "«EMPTY_LEAF»";
        }
    }


}
