package no.hvl.past.graph.trees;

import no.hvl.past.graph.Graph;
import no.hvl.past.graph.Universe;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;
import no.hvl.past.util.ShouldNotHappenException;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TreeBuilder {


    public static abstract class ConcreteBuilder {
        public abstract Name currentName();
    }

    public static class RootBuilder extends ConcreteBuilder {
        private final NormalNode node;
        private TreeBuilder parent;
        private final List<BranchBuilder<?>> childBuilders = new ArrayList<>();

        public RootBuilder(TreeBuilder parent, Name rootElementName) {
            this.parent = parent;
            this.node = new NormalNode(rootElementName);
        }

        @Override
        public Name currentName() {
            return node.elementName();
        }

        public RootBuilder typedOver(Name nodeType) {
            this.node.setTypeName(nodeType);
            return this;
        }

        public RootBuilder complex() {
            this.node.setComplex(true);
            return this;
        }

        public RootBuilder simple() {
            this.node.setComplex(false);
            return this;
        }

        public BranchBuilder<RootBuilder> branch(Name branchKey) {
            BranchBuilder<RootBuilder> result = new BranchBuilder<>(this, branchKey);
            this.childBuilders.add(result);
            return result;
        }

        public BranchBuilder<RootBuilder> branch(String branchKey) {
            BranchBuilder<RootBuilder> result = new BranchBuilder<>(this, Name.identifier(branchKey));
            this.childBuilders.add(result);
            return result;
        }

        public NormalNode build() {
            for (BranchBuilder<?> bb : childBuilders) {
                this.node.getChildren().addAll(bb.build());
            }
            return this.node;
        }

        public TreeBuilder endRoot() {
            return parent;
        }
    }

    public static class NodeBuilder<P extends ConcreteBuilder> extends ConcreteBuilder {

        private final P parent;
        private final NormalNode node;

        private final List<BranchBuilder<?>> childBuilders = new ArrayList<>();

        NodeBuilder(P parentBuilder, Name parentElement, Name element) {
            this.parent = parentBuilder;
            this.node = new NormalNode(element);
            this.node.setParentNodeName(parentElement);
        }

        public NodeBuilder<P> typedOver(Name nodeType) {
            this.node.setTypeName(nodeType);
            return this;
        }

        public NodeBuilder<P> complex() {
            this.node.setComplex(true);
            return this;
        }

        public NodeBuilder<P> simple() {
            this.node.setComplex(false);
            return this;
        }

        public P endNode() {
            return parent;
        }

        public BranchBuilder<NodeBuilder<P>> branch(Name branchKey) {
            BranchBuilder<NodeBuilder<P>> result = new BranchBuilder<>(this, branchKey);
            this.childBuilders.add(result);
            return result;
        }

        public BranchBuilder<NodeBuilder<P>> branch(String branchKey) {
            BranchBuilder<NodeBuilder<P>> result = new BranchBuilder<>(this, Name.identifier(branchKey));
            this.childBuilders.add(result);
            return result;
        }

        public NormalNode build() {
            for (BranchBuilder<?> bb : childBuilders) {
                this.node.getChildren().addAll(bb.build());
            }
            return this.node;
        }


        @Override
        public Name currentName() {
            return this.node.elementName();
        }
    }


    public static class BranchBuilder<P extends ConcreteBuilder> extends ConcreteBuilder {
        private final P parent;
        private final Name branchKey;
        private Triple type;
        private boolean isCollection;
        private final List<NodeBuilder<?>> childBilders = new ArrayList<>();

        public BranchBuilder(P parent, Name branchKey) {
            this.parent = parent;
            this.branchKey = branchKey;
        }

        public BranchBuilder<P> collection() {
            this.isCollection = true;
            return this;
        }

        public BranchBuilder<P> typedOver(Triple triple) {
            this.type = triple;
            return this;
        }

        public BranchBuilder<P> simpleChild(Name value) {
            NodeBuilder<BranchBuilder<P>> nodeBuilder = new NodeBuilder<>(this, parent.currentName(), value);
            nodeBuilder.simple();
            if (type != null) {
                nodeBuilder.typedOver(type.getTarget());
            }
            childBilders.add(nodeBuilder);
            return this;
        }

        public NodeBuilder<BranchBuilder<P>> complexChild(Name elementName) {
            NodeBuilder<BranchBuilder<P>> nodeBuilder = new NodeBuilder<>(this, parent.currentName(), elementName);
            nodeBuilder.complex();
            if (type != null) {
                nodeBuilder.typedOver(type.getTarget());
            }
            childBilders.add(nodeBuilder);
            return nodeBuilder;
        }

        public P endBranch() {
            return parent;
        }

        public List<Branch> build() {
            List<Branch> result = new ArrayList<>();
            if (isCollection || childBilders.size() > 1) {
                for (int i = 0; i < childBilders.size(); i++) {
                    result.add(new NormalBranch(parent.currentName(), branchKey, childBilders.get(i).build(), Long.valueOf(i), type));
                }
            } else {
                if (!childBilders.isEmpty()) {
                    result.add(new NormalBranch(parent.currentName(), branchKey, childBilders.get(0).build(), null, type));
                }
            }
            return result;
        }

        @Override
        public Name currentName() {
            Name pre = branchKey.prefixWith(parent.currentName());
            if (isCollection || childBilders.size() > 1) {
                pre = pre.index(childBilders.size() - 1);
            }
            return pre;
        }
    }

    public static TreeReceiver builderHandler(
            List<TreeElementNamingStrategy> nameProviderChain,
            Consumer<Tree> finishedHandler) {
        Supplier<Name> nameSupplier = new Supplier<Name>() {
            @Override
            public Name get() {
                Name name = null;
                for (TreeElementNamingStrategy ns : nameProviderChain) {
                    Optional<Name> proposal = ns.proposeName();
                    if (proposal.isPresent()) {
                        name = proposal.get();
                        break;
                    }
                }
                if (name == null) {
                    throw new ShouldNotHappenException(TreeElementNamingStrategy.class, "A name provider chain should always end with a strategy that can always come up with a name!");
                }
                for (TreeElementNamingStrategy ns : nameProviderChain) {
                    ns.nameAccepted(name);
                }

                return name;
            }

        };
        TreeReceiver mainHandler =  new ToBuilderTreeReceiver(nameSupplier, finishedHandler);
        List<TreeReceiver> handlers = new ArrayList<>(nameProviderChain);
        handlers.add(mainHandler);
        return new TreeReceiver.Multiplexer(handlers);
    }

    public static class ToBuilderTreeReceiver implements TreeReceiver {

        private TreeTypeLibrary lib;
        private ConcreteBuilder current;
        private final TreeBuilder treeBuilder;
        private final Supplier<Name> nameSupplier;
        private final Consumer<Tree> finishedCallback;
        private final Stack<Name> parentTypingStack = new Stack<>();

        public ToBuilderTreeReceiver(
                Supplier<Name> nameSupplier,
                Consumer<Tree> finishedCallback) {
            this.treeBuilder = new TreeBuilder();
            this.nameSupplier = nameSupplier;
            this.finishedCallback = finishedCallback;
        }

        @Override
        public void startTree(Name treeName) throws Exception {
            this.treeBuilder.name(treeName);
        }

        @Override
        public void treeType(Graph typeGraph, TreeTypeLibrary mappingLibrary) {
            this.treeBuilder.typedOver(typeGraph);
            this.lib = mappingLibrary;
        }

        @Override
        public void startRoot(Name rootName) {
            RootBuilder root = treeBuilder.root(nameSupplier.get());
            if (lib != null) {
                Name nodeType = lib.rootTyping();
                parentTypingStack.push(nodeType);
                root.typedOver(nodeType);
            } else {
                parentTypingStack.push(null);
            }
            this.current = root;
        }

        @Override
        public void nodeId(Name id) {
            if (current instanceof RootBuilder) {
                ((RootBuilder) current).node.setElementName(id);
            } else if (current instanceof NodeBuilder<?>) {
                ((NodeBuilder<?>) current).node.setElementName(id);
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a root or node builder here");
            }
        }

        @Override
        public void nodeType(Name type) throws IOException {
            if (current instanceof RootBuilder) {
                ((RootBuilder) current).typedOver(type);
            } else if (current instanceof NodeBuilder<?>) {
                ((NodeBuilder<?>) current).typedOver(type);
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a root or node builder here");

            }
        }

        @Override
        public void startBranch(Name key, boolean isCollection) {
            if (current instanceof RootBuilder) {
                RootBuilder builder = (RootBuilder) current;
                BranchBuilder<RootBuilder> branchBuilder = builder.branch(key);
                handleBranch(branchBuilder, key);
            } else if (current instanceof NodeBuilder<?>) {
                NodeBuilder<?> builder = (NodeBuilder<?>) current;
                BranchBuilder<?> branch = builder.branch(key);
                handleBranch(branch, key);
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a root or node builder here");
            }
        }

        @Override
        public void branchType(Triple type) {
            if (current instanceof BranchBuilder<?>) {
                ((BranchBuilder<?>) current).type = type;
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a branch builder here");

            }
        }

        private void handleBranch(BranchBuilder<?> branchBuilder, Name key) {
            if (lib != null && parentTypingStack.peek() != null) {
                Optional<Triple> triple = lib.childTyping(parentTypingStack.peek(), key);
                if (triple.isPresent()) {
                    parentTypingStack.push(triple.get().getTarget());
                    branchBuilder.typedOver(triple.get());
                } else {
                    parentTypingStack.push(null);
                }
            } else {
                parentTypingStack.push(null);
            }
            current = branchBuilder;
        }

        @Override
        public void endBranch() {
            parentTypingStack.pop();
            if (current instanceof BranchBuilder<?>) {
                BranchBuilder<?> bb = (BranchBuilder<?>) current;
                current = bb.endBranch();
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a branch builder here");
            }

        }

        @Override
        public void startComplexNode() {
            if (current instanceof RootBuilder) {
                // nothing to do here
            } else if (current instanceof BranchBuilder<?>) {
                BranchBuilder<?> bb = (BranchBuilder<?>) current;
                NodeBuilder<?> nodeBuilder = bb.complexChild(nameSupplier.get());
                current = nodeBuilder;
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a branch builder here");
            }
        }

        @Override
        public void endComplexNode() throws IOException {
            if (current instanceof RootBuilder) {
                // nothing to do here
            } else if (current instanceof NodeBuilder<?>) {
                NodeBuilder<?> nb = (NodeBuilder<?>) current;
                current = nb.endNode();
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a node builder here");
            }
        }

        @Override
        public void emptyLeaf() throws IOException {
            // nothing to do here
        }

        @Override
        public void valueLeaf(Name value) throws IOException {
            if (current instanceof BranchBuilder<?>) {
                BranchBuilder<?> bb = (BranchBuilder<?>) current;
                bb.simpleChild(value);
                //
            } else {
                throw new ShouldNotHappenException(TreeBuilder.class, "Implementation error or invalid usage of the TreeCretor interface: Expecting a branch builder here");
            }
        }

        @Override
        public void endRoot() throws IOException {
            finishedCallback.accept(treeBuilder.build());
        }


        @Override
        public void endTree() {

        }
    }



    private Name treeName;
    private Graph type;
    private RootBuilder rootBuilder;

    public TreeBuilder() {
        this.treeName = Name.anonymousIdentifier();
        this.type = Universe.CYCLE;
    }

    public TreeBuilder name(Name name) {
        this.treeName = name;
        return this;
    }

    public TreeBuilder typedOver(Graph typeGraph) {
        this.type = typeGraph;
        return this;
    }

    public RootBuilder root(Name rootName) {
        this.rootBuilder = new RootBuilder(this, rootName);
        return rootBuilder;
    }

    public Tree build() {
        NormalNode root = this.rootBuilder.build();
        NormalTree result = new NormalTree(root, treeName);
        result.setType(type);
        return result;
    }

}
