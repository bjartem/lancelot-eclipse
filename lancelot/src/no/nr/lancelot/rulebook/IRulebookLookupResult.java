package no.nr.lancelot.rulebook;

import java.util.Set;

public interface IRulebookLookupResult {
    Phrase getBestMatchingPhrase();
    Set<Rule> getRules();
    Set<Rule> getViolations();
    boolean isCovered();
}