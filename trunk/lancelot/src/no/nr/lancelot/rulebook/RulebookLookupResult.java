package no.nr.lancelot.rulebook;

import java.util.Set;

import no.nr.lancelot.rulebook.IRulebook.IRulebookLookupResult;

public class RulebookLookupResult implements IRulebookLookupResult {
    private final Phrase bestMatchingPhrase;
    private final Set<Rule> rules;
    private final Set<Rule> violations;

    RulebookLookupResult(
        final Phrase bestMatchingPhrase,
        final Set<Rule> rules, 
        final Set<Rule> violations
    ) {
    	if (bestMatchingPhrase == null) {
    		throw new IllegalArgumentException("bestMatchingPhrase cannot be null!"); 
    	}
    	
    	if (rules == null) {
    		throw new IllegalArgumentException("rules cannot be null!"); 
    	}
    	
    	if (violations == null) {
    		throw new IllegalArgumentException("violations cannot be null!"); 
    	}
        
    	this.bestMatchingPhrase = bestMatchingPhrase;
        this.rules = rules;
        this.violations = violations;
    }

    @Override
    public Phrase getBestMatchingPhrase() {
        return bestMatchingPhrase;
    }

    @Override
    public Set<Rule> getRules() {
        return rules;
    }

    @Override
    public Set<Rule> getViolations() {
        return violations;
    }

    @Override
    public boolean isCovered() {
        return !bestMatchingPhrase.equals(IRulebook.ROOT);
    }
    
    @Override
    public boolean isBuggy() {
        return !violations.isEmpty();
    }
}
