package no.nr.lancelot.rulebook;

import java.util.Collections;
import java.util.Set;

public interface IRulebook {
    public static final Phrase ROOT = new Phrase("*", Collections.<Rule>emptySet());
    
    interface IRulebookLookupResult {
        Phrase getBestMatchingPhrase();
        Set<Rule> getRules();
        Set<Rule> getViolations();
        boolean isCovered();
        boolean isBuggy();
    }
    
    IRulebookLookupResult lookup(final MethodIdea methodIdea);
}