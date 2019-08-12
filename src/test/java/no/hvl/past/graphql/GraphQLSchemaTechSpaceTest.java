package no.hvl.past.graphql;

import com.google.common.io.Resources;
import no.hvl.past.graph.AbstractMorphism;
import no.hvl.past.graph.names.Name;
import no.hvl.past.graph.operations.DiagrammaticGraph;
import no.hvl.past.graph.techspace.UnsupportedException;
import no.hvl.past.util.Pair;
import org.junit.Test;

import java.io.File;

public class GraphQLSchemaTechSpaceTest {


    @Test
    public void testReadin() throws UnsupportedException {
        String path = "/Users/past/Documents/dev/graphql/graphql-java/src/test/resources/starWarsSchema.graphqls";
        GraphQLSchemaTechSpace space = new GraphQLSchemaTechSpace();

        Pair<AbstractMorphism, DiagrammaticGraph> result = space.load(path);

        result.getFirst().getDomain().outgoing(Name.identifier("Human")).forEach(System.out::println);


    }

}
