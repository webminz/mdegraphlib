package no.hvl.past.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuiltinPlugins {


    @Bean
    public PlottingTechSpace.AdapterFactory plotPNGTechSpace() {
        return new PlottingTechSpace.AdapterFactory(PlottingTechSpace.PNG);
    }

    @Bean
    public PlottingTechSpace.AdapterFactory plotSVGTechSpace() {
        return new PlottingTechSpace.AdapterFactory(PlottingTechSpace.SVG);
    }

    @Bean
    public PlottingTechSpace.AdapterFactory plotEPSTechSpace() {
        return new PlottingTechSpace.AdapterFactory(PlottingTechSpace.EPS);
    }

    // TODO register beans for all the builtin stuff




}
