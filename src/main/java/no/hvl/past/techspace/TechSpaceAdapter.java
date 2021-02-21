package no.hvl.past.techspace;

import no.hvl.past.graph.GraphMorphism;
import no.hvl.past.graph.Sketch;
import no.hvl.past.graph.Star;
import no.hvl.past.graph.trees.QueryHandler;
import no.hvl.past.names.Name;
import no.hvl.past.plugin.UnsupportedFeatureException;
import no.hvl.past.server.WebserviceRequestHandler;

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

    Sketch parseSchema(Name schemaName, String schemaLocationURI) throws TechSpaceException, UnsupportedFeatureException;

    TechSpaceDirective directives();

    void writeSchema(Sketch formalSchemaRepresentation, OutputStream outputStream) throws TechSpaceException, UnsupportedFeatureException;

    QueryHandler queryHandler(String locationURI, String schemaLocationURI) throws TechSpaceException, UnsupportedFeatureException;

    WebserviceRequestHandler federationQueryHandler(Sketch comprehensiveSchema, List<GraphMorphism> embeddings, List<QueryHandler> localQueryHandlers) throws TechSpaceException, UnsupportedFeatureException;

}
