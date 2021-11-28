package no.hvl.past.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface GenericIOHandler {

    void handle(InputStream i, OutputStream o) throws Exception;
}
