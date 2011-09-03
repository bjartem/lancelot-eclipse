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

import no.nr.lancelot.frontend.LancelotRegistry;

import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public final class MorphyTagger extends ChainTagger {
    
    private final WordnetStemmer stemmer = new WordnetStemmer(
            LancelotRegistry.getInstance().getWordnetDictionary());

    public MorphyTagger(final FragmentTagger nextTagger) {
        super(nextTagger);
    }

    @Override
    public List<Tag> tag(final String s) {
        debug(this.getClass().toString());
        
        final List<Tag> $ = new ArrayList<Tag>();
        if (stemsFrom(s, POS.NOUN)) {
            $.add(Tag.Noun);
        }
        if (stemsFrom(s, POS.VERB)) {
            $.add(Tag.Verb);
            $.add(Tag.Adjective);
        }
        if (stemsFrom(s, POS.ADJECTIVE)) {
            $.add(Tag.Adjective);
        }
        if (stemsFrom(s, POS.ADVERB)) {
            $.add(Tag.Adverb);
        }
        
        debug(this.getClass().toString() + " : " + $.size());
        
        return $;
    }

    private boolean stemsFrom(final String s, final POS pos) {
        final List<String> stems = stemmer.findStems(s, pos);
        return stems.size() > 0;
    }

}
