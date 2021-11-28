package no.hvl.past.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileSystemAccessPoint {

    private final File path;
    private final FileSystemUtils fs;

    /**
     * Creates a new file system entry point expects, path to be a directory that exists.
     */
    private FileSystemAccessPoint(File path, FileSystemUtils fs) {
        this.path = path;
        this.fs = fs;
    }

    public String absolutePath() {
        return path.getAbsolutePath();
    }

    public String toURI() {
        return "file://" + path.getAbsolutePath();
    }

    public List<File> ls() {
        File[] a = path.listFiles();
        if (a != null) {
            return Arrays.asList(a);
        }
        return Collections.emptyList();
    }

    public void mkdir(String name) {
        File folder = new File(path, name);
        folder.mkdirs();
    }

    public FileSystemAccessPoint cd(String navigationPath) throws IOException {
        if (navigationPath.isEmpty()) {
            return this;
        }
        if (navigationPath.contains(":")) {
            if (fs.getOs().equals(FileSystemUtils.OperatingSystemType.WINDOWS) && navigationPath.indexOf(':') == 1) {
                return FileSystemAccessPoint.create(new File(navigationPath), fs);
            } else {
                if (navigationPath.startsWith("classpath")) {
                    return FileSystemAccessPoint.create(new ClassPathResource(navigationPath).getFile(),fs);
                } else {
                    return FileSystemAccessPoint.create(new UrlResource(navigationPath).getFile(), fs);
                }
            }
        }
        if (navigationPath.startsWith("/")) {
            return FileSystemAccessPoint.create(new File(navigationPath), fs);
        }
        if (navigationPath.startsWith("..")) {
            return FileSystemAccessPoint.create(path.getParentFile(), fs).cd(navigationPath.substring(3));
        }
        if (navigationPath.startsWith("./")) {
            return this.cd(navigationPath.substring(2));
        }
        return FileSystemAccessPoint.create(new File(path, navigationPath), fs);
    }


    /**
     * Retrieves the {@link File} object for the given location.
     */
    public File file(String location) throws IOException {
        if (location.contains(":")) {
            if (fs.getOs().equals(FileSystemUtils.OperatingSystemType.WINDOWS) && location.indexOf(':') == 1) {
                return new File(location);
            } else {
                if (location.startsWith("classpath")) {
                    return new ClassPathResource(location).getFile();
                } else {
                    return new UrlResource(location).getFile();
                }
            }
        }
        if (location.startsWith("/")) {
            return new File(location);
        }
        if (location.contains("/")) {
            int idx = location.lastIndexOf('/');
            String path = location.substring(0, idx);
            String file = location.substring(idx + 1);
            return cd(path).file(file);
        }
        return new File(path, location);
    }


    public static FileSystemAccessPoint create(File path, FileSystemUtils fs) throws IOException {
        if (!path.isDirectory() && path.exists()) {
            throw new IOException(path.getAbsolutePath() + " is not a directory!");
        }
        return new FileSystemAccessPoint(path, fs);
    }

}
