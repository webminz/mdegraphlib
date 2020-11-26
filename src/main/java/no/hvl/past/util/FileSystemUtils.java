package no.hvl.past.util;

public class FileSystemUtils {

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

        /**
         * Any other kind of UNIX operating system, e.g. FreeBSD, etc.
         */
        UNIX,

        /**
         * IBMs Mainframe operating system.
         */
        ZOS,

        /**
         * Any other kind of (possibly) embedded operating system that has a JVM.
         */
        EMBEDDED
    }

    public enum ResourceType {

        LOCAL_FILE,

        LOCAL_DIRECTORY,

        NETWORK_URI
    }

    public static OperatingSystemType osType() {
        // TODO possible incomplete
        String osName = System.getProperty("os.name");
        if (osName == null || osName.isEmpty()) {
            return null;
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
        return OperatingSystemType.EMBEDDED;
    }

}
