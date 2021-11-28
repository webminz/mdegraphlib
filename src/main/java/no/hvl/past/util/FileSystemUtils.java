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

    private final String baseDir;
    private final OperatingSystemType os;
    private FileSystemAccessPoint entryPoint;

    private FileSystemUtils(String baseDir, OperatingSystemType os) {
        this.baseDir = baseDir;
        this.os = os;
    }

    private FileSystemAccessPoint getEntryPoint() {
        if (entryPoint == null) {
            try {
                this.entryPoint = FileSystemAccessPoint.create(new File(baseDir), this);
            } catch (IOException e) {
                throw new ShouldNotHappenException(FileSystemUtils.class,e);
            }
        }
        return this.entryPoint;
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

    public FileSystemAccessPoint accessPoint(String atPath) throws IOException {
        return getEntryPoint().cd(atPath);
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
