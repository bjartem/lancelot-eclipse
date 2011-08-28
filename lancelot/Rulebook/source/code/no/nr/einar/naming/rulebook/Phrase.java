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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class Phrase {  
	private final List<IPhrasePart> phraseParts;
	
	private final Type returnType;
	private final ParameterRequirement paramRequirement;
    
	private final List<Phrase> refinements;
    private final Set<Rule> rules;
    
    // Should only be used directly for testing.
    protected Phrase(
    	final List<IPhrasePart> phraseParts, 
    	final Type returnType,
    	final ParameterRequirement parameterRequirement,
    	final List<Phrase> refinements, 
    	final Set<Rule> rules
    ) {
    	this.phraseParts = phraseParts;
    	this.returnType = returnType;
    	this.paramRequirement = parameterRequirement;
    	this.refinements = Collections.unmodifiableList(refinements);
		this.rules = Collections.unmodifiableSet(rules);
    }

    public List<Phrase> getRefinements() {
    	return refinements;
    }
    
    public Set<Rule> getRules() {
    	return rules;
    }
    
    public boolean captures(final MethodIdea methodIdea) {
    	return    paramTypeMatches(methodIdea)
    		   && returnTypeMatches(methodIdea)
    		   && phraseMatches(methodIdea);
    }
    
    private boolean paramTypeMatches(final MethodIdea methodIdea) {
    	return paramRequirement.accepts(methodIdea);
    }

	private boolean returnTypeMatches(final MethodIdea methodIdea) {
		final boolean weAcceptAnyReturnType = this.returnType == Type.ANY,
					  returnTypesMatch = this.returnType == methodIdea.getReturnType();
		return weAcceptAnyReturnType || returnTypesMatch;
	}

	private boolean phraseMatches(final MethodIdea methodIdea) {
        final Iterator<IPhrasePart> pItor = phraseParts.iterator();
        
        IPhrasePart phrasePart = null;
        for (final NamePart namePart : methodIdea.getPhrase()) {
            if (!(phrasePart instanceof WildcardPart)) {
                if (!pItor.hasNext()) {
                    return false;
                }
                phrasePart = pItor.next();
            }
            if (!phrasePart.captures(namePart)) {
                return false;
            }
        }
        
        return true;
    }
    
	@Override
	public int hashCode() {
		int hashCode = 17;
		for (final IPhrasePart pp : phraseParts)
			hashCode *= pp.hashCode();
		return hashCode;		
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other == null)
			return false;
		
		if (!(other.getClass() == Phrase.class))
			return false;
		
		final Phrase otherPhrase = (Phrase) other;
		return    this.returnType.equals(otherPhrase.returnType)
			   && this.phraseParts.equals(otherPhrase.phraseParts)
			   && this.refinements.equals(otherPhrase.refinements)
			   && this.paramRequirement.equals(otherPhrase.paramRequirement)
			   && this.rules.equals(otherPhrase.rules);
	}
    
	@Override
    public String toString() {
    	final StringBuilder sb = new StringBuilder();
    	sb.append("Phrase[returnType: " + returnType + " paramTypeReq: " + paramRequirement + " ");
    	
    	sb.append("phrase: ");
    	for (final IPhrasePart pp : phraseParts) {
    		sb.append(pp);
    	}
    	sb.append(" ");
    	
    	sb.append("refinements: ");
    	for (final Phrase p : refinements) {
    		sb.append(p);
    	}
    	sb.append(" ");
    	
    	sb.append("rules: ");
    	for (final Rule r : rules) {
    		sb.append(r);
    	}
    	
    	sb.append("]");
    	return sb.toString();
    }
	
	/* This method is meant to identify that we have reached the right
	 * phrase when we are testing. 
	 */
	protected String constructSignature() {
		final StringBuilder sb = new StringBuilder();

		sb.append(returnType + "$");
    	for (final IPhrasePart pp : phraseParts) {
    		sb.append(pp + "$");
    	}
    	sb.append(paramRequirement);
    	
    	return sb.toString();
		
	}
}