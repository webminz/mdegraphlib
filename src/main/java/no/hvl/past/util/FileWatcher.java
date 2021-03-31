package no.hvl.past.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FileWatcher implements Runnable {

    private final String fileUri;
    private final Consumer<LocalDateTime> call;
    private boolean isActive;
    private List<String> watchedFiles = new ArrayList<>();

    public FileWatcher(String fileUri, Consumer<LocalDateTime> call) {
        this.fileUri = fileUri;
        this.call = call;
        this.isActive = true;
    }

    public void watch() throws IOException, InterruptedException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path fullPath = Paths.get(fileUri);
        watchedFiles.add(fullPath.toFile().getName());
        Path path = fullPath.getParent();
        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        while (isActive) {
            WatchKey take = watchService.take();
            for (WatchEvent<?> evt : take.pollEvents()) {
                if (watchedFiles.contains(evt.context().toString())) {
                    LocalDateTime now = LocalDateTime.now();
                    call.accept(now);
                }
            }
            take.reset();
        }
    }


    public void stop() {
        this.isActive = false;
    }

    public void addWatchedFile(String fileName) {
        this.watchedFiles.add(fileName);
    }


    @Override
    public void run() {
        try {
            watch();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static FileWatcher watch(String fileUri, Consumer<LocalDateTime> handler) {
        FileWatcher fw = new FileWatcher(fileUri, handler);
        Thread t = new Thread(fw);
        t.setDaemon(false);
        t.start();
        return fw;
    }


}
