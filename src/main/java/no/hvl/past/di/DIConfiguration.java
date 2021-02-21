package no.hvl.past.di;

import no.hvl.past.graph.Universe;
import no.hvl.past.graph.UniverseImpl;
import no.hvl.past.plugin.MetaRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.Properties;

@Configuration
public class DIConfiguration {

    @Bean
    @Scope("singleton")
    public MetaRegistry registry() {
       return new RegistryImpl();
    }

    @Bean
    @Scope("prototype")
    public Universe universe() {
        return new UniverseImpl(UniverseImpl.EMPTY);
    }

    @Bean
    @Scope("singleton")
    public PropertyHolder propertyHolder() {
        return new PropertyHolder(new Properties());
    }

    @Bean
    @Scope("singleton")
    @Lazy
    public ServerStarter serverStarter() {
        return new ServerStarter();
    }


}
