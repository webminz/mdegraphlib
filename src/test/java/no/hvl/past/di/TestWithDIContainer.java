package no.hvl.past.di;

import no.hvl.past.TestBase;
import org.junit.Before;

import java.util.Properties;

public abstract class TestWithDIContainer extends TestBase {

    private DependencyInjectionContainer diContainer;

    public DependencyInjectionContainer getDiContainer() {
        return diContainer;
    }

    protected void configure(Properties properties) {
        String resourceRoot =  getResourceFolderItem(".").getAbsolutePath();
        properties.put(PropertyHolder.BASE_DIR, resourceRoot);
        properties.put(PropertyHolder.SSL_ALLOW_ALL, true);
    }

    @Before
    public void setUp() throws Exception {
        Properties p = new Properties();
        configure(p);
        diContainer = DependencyInjectionContainer.create("UnitTest", p);

    }
}
