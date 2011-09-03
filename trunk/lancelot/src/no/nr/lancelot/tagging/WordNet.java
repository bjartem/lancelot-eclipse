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
package no.nr.lancelot.tagging;

import no.nr.lancelot.frontend.LancelotRegistry;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;

public final class WordNet {
    
    private final IDictionary wnDict = LancelotRegistry.getInstance().getWordnetDictionary();
    
    public boolean isNoun(final String s) {
        return isWordClass(s, POS.NOUN);
    }

    public boolean isVerb(final String s) {
        return isWordClass(s, POS.VERB);
    }
    
    public boolean isAdjective(final String s) {
        return isWordClass(s, POS.ADJECTIVE);
    }
    
    public boolean isAdverb(final String s) {
        return isWordClass(s, POS.ADVERB);
    }
    
    private boolean isWordClass(final String s, final POS pos) {
        return wnDict.getIndexWord(s, pos) != null;
    }

}
