package no.hvl.past.graph.names;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PrintingStrategy {

    String empty();

    String sequentialComposition(String fst, String snd);

    String coproduct(String fst, String snd);

    String pullback(String applicant, String target);

    String merge(Collection<String> transformedNames);

    String transform(Name n, String prefix);

    String typedBy(String element, String type);
}
