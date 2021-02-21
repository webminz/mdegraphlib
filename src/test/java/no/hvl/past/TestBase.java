package no.hvl.past;

import no.hvl.past.util.ShouldNotHappenException;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Logger;


import static org.junit.Assume.assumeTrue;

public class TestBase {

    private static final String ENABLED_HOSTS_FILE = "integration_enabled_hosts.properties";
    private static boolean testsWithExternalComponentsEnabled = false;
    private static Logger logger = Logger.getLogger(TestBase.class.getName());

    protected void logInfo(String info) {
        logger.info(info);
    }

    protected void logWarning(String warning) {
        logger.warning(warning);
    }

    protected void logException(Throwable ex) {
        logger.throwing(getClass().getName(), Thread.currentThread().getStackTrace()[2].getMethodName(), ex);
    }

    protected static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.throwing(TestBase.class.getName(), "getHostname", e);
            throw new ShouldNotHappenException(TestBase.class, "getHostname", e);
        }
    }

    protected static File getResourceFolderItem(String path) {
        try {
            return new ClassPathResource(path).getFile();
        } catch (IOException e) {
            logger.throwing(TestBase.class.getName(), "getHostname", e);
            throw new ShouldNotHappenException(TestBase.class, "getResourceFolderItem", e);
        }
    }

    @BeforeClass
    public static void setUpEnvironment() throws Exception {
        String hostname = getHostname();
        Properties properties = new Properties();
        properties.load(new FileInputStream(getResourceFolderItem(ENABLED_HOSTS_FILE)));

        if (properties.containsKey(hostname) && properties.getProperty(hostname).toLowerCase().equals("true")) {
            logger.info("Host '" + hostname + "' is allowed to execute tests who depend on external resources. Integration tests are enabled!");
            testsWithExternalComponentsEnabled = true;
        } else {
            logger.info("Host '" + hostname + "' is not allowed to execute tests who depend on external resources. Integration tests are disabled!");
            testsWithExternalComponentsEnabled = false;
        }
    }

    protected String getTestName() {
        String className = getClass().getCanonicalName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return className + "." + methodName + "()";
    }

    protected boolean integrationTestsAllowed() {
        return testsWithExternalComponentsEnabled;
    }

    protected void requiresSpecificInstalledComponents() {
        if (!integrationTestsAllowed()) {
            logInfo("Integration Tests are not enabled on the current system. Test '" + getTestName() + "' will not be executed" );
        }
        assumeTrue(testsWithExternalComponentsEnabled);
    }

}
