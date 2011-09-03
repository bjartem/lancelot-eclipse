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
package no.nr.lancelot.rulebook;

import java.util.Collections;
import java.util.Set;

public final class Phrase {  
    private final String phraseText;
    private final Set<Rule> rules;
    
    public Phrase(final String phraseText, final Set<Rule> rules) {
        this.phraseText= phraseText;
        this.rules = Collections.unmodifiableSet(rules);
    }
    
    public String getPhraseText() {
        return phraseText;
    }
    
    public Set<Rule> getRules() {
        return rules;
    }
    
    @Override
    public int hashCode() {
        return phraseText.hashCode();       
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Phrase))
            return false;
        final Phrase otherPhrase = (Phrase) other;
        return this.phraseText.equals(otherPhrase.phraseText) 
               && this.rules.equals(otherPhrase.rules);
    }
    
    @Override
    public String toString() {
        return phraseText; 
    }
}