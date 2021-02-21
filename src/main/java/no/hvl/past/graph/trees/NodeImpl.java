package no.hvl.past.graph.trees;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import no.hvl.past.names.Name;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NodeImpl implements Node {

    private static final Name ROOT_NAME = Name.identifier("/");

    static class Builder {

        private Name branchName;
        private int nbr;
        private Name elementName;
        private Map<Name, Name> attributes;
        private Multimap<Name, Builder> children;
        private Name parent;

        public Builder(Name branchName, int nbr) {
            this.branchName = branchName;
            this.nbr = nbr;
            this.attributes = new HashMap<>();
            this.children = ArrayListMultimap.create();
        }

        public Builder key(Name name) {
            if (attributes.containsKey(name)) {
                elementName = attributes.get(name);
            }
            return this;
        }


        public Builder setRoot() {
            this.elementName = ROOT_NAME;
            return this;
        }

        public Builder name(Name name) {
            this.elementName = name;
            return this;
        }

        public Builder addAttribute(Name key, Name value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder addChild(Name branch) {
            int nbr = this.children.get(branch).size();
            Builder child = new Builder(branch, nbr);
            this.children.put(branch, child);
            return child;
        }

        public NodeImpl build(Name parentName) {
            if (elementName == null) {
                elementName = branchName.index(nbr);
            }
            if (parentName != null) {
                this.parent = parentName;
                elementName = elementName.prefixWith(parentName);
            }
            Multimap<Name, Node> childrenResult = ArrayListMultimap.create();
            for (Name branch : children.keySet()) {
                for (Builder child : children.get(branch)) {
                    childrenResult.put(branch, child.build(elementName));
                }
            }
            return new NodeImpl(elementName, attributes, childrenResult, parent);
        }
    }

    public static class FilteredNode implements Node {

        private final Node base;
        private final Map<Name, QueryNode> filter;

        public FilteredNode(Node base, QueryNode filter) {
            this.base = base;
            this.filter = new HashMap<>();
            filter.children().forEach(qn -> this.filter.put(qn.filteredElementName(), qn));
        }

        @Override
        public Name elementName() {
            return base.elementName();
        }

        @Override
        public Optional<Name> parentName() {
            return base.parentName();
        }

        @Override
        public Optional<Name> attribute(Name attributeName) {
            return base.attribute(attributeName);
        }

        @Override
        public Stream<Name> attributeNames() {
            return base.attributeNames();
        }

        @Override
        public Stream<Node> children(Name childBranchName) {
            if (filter.containsKey(childBranchName)) {
                return base.children(childBranchName)
                        .filter(node -> this.filter.get(childBranchName).filterPredicate().isSatisfied(node.attributeValues()))
                        .map(n -> new FilteredNode(n, filter.get(childBranchName)));
            }
            return Stream.empty();
        }

        @Override
        public Stream<Name> childBranchNames() {
            return base.childBranchNames().filter(filter::containsKey);
        }
    }


    private final Name name;
    private final Map<Name, Name> attributes;
    private final Multimap<Name, Node> children;
    private final Name parent;

    public NodeImpl(Name name, Map<Name, Name> attributes, Multimap<Name, Node> children, Name parent) {
        this.name = name;
        this.attributes = attributes;
        this.children = children;
        this.parent = parent;
    }

    @Override
    public Name elementName() {
        return name;
    }

    @Override
    public Optional<Name> parentName() {
        return Optional.ofNullable(parent);
    }

    @Override
    public Optional<Name> attribute(Name attributeName) {
        return attributes.containsKey(attributeName) ? Optional.of(attributes.get(attributeName)) : Optional.empty();
    }

    @Override
    public Stream<Name> attributeNames() {
        return attributes.keySet().stream();
    }

    @Override
    public Stream<Node> children(Name childBranchName) {
        return children.get(childBranchName).stream();
    }

    @Override
    public Stream<Name> childBranchNames() {
        return children.keySet().stream();
    }

}
