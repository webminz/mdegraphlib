package no.hvl.past.graph.trees;

import no.hvl.past.graph.*;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.graph.elements.Tuple;
import no.hvl.past.names.Name;
import no.hvl.past.util.Pair;

import java.util.*;
import java.util.stream.Stream;


public interface TypedTree extends Tree, GraphMorphism {

    Name BUNDLE = Name.identifier("Bundle");


    class Impl implements TypedTree {

        private final TypedNode root;
        private final Name name;
        private final Graph typeGraph;
        private final Map<Name, Name> typingCache = new HashMap<>();

        public Impl(TypedNode root, Name name, Graph typeGraph) {
            this.root = root;
            this.name = name;
            this.typeGraph = typeGraph;
        }

        @Override
        public TypedNode root() {
            return root;
        }

        @Override
        public Graph codomain() {
            return typeGraph;
        }

        @Override
        public Optional<Name> map(Name name) {
            if (!typingCache.containsKey(name)) {
                Optional<Name> result = this.root.lookupTyping(name);
                if (result.isPresent()) {
                    this.typingCache.put(name, result.get());
                } else {
                    this.typingCache.put(name, null);
                }
            }
            return Optional.ofNullable(this.typingCache.get(name));
        }

        @Override
        public Name getName() {
            return name;
        }

        @Override
        public boolean isInfinite() {
            return false;
        }
    }

   default GraphMorphism typedPartToMorphism() throws GraphError {
       Set<Triple> elements = new LinkedHashSet<>();
       Set<Tuple> mappings = new HashSet<>();
       root().aggregateTypedPart(elements, mappings);
       return GraphMorphismImpl.create(getName(), new GraphImpl(Name.identifier("dom").appliedTo(getName()), elements), codomain(), mappings);
   }

    @Override
    default Optional<Name> map(Name name) {
        return this.root().lookupTyping(name);
    }

    @Override
    default Stream<Triple> preimage(Triple to) {
        if (to.isNode()) {
            Set<TypedNode> preResult = new LinkedHashSet<>();
            root().findNodeByType(to.getLabel(), preResult);
            return preResult.stream().map(TypedNode::elementName).map(Triple::node);
        } else {
            Set<TypedBranch> preResult = new LinkedHashSet<>();
            root().findChildRelationByType(to, preResult);
            return preResult.stream().map(TypedBranch::asEdge);
        }
    }

    @Override
    default Stream<Triple> allOutgoingInstances(Triple type, Name src) {
        return root().findByName(src)
                .filter(n -> n instanceof TypedNode)
                .map(n -> (TypedNode) n)
                .map(tn -> tn.typedChildren()
                        .filter(c -> c.matches(type))
                        .map(TypedBranch::asEdge))
                .orElse(Stream.empty());
    }

    TypedNode root();

    @Override
    default Graph domain() {
        return this;
    }

    @Override
    default boolean verify() {
        return false; // TODO check absence of cycles
    }

    @Override
    default void accept(Visitor visitor) {
        visitor.beginMorphism();
        visitor.handleElementName(getName());
        domain().accept(visitor);
        codomain().accept(visitor);
        mappings().forEach(visitor::handleMapping);
        visitor.endMorphism();
    }


    static TypedTree fuseAsForrestAsBundle(List<TypedTree> trees, Name resultName, Sketch schema) {
        List<TypedBranch> children = new ArrayList<>();
        TypedNode newRoot = new TypedNode.Impl(Node.ROOT_NAME, null, children, BUNDLE);
        for (TypedTree tree : trees) {
            children.add(new TypedBranch.Impl(newRoot, tree.getName().printRaw(), tree.root(), null));
        }
        return new TypedTree.Impl(newRoot,resultName , schema.carrier());
    }

}
