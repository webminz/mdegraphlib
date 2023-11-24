package no.hvl.past.names;

import no.hvl.past.graph.trees.Node;
import no.hvl.past.util.ByteUtils;
import no.hvl.past.util.ShouldNotHappenException;
import no.hvl.past.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class NamePath extends Identifier {

    private final List<Name> segments;

    public NamePath(List<Name> segments) {
        this.segments = segments;
    }

    public NamePath addChild(Name child) {
        ArrayList<Name> objects = new ArrayList<>(segments.size() + 1);
        objects.addAll(segments);
        objects.add(child);
        return new NamePath(objects);
    }

    public List<String> segmentString() {
        List<String> result = new ArrayList<>();
        for (Name n : segments) {
            if (n instanceof Index) {
                Index indexName = (Index) n;
                result.add(indexName.getWrapped().print(PrintingStrategy.IGNORE_PREFIX));
                result.add(Long.toString(indexName.getIndex()));
            } else {
                result.add(n.print(PrintingStrategy.IGNORE_PREFIX));
            }
        }

        return result;
    }

    @Override
    public byte[] getValue() {
        byte[] result = ByteUtils.prefix(NAME_PATH_MAGIC_BYTE, ByteUtils.intToByteArray(segments.size(), false));
        for (Name n : segments) {
            result = ByteUtils.concat(result, n.getValue());
        }
        return result;
    }

    public NamePath parent() {
        if (segments.isEmpty()) {
            throw new ShouldNotHappenException(NamePath.class, "This method should not have been called since, the current object is already the root path!!!");
        }
        List<Name> seg = new ArrayList<>(segments);
        seg.remove(seg.size() - 1);
        return new NamePath(seg);
    }

    public NamePath next(Long top) {
        if (segments.isEmpty()) {
            return this;
        }
        List<Name> seg = new ArrayList<>(segments);
        Name last = seg.get(seg.size() - 1);
        if (last instanceof Index) {
            Index idx = (Index) last;
            Index newIdx = new Index(idx.getWrapped(), top);
            seg.remove(seg.size() - 1);
            seg.add(newIdx);
            return new NamePath(seg);
        } else {
            return this;
        }

    }

    public Name current() {
        if (segments.isEmpty()) {
            return Node.ROOT_NAME;
        } else {
            return segments.get(segments.size() - 1);
        }
    }

    @Override
    public String toString() {
        return "/" + StringUtils.fuseList(segments, Name::toString, "/");
    }
}
