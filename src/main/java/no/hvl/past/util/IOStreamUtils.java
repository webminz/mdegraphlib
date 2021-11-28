package no.hvl.past.util;

import no.hvl.past.systems.Sys;

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



    public static class Wiretap extends OutputStream {
        private StringBuilder recorder;
        private OutputStream realTarget;

        public Wiretap(OutputStream realTarget) {
            this.realTarget = realTarget;
            this.recorder = new StringBuilder();
        }

        public String getRecorded() {
            return recorder.toString();
        }

        @Override
        public void write(int b) throws IOException {
            recorder.append((char) b);
            realTarget.write(b);
        }

        @Override
        public void close() throws IOException {
            realTarget.close();
        }
    }
}
