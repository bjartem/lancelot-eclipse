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

import java.util.ArrayList;
import java.util.List;

public final class WordNetTagger extends ChainTagger {
    
    public WordNetTagger(final FragmentTagger nextTagger) {
        super(nextTagger);
    }

    private final WordNet wn = new WordNet();

    @Override
    public List<Tag> tag(final String s) {
        debug(this.getClass().toString());
        
        final List<Tag> $ = new ArrayList<Tag>();
        if (wn.isNoun(s)) {
            $.add(Tag.Noun);
        }
        if (wn.isVerb(s)) {
            $.add(Tag.Verb);
        }
        if (wn.isAdjective(s)) {
            $.add(Tag.Adjective);
        }
        if (wn.isAdverb(s)) {
            $.add(Tag.Adverb);
        }
        if ($.isEmpty()) {
            $.addAll(nextTagger.tag(s));
        }
        
        debug(this.getClass().toString() + " : " + $.size());
        
        return $;
    }
    
}
