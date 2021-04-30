package no.hvl.past.di;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.plotting.PlantUMLPlotter;
import no.hvl.past.graph.trees.QueryHandler;
import no.hvl.past.names.Name;
import no.hvl.past.plugin.MetaRegistry;
import no.hvl.past.plugin.UnsupportedFeatureException;
import no.hvl.past.systems.Data;
import no.hvl.past.systems.Sys;
import no.hvl.past.techspace.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

public class PlottingTechSpace implements TechSpace {


    public static final PlottingTechSpace PNG = new PlottingTechSpace("PNG");
    public static final PlottingTechSpace EPS = new PlottingTechSpace("EPS");
    public static final PlottingTechSpace SVG = new PlottingTechSpace("SVG");

    private final String fileFormat;

    private PlottingTechSpace(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String ID() {
        return fileFormat;
    }

    public static final class AdapterFactory implements TechSpaceAdapterFactory<PlottingTechSpace> {

        @Autowired
        MetaRegistry pluginRegistry;

        private final PlottingTechSpace techSpace;

        public AdapterFactory(PlottingTechSpace techSpace) {
            this.techSpace = techSpace;
        }

        @Override
        public void doSetUp() {
        }

        @Override
        public TechSpaceAdapter<PlottingTechSpace> createAdapter() {
            PlantUMLPlotter plotter = new PlantUMLPlotter(techSpace.ID(), true);
            return new Adapter(plotter, techSpace);
        }

        @Override
        public void prepareShutdown() {
        }


        @PostConstruct
        public void setUp() {
            pluginRegistry.register(techSpace.ID(),techSpace);
            pluginRegistry.register(techSpace.ID(), this);
        }
    }


    public static class Adapter implements TechSpaceAdapter<PlottingTechSpace>, TechSpaceDirective {

        private final PlantUMLPlotter plotter;
        private final PlottingTechSpace techSpace;

        public Adapter(PlantUMLPlotter plotter, PlottingTechSpace techSpace) {
            this.plotter = plotter;
            this.techSpace = techSpace;
        }

        @Override
        public TechSpaceDirective directives() {
            return this;
        }

        @Override
        public Sys parseSchema(Name name, String locationURI) throws UnsupportedFeatureException {
            throw new UnsupportedFeatureException();
        }

        @Override
        public void writeSchema(Sys sys, OutputStream outputStream) throws TechSpaceException {
            try {
                plotter.plot(sys, outputStream);
            } catch (IOException e) {
                throw new TechSpaceException(e, techSpace);
            }
        }

        @Override
        public QueryHandler queryHandler(Sys system) throws TechSpaceException, UnsupportedFeatureException {
            throw new UnsupportedFeatureException();
        }

        @Override
        public Data readInstance(Sys system, InputStream inputStream) throws UnsupportedFeatureException {
            throw new UnsupportedFeatureException();
        }

        @Override
        public void writeInstance(Sys system, GraphMorphism instance, OutputStream outputStream) throws TechSpaceException, UnsupportedFeatureException {
            throw new UnsupportedFeatureException(); // TODO add support for this
        }

        @Override
        public Optional<Name> stringDataType() {
            return Optional.empty();
        }

        @Override
        public Optional<Name> boolDataType() {
            return Optional.empty();
        }

        @Override
        public Optional<Name> integerDataType() {
            return Optional.empty();
        }

        @Override
        public Optional<Name> floatingPointDataType() {
            return Optional.empty();
        }

        @Override
        public Stream<Name> implicitTypeIdentities() {
            return Stream.empty();
        }
    }


}
