package no.hvl.past.techspace;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.trees.QueryHandler;
import no.hvl.past.names.Name;
import no.hvl.past.plugin.UnsupportedFeatureException;
import no.hvl.past.server.WebserviceRequestHandler;
import no.hvl.past.systems.Data;
import no.hvl.past.systems.Sys;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * An Adaptor translates between the formal mdegraphlib-sketch-graph-representation
 * and a concrete technology.
 *
 * This interfaces defines all possible translations possible.
 * If a concrete TechSpace implementation cannot or does want to implement some functionality
 * it can raise an {@link UnsupportedFeatureException}.
 *
 * A tech space should at least allow to read its schema.
 *
 * @param <X> the type capture representing the actual technological space.
 */
public interface TechSpaceAdapter<X extends TechSpace> {

    TechSpaceDirective directives();

    Sys  parseSchema(Name name, String locationURI) throws TechSpaceException, UnsupportedFeatureException;

    void writeSchema(Sys sys, OutputStream outputStream) throws TechSpaceException, UnsupportedFeatureException;

    QueryHandler queryHandler(Sys system) throws TechSpaceException, UnsupportedFeatureException;

    Data readInstance(Sys system, InputStream inputStream) throws TechSpaceException, UnsupportedFeatureException;

    void writeInstance(Sys system, GraphMorphism instance, OutputStream outputStream) throws TechSpaceException, UnsupportedFeatureException;


}
