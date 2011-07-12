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
package no.nr.einar.naming.tagging;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public final class AdditionalWordClassTagger extends ChainTagger {

    public AdditionalWordClassTagger(final FragmentTagger nextTagger) {
        super(nextTagger);
    }

    private final String[] prepositions = {
            "aboard", "about", "above", "absent", "across", "after", "against",
            "along", "alongside", "amid", "amidst", "among", "amongst", "around",
            "as", "aslant", "astride", "at", "atop", "barring", "before", "behind",
            "below", "beneath", "beside", "besides", "between", "beyond", "but", "by",
            "despite", "down", "during", "except", "failing", "following", "for",
            "from", "in", "inside", "into", "like", "mid", "minus", "near", "next",
            "notwithstanding", "of", "off", "on", "onto", "opposite", "outside",
            "over", "past", "per", "plus", "regarding", "round", "save", "since",
            "than", "through", "throughout", "till", "times", "to", "toward", "towards",
            "under", "underneath", "unlike", "until", "up", "upon", "versus", "via",
            "with", "within", "without"
    };

    private final String[] coordinatingConjunctions = {
            "for", "and", "nor", "but", "or", "yet", "so" };

    private final String[] subordinatingConjunctions = {
            "after", "before", "when", "while", "since", "until",
            "because", "as", "so",
            "although", "though", "whereas",
            "if", "unless" 
    };

    private final String[] personalPronouns = {
            "I", "we", "you", "he", "she", "it", "they", "me", "us", "him", "her", "them",
            "myself", "ourself", "ourselves", "yourself", "yourselves", "himself", "herself", 
            "itself", "themselves" 
    };

    private final String[] possessivePronouns = { 
            "mine", "ours", "yours", "his", "hers", "theirs" 
    };

    private final String[] possessiveDeterminers = { 
            "my", "our", "your", "his", "hers", "theirs" 
    };

    private final String[] indefinitePronouns = {
            "all", "another", "any", "anybody", "anyone", "anything", "both", "each", "either", 
            "enough", "every", "everybody", "everyone", "everything", "few", "less", "little", 
            "many", "more", "most", "much", "neither", "nobody", "no", "none", "one", "several", 
            "some", "somebody", "someone", "something" 
    };

    private final String[] demonstrativePronouns = { 
            "that", "this", "such", "these", "those"
    };

    private final String[] interrogativePronouns = { 
            "who", "which", "what"
    };

    private final String[] articles = { 
            "the", "a", "an" 
    };
    
    private static class WordClassSet extends HashSet<String> {
        public void addArray(final String[] array) {
            for (int i = 0; i < array.length; i++) {
                add(array[i]);
            }
        }
    }
    
    private final Set<String> allPrepositions = new WordClassSet() {{
        addArray(prepositions);
    }};
    
    private final Set<String> allPronouns = new WordClassSet() {{
        addArray(personalPronouns);
        addArray(possessivePronouns);
        addArray(possessiveDeterminers);
        addArray(indefinitePronouns);
        addArray(demonstrativePronouns);
        addArray(interrogativePronouns);
    }};
    
    private final Set<String> allConjunctions = new WordClassSet() {{
        addArray(coordinatingConjunctions);
        addArray(subordinatingConjunctions);
    }};
    
    private final Set<String> allArticles = new WordClassSet() {{
        addArray(articles);
    }};

    @Override
    public List<Tag> tag(final String s) {
        debug(this.getClass().toString());
        
        final List<Tag> $ = new ArrayList<Tag>();
        if (isPreposition(s)) {
            $.add(Tag.Preposition);
        }
        if (isPronoun(s)) {
            $.add(Tag.Pronoun);
        }
        if (isConjunction(s)) {
            $.add(Tag.Conjunction);
        }
        if (isArticle(s)) {
            $.add(Tag.Article);
        }
        if ($.isEmpty()) {
            $.addAll(nextTagger.tag(s));
        }
        
        debug(this.getClass().toString() + " : " + $.size());
        
        return $;
    }

    private boolean isPreposition(String s) {
        return allPrepositions.contains(s);
    }

    private boolean isPronoun(String s) {
        return allPronouns.contains(s);
    }

    private boolean isConjunction(String s) {
        return allConjunctions.contains(s);
    }
    
    private boolean isArticle(String s) {
        return allArticles.contains(s);
    }

}
