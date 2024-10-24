package no.hvl.past;

import com.google.common.io.Files;
import no.hvl.past.util.IOStreamUtils;
import no.hvl.past.util.ShouldNotHappenException;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;



public class TestBase {

    public static final String CLASS_PATH = "java.class.path";
    private static boolean testsWithExternalComponentsEnabled = false;
    private static Logger logger = Logger.getLogger(TestBase.class.getName());

    private static final String TEST_RESOURCES = "resources/test";

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
            throw new ShouldNotHappenException(TestBase.class, e.getMessage());
        }
    }

    protected File getContainingClasspathElement() {
        try {
            return new File(getClass().getClassLoader().getResource(".").toURI()) // getting the class directory of the current test file via the class loader
                    .getParentFile().getParentFile().getParentFile(); // Three layers up (src/test/java)
        } catch (URISyntaxException | NullPointerException e) {
            throw new ShouldNotHappenException(getClass(), e);
        }
    }

    protected File getTestResource(String path) {
        File containingClasspathElement = getContainingClasspathElement();
        String relatedResourceClasspathElement = new File(containingClasspathElement, TEST_RESOURCES).getAbsolutePath();
        List<String> classpath = Arrays.asList(System.getProperty(CLASS_PATH).split(String.valueOf(File.pathSeparatorChar)));
        if (classpath.contains(relatedResourceClasspathElement)) {
            return new File(relatedResourceClasspathElement, path);
        } else {
            return getResourceFolderItem(path);
        }
    }



    protected static File getResourceFolderItem(String path) {
        try {
            return new File(TestBase.class.getResource(path).getFile());
        } catch (NullPointerException e) {
            logger.throwing(TestBase.class.getName(), "getHostname", e);
            throw new ShouldNotHappenException(TestBase.class, e.getMessage());
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
        //assumeTrue(testsWithExternalComponentsEnabled);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void assertBinaryFileContentAsExpected(File expectedFile, File actualFile) {
        Assertions.assertTrue(expectedFile.exists(), "The 'expected' file '" + expectedFile.getAbsolutePath() + "' does not exist! Did you check that your code actually produces this file?");
        Assertions.assertTrue(actualFile.exists(), "The 'actual' file '" + actualFile.getAbsolutePath() + "' does not exist! Did you forget to add it?");
        try {
            Assertions.assertTrue(Files.equal(actualFile, expectedFile), "Expected file content does not match");
        } catch (IOException e) {
            Assertions.fail("Nested exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void assertTextFileContentAsExpected(File expectedFile, File actualFile) {
        Assertions.assertTrue(expectedFile.exists(), "The 'expected' file '" + expectedFile.getAbsolutePath() + "' does not exist! Did you check that your code actually produces this file?");
        Assertions.assertTrue(actualFile.exists(), "The 'actual' file '" + actualFile.getAbsolutePath() + "' does not exist! Did you forget to add it?");
        try (FileInputStream afis = new FileInputStream(actualFile);
             FileInputStream efils =  new FileInputStream(expectedFile)) {
            Assertions.assertEquals("Text file content mismatch: ", IOStreamUtils.readInputStreamAsString(efils), IOStreamUtils.readInputStreamAsString(afis));
        } catch (IOException e) {
            Assertions.fail("Nested exception: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
