package no.hvl.past.plugin;

import java.util.Optional;

/**
 * The meta-registry allows to register arbitrary extensions.
 *
 * You can retrieve the instance of the MetaRegistry in you plugin using dependency injection.
 * Simply use the @Autowired annotation.
 */
public interface MetaRegistry {

    /**
     * Registers an extension with the given key.
     */
    <X extends ExtensionPoint> void register(String key, X extension);

    /**
     * Retrieves the Extension with the given key.
     */
    <X extends ExtensionPoint> Optional<X> getExtension(String key, Class<X> extensionType);

    String printPluginInfo();
}
