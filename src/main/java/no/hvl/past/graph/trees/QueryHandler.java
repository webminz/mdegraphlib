package no.hvl.past.graph.trees;

import no.hvl.past.util.GenericIOHandler;

import java.io.*;

public interface QueryHandler extends GenericIOHandler {

    default Tree resolve(QueryTree queryTree) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resolve(queryTree, bos);
        return deserialize(new ByteArrayInputStream(bos.toByteArray()));
    }

    default void resolve(QueryTree queryTree, OutputStream outputStream) throws IOException {
        InputStream inputStream = serialize(queryTree);
        handle(inputStream, outputStream);
    }

    default InputStream resolveAsStream(QueryTree queryTree) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        resolve(queryTree, bos);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    InputStream serialize(QueryTree queryTree) throws IOException;

    QueryTree parse(InputStream inputStream) throws IOException;

    Tree deserialize(InputStream inputStream) throws IOException;
}
