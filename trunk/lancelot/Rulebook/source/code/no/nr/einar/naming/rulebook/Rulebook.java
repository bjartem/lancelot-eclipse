/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Norwegian Computing Center - initial API and implementation
 ******************************************************************************/
package no.nr.einar.naming.rulebook;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public final class Rulebook implements IRulebook {
    @SuppressWarnings("serial")
    public static final class RulebookInitException extends Exception {
        private RulebookInitException(final Throwable t) {
            super(t);
        }
    }

    private final Phrase root;
    
    public Rulebook(final URL ruleBookUrl) throws RulebookInitException {
    	if (ruleBookUrl == null) {
    		throw new RulebookInitException(
    		    new IllegalArgumentException("ruleBookUrl cannot be null")
    		);
    	}

    	try {
        	root = new RulebookParser(ruleBookUrl).getRoot();
        } catch (Exception e) {
        	throw new RulebookInitException(e);
        }
    }
    
    // For testing.
    protected Rulebook(final Phrase root) {
    	this.root = root;
    }
    
    @Override
	public Set<Rule> findViolations(final MethodIdea methodIdea) {
        if (methodIdea == null) {
            throw new IllegalArgumentException();
        }

        final Phrase bestMatchingPhrase = find(methodIdea);
        final Set<Rule> rules = bestMatchingPhrase.getRules();
        final Set<Rule> violations = new HashSet<Rule>();
        final long semantics = methodIdea.getSemantics();
        
        for (final Rule r : rules) {
            if (r.covers(semantics)) {
                violations.add(r);
            }
        }
        
        return violations;
    }
    
    protected Phrase find(final MethodIdea methodIdea) {
    	return find(methodIdea, root);
    }
    
    protected Phrase find(final MethodIdea methodIdea, final Phrase phrase) {
        for (final Phrase refinement : phrase.getRefinements()) {
            if (refinement.captures(methodIdea)) {
                return find(methodIdea, refinement);
            }
        }
        
        return phrase;
    }    
}
