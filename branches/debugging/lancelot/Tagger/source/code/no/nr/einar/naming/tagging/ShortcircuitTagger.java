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
public final class ShortcircuitTagger extends ChainTagger {
    
    public ShortcircuitTagger(final FragmentTagger nextTagger) {
        super(nextTagger);
    }
    
    private final String[] nouns = new String[] { 
        "action", "mouse", "key", "value", "state", "size", "index", "item", "message", "bean", "model", "result", "table", "document", "input"
    };
    
    private final String[] verbs = new String[] {
        "is", "has", "equals", "exists", "contains", "matches", "needs", "save", "was", "supports"
    };
    
    private final String[] adjectives = new String[] { 
        "next", "best", "first", "second", "third", "last", "initial", "editable", "empty"
    };
    
    private final Set<String> knownNouns = new HashSet<String>() {{
        for (int i = 0; i < nouns.length; i++) {
            add(nouns[i]);
        }
    }};
    
    private final Set<String> knownVerbs = new HashSet<String>() {{
        for (int i = 0; i < verbs.length; i++) {
            add(verbs[i]);
        }
    }};
    
    private final Set<String> knownAdjectives = new HashSet<String>() {{
        for (int i = 0; i < adjectives.length; i++) {
            add(adjectives[i]);
        }
    }};
    
    private final Set<String> adjectiveOrNoun = new HashSet<String>() {{
        add("default");
        add("original");
    }};
    
    @Override
    public List<Tag> tag(final String fragment) {
        debug(this.getClass().toString());
        
        final List<Tag> $ = new ArrayList<Tag>();
        if (isNumber(fragment)) {
            $.add(Tag.Number);
            return $;
        }
        if (isAcronym(fragment)) {
            $.add(Tag.Noun);
            return $;
        }
        if (isKnownNoun(fragment)) {
            $.add(Tag.Noun);
            return $;
        }
        if (isKnownVerb(fragment)) {
            $.add(Tag.Verb);
            return $;
        }
        if (isKnownAdjective(fragment)) {
            $.add(Tag.Adjective);
            return $;
        }
        if (isAdjectiveOrNoun(fragment)) {
            $.add(Tag.Adjective);
            $.add(Tag.Noun);
            return $;
        }
        $.addAll(nextTagger.tag(fragment));
        
        debug(this.getClass().toString() + " : " + $.size());
        
        return $;
    }

    private boolean isNumber(final String fragment) {
        // Trust the name decomposer to work properly.
        // If one character is a digit, then all should be.
        return Character.isDigit(fragment.charAt(0));
    }
    
    private boolean isAcronym(final String fragment) {
        // Trust the name decomposer to work properly.
        // If last character is uppercase, then all should be.
        return Character.isUpperCase(fragment.charAt(fragment.length() - 1));
    }
    
    private boolean isKnownNoun(final String fragment) {
        return knownNouns.contains(fragment);
    }
    
    private boolean isKnownVerb(final String fragment) {
        return knownVerbs.contains(fragment);
    }
    
    private boolean isKnownAdjective(final String fragment) {
        return knownAdjectives.contains(fragment);
    }
    
    private boolean isAdjectiveOrNoun(final String fragment) {
        return adjectiveOrNoun.contains(fragment);
    }

}
