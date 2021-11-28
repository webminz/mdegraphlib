package no.hvl.past.graph.trees;

import no.hvl.past.attributes.DataTypeVisitor;
import no.hvl.past.graph.elements.Triple;
import no.hvl.past.names.Name;

import java.util.Optional;

/**
 * Extends the standard {@link TreeTypeLibrary} with functionality to check
 * whether the type contains additional constraints, possibly limiting the multiplicity
 * and kind of elements.
 *
 * Per default (= not defined otherwise) all branches (edges) are interpreted collection valued,
 * and all nodes as a priori complex...
 */
public interface ExtendedTreeTypeLibrary extends TreeTypeLibrary {

    default boolean isCollectionValued(Triple branchTyping) {
        return true;
    }

    default boolean isCollectionValued(Name parentTyping, String branchLabel) {
        return childTyping(parentTyping, branchLabel).map(this::isCollectionValued).orElse(true);
    }

    default boolean isOrderedCollection(Triple branchTyping) {
        return true;
    }

    default boolean isOrderedCollection(Name parentTyping, String branchLabel) {
        return childTyping(parentTyping, branchLabel).map(this::isOrderedCollection).orElse(true);
    }

    default boolean isUniqueCollection(Triple branchTyping) {
        return false;
    }

    default boolean isUniqueCollection(Name parentTyping, String branchLabel) {
        return childTyping(parentTyping, branchLabel).map(this::isUniqueCollection).orElse(false);
    }

    default boolean isSimpleType(Name typeName) {
        return false;
    }

    default boolean isSimpleTypeChild(Name parentTyping, String branchLabel) {
        return childTyping(parentTyping, branchLabel).map(t -> isSimpleType(t.getTarget())).orElse(false);
    }

    default boolean isComplexTypeChild(Name parentTyping, String branchLabel) {
        return !isSimpleTypeChild(parentTyping, branchLabel);
    }

    default boolean isComplexType(Name typeName) {
        return !isSimpleType(typeName);
    }

    default boolean isStringType(Name nodeType) {
        return false;
    }

    default boolean isIntegerType(Name nodeType) {
        return false;
    }

    default boolean isFloatType(Name nodeType) {
        return false;
    }

    default boolean isBooleanType(Name nodeType) {
        return false;
    }

    default boolean isEnumType(Name nodeType) {
        return false;
    }

    default <R> R handleSimpleTypeChild(Name parentNodeType, String branchLabel, DataTypeVisitor<R> visitor) {
        Optional<Triple> triple = childTyping(parentNodeType, branchLabel);
        if (!triple.isPresent()) {
            return visitor.handleOtherUserType();
        } else {
            Name type = triple.get().getTarget();
            if (isStringType(type)) {
                return visitor.handleStringType();
            } else if (isIntegerType(type)) {
                return visitor.handleIntegerType();
            } else if (isFloatType(type)) {
                return visitor.handleFloatType();
            } else if (isBooleanType(type)) {
                return visitor.handleBoolType();
            } else if (isEnumType(type)) {
                return visitor.handleEnumType();
            } else {
                return visitor.handleOtherUserType();
            }
        }
    }
}
