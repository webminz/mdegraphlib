package no.hvl.past.di;

import no.hvl.past.graph.Universe;
import no.hvl.past.graph.UniverseImpl;
import no.hvl.past.plugin.MetaRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class DIContainerTest {

    @Test
    public void testStartup() throws IOException {
        DependencyInjectionContainer dependencyInjectionContainer = DependencyInjectionContainer.create();
        MetaRegistry pluginRegistry = dependencyInjectionContainer.getPluginRegistry();
        assertNotNull(pluginRegistry);
        assertTrue(pluginRegistry instanceof RegistryImpl);
        Universe universe = dependencyInjectionContainer.getUniverse();
        assertNotNull(universe);
        assertTrue(universe instanceof UniverseImpl);

        dependencyInjectionContainer.setUpLogging();

        Logger logger = LogManager.getLogger(DIContainerTest.class);
        logger.info("Hello: it works!");
    }
}
