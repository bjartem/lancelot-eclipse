package no.nr.lancelot.rulebook;

import java.util.Set;

public interface IRulebookLookupResult {

    public abstract Phrase getBestMatchingPhrase();

    public abstract Set<Rule> getRules();

    public abstract Set<Rule> getViolations();

    public abstract boolean isCovered();

}