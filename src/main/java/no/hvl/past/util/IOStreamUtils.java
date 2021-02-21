package no.hvl.past.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class IOStreamUtils {

    public interface OutputStreamConsumer {

        void consume(OutputStream outputStream) throws IOException;

    }


    public static String readInputStreamAsString(InputStream i) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(i));
        StringBuilder builder = new StringBuilder();
        int read = reader.read();
        while (read >= 0) {
            builder.append((char) read);
            read = reader.read();
        }
        reader.close();
        return builder.toString();
    }

    public static String outputStreamClosure(OutputStreamConsumer consumer) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        consumer.consume(bos);
        return bos.toString();
    }

    public static InputStream stringAsInputStream(String data) {
        ByteArrayInputStream result = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        return result;
    }

    public static void copyOver(InputStream i, OutputStream o) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(o));
        BufferedReader reader = new BufferedReader(new InputStreamReader(i));
        int read = reader.read();
        while (read >= 0) {
            writer.write(read);
            read = reader.read();
        }
        reader.close();
        writer.flush();
        writer.close();
        o.close();
    }
}
