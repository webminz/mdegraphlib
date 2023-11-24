package no.hvl.past.names;

/**
 * Values are also names.
 * The speciality of names is that they are constants in some
 * predefined algebra and we can do (simple) builtin functional operations
 * on them.
 */
public abstract class Value extends Name {

    @Override
    public final boolean isValue() {
        return true;
    }

    @Override
    public final boolean isVariable() {
        return false;
    }

    @Override
    public final boolean isIdentifier() {
        return false;
    }

    @Override
    public final Name unprefix(Name name) {
        return this;
    }

    @Override
    public final Name unprefixAll() {
        return this;
    }

    @Override
    public final Name firstPart() {
        return this;
    }

    @Override
    public final Name secondPart() {
        return this;
    }

    @Override
    public final Name part(int i) {
        return this;
    }

    @Override
    public final boolean isMultipart() {
        return false;
    }

    @Override
    public final boolean isDerived() {
        return false;
    }

    @Override
    public boolean isIndexed() {
        return false;
    }
}
