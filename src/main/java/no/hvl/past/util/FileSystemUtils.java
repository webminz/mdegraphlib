package no.hvl.past.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class FileSystemUtils {

    private static FileSystemUtils instance;

    private  String baseDir;
    private final OperatingSystemType os;


    public FileSystemUtils(String baseDir, OperatingSystemType os) {
        this.baseDir = baseDir;
        this.os = os;
    }

    public void setBaseDir(File baseDir) {
       this.baseDir = baseDir.getAbsolutePath();
    }

    public enum OperatingSystemType {

        /**
         * Any variant of the Microsoft Windows OS (XP, Vista, 7, 10, ...)
         */
        WINDOWS,

        /**
         * Any linux distribution.
         */
        LINUX,

        /**
         * Any variant of the Apple Macintosh OS.
         */
        MAC_OS,


        UNKNOWN
    }

    public static OperatingSystemType osType() {
        String osName = System.getProperty("os.name");
        if (osName == null || osName.isEmpty()) {
            return OperatingSystemType.UNKNOWN;
        }
        if (osName.toLowerCase().contains("windows")) {
            return OperatingSystemType.WINDOWS;
        }
        if (osName.toLowerCase().contains("mac os") || osName.toLowerCase().contains("macintosh")) {
            return OperatingSystemType.MAC_OS;
        }
        if (osName.toLowerCase().contains("linux")) {
            return OperatingSystemType.LINUX;
        }
        return OperatingSystemType.UNKNOWN;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public OperatingSystemType getOs() {
        return os;
    }

    /**
     * Retrieves the {@link File} object for the given location.
     */
    public File file(String location) throws IOException {
        // TODO check for URL schemes as well
        if (location.startsWith("file:") || location.startsWith("http")) {
            return new UrlResource(location).getFile();
        }
        if (location.startsWith("classpath:")) {
            return new ClassPathResource(location).getFile();
        }
        if (location.startsWith("/")) {
            return new File(location);
        } else {
            return new File(baseDir, location);
        }
    }

    public File fileOverwrite(String location) throws IOException {
        File file = file(location);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public File fileCreateIfNecessary(String location) throws IOException {
        File file = file(location);
        file.mkdirs();
        return file;
    }


    public static String execReadToString(String execCommand) throws IOException {
        try (Scanner s = new Scanner(Runtime.getRuntime().exec(execCommand).getInputStream()).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }


    public static FileSystemUtils getInstance() {
        if (instance == null) {
            instance = new FileSystemUtils(System.getProperty("user.dir"), FileSystemUtils.osType());
        }
        return instance;
    }



}
