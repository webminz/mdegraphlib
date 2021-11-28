package no.hvl.past.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuiltinPlugins {


    @Bean
    public PlottingTechSpace.AdapterFactory plotPNGTechSpace() {
        return new PlottingTechSpace.AdapterFactory(PlottingTechSpace.IMAGE);
    }


    // TODO register beans for all the builtin stuff




}
