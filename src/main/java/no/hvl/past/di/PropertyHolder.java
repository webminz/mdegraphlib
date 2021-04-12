package no.hvl.past.di;

import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

public class PropertyHolder {

    public static final String BASE_DIR = "path";
    public static final String CONFIG_FILE = "config";
    public static final String SERVER_HOSTNAME = "server.hostname";
    public static final String SERVER_PORT = "server.port";
    public static final String HIDE_LOGO = "logo.hide";
    public static final String LOG_LOCATION = "log.location";
    public static final String LOG_LEVEL = "log.level";
    public static final String ENABLE_DRAWING_DOT = "enable.drawing.dot";
    private static final String SSL_ALLOW_ALL = "ssl.acceptAll";

    private Properties properties;

    public PropertyHolder(Properties properties) {
        this.properties = properties;
    }

    public File getBaseDir() {
        String base = getProperty(BASE_DIR);
        if (base == null || base.isEmpty()) {
            base = System.getProperty("user.dir");
            setProperty(BASE_DIR, base);
        }
        return new File(base);
    }

    public File getConfigFile() {
        String property = getProperty(CONFIG_FILE);
        if (property == null || property.isEmpty()) {
            property = getBaseDir().getAbsolutePath() + File.pathSeparator + "corrlang.conf";
            setProperty(CONFIG_FILE, property);
            return new File(getBaseDir(), "corrlang.conf");
        } else {
            return new File(property);
        }
    }

    public int getServerPort() {
        int result = 9001; // Default. Its over 9000 ;-)
        String property = this.properties.getProperty(SERVER_PORT);
        if (property == null) {
            property = Integer.toString(result);
        } else {
            try {
                result = Integer.parseInt(property);
            } catch (NumberFormatException e) {
                property = Integer.toString(result);
            }
        }
        setProperty(SERVER_PORT, property);
        return result;
    }

    public boolean isHideLogo() {
        String property = this.properties.getProperty(HIDE_LOGO);
        if (property == null) {
            return false;
        } else {
            return Boolean.parseBoolean(property);
        }
    }


    public String reportContent() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PARAMETERS]");
        stringBuilder.append("\n");
        for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append('=');
            stringBuilder.append(entry.getValue());
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }

    public String getPropertyAndSetDefaultIfNecessary(String property, String defaultValue) {
        if (this.properties.containsKey(property)) {
            return this.properties.getProperty(property);
        } else {
            this.properties.put(property, defaultValue);
            return defaultValue;
        }
    }

    private void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    private String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    public void write(File target) throws IOException {
        if (!target.getParentFile().exists()) {
            target.mkdirs();
        }
        if (target.exists()) {
            target.delete();
        }
        FileOutputStream fos = new FileOutputStream(target);
        properties.store(fos, "Updated at " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        fos.flush();
        fos.close();
    }

    public void load(File configFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(configFilePath);
        this.properties.load(fileInputStream);
    }

    public void bootstrap(File configFilePath) throws IOException {
        this.setProperty(CONFIG_FILE, configFilePath.getAbsolutePath());
        this.setProperty(BASE_DIR, configFilePath.getParentFile().getAbsolutePath());
        this.getLogDir();
        this.getLogLevel();
        this.write(configFilePath);
    }

    public String getLogDir() {
        if (this.properties.containsKey(LOG_LOCATION)) {
            return this.properties.getProperty(LOG_LOCATION);
        } else {
            File baseDir = getBaseDir();
            File logDir = new File(baseDir, "logs");
            logDir.mkdirs();
            this.properties.put(LOG_LOCATION, logDir.getAbsolutePath());
            return logDir.getAbsolutePath();
        }
    }

    public Level getLogLevel() {
        if (this.properties.containsKey(LOG_LEVEL)) {
            switch (this.properties.getProperty(LOG_LEVEL)) {
                case "FATAL" :
                    return Level.FATAL;
                case "ERROR":
                    return Level.ERROR;
                case "WARN":
                    return Level.WARN;
                case "TRACE":
                    return Level.TRACE;
                case "DEBUG":
                    return Level.DEBUG;
                case "OFF":
                    return Level.OFF;
                case "ALL":
                    return Level.ALL;
                default:
                case "INFO":
                    return Level.INFO;
            }
        } else {
            this.properties.put(LOG_LEVEL, "INFO");
            return Level.INFO;
        }
    }

    public String getServerurl() {
        String prefix = "http://"; // TODO later add SSL support
        String hsotname = getServerHostname();
        int serverPort = getServerPort();
        return prefix + hsotname + ":" + serverPort;
    }

    private String getServerHostname() {
        if (properties.containsKey(SERVER_HOSTNAME)) {
            return properties.getProperty(SERVER_HOSTNAME);
        } else {
            this.properties.setProperty(SERVER_HOSTNAME, "127.0.0.1");
            return "127.0.0.1";
        }
    }

    public boolean isSSLAllowAll() {
        if (properties.containsKey(SSL_ALLOW_ALL)) {
            return Boolean.parseBoolean(properties.getProperty(SSL_ALLOW_ALL));
        } else {
            this.properties.setProperty(SSL_ALLOW_ALL, "false");
            return false;
        }
    }
}
