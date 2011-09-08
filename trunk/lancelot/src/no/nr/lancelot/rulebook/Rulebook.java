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

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Rulebook implements IRulebook {
    @SuppressWarnings("serial")
    public static final class RulebookInitException extends Exception {
        private RulebookInitException(final Throwable t) {
            super(t);
        }
    }

    private final Map<String, Phrase> phrases;

    public Rulebook(final URL ruleBookUrl) throws RulebookInitException {
        if (ruleBookUrl == null) {
            throw new RulebookInitException(
                new IllegalArgumentException("ruleBookUrl cannot be null")
            );
        }

        try {
            phrases = new HashMap<String, Phrase>(2000, 0.5f);
            addRoot();
            addPhrases(new RulebookParser(ruleBookUrl).getParsedPhrases());
        } catch (Exception e) {
            throw new RulebookInitException(e);
        }
    }

    // For testing.
    protected Rulebook(final Map<String, Phrase> phrases) {
        this.phrases = new HashMap<String, Phrase>(phrases);
    }

    private void addRoot() {
        phrases.put(IRulebook.ROOT.getPhraseText(), IRulebook.ROOT);
    }
    
    private void addPhrases(final List<Phrase> phrases_) {
        for (Phrase p : phrases_) {
            phrases.put(p.getPhraseText(), p);
        }
    }
    
    @Override
    public IRulebookLookupResult lookup(final MethodIdea methodIdea) {
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
        
        return new RulebookLookupResult(bestMatchingPhrase, rules, violations);
    }
    
    public Phrase find(final MethodIdea methodIdea) {
        final int partCount = methodIdea.getPhrase().length();
        
        String acc = "";
        String phraseText = null;
        
        final Iterator<NamePart> namePartIter = methodIdea.getPhrase().iterator();
        for (int i = 0; i < partCount; ++i) {
            final NamePart namePart = namePartIter.next(); 
            final String textPart = namePart.getText();
            final String tagPart = namePart.getTag();

            if (i > 0) {
                acc = acc + "-";
            }

            String temp = acc + textPart;
            String key = temp;
            if ((i + 1) == partCount && phrases.containsKey(key)) {
                acc = temp;
                phraseText = key;
                continue;
            }

            key = key + "-*";
            if (phrases.containsKey(key)) {
                acc = temp;
                phraseText = key;
                continue;
            }

            temp = acc + "[" + tagPart + "]";
            key = temp;
            if ((i + 1) == partCount && phrases.containsKey(key)) {
                acc = temp;
                phraseText = key;
                continue;
            }

            key = key + "-*";
            if (phrases.containsKey(key)) {
                acc = temp;
                phraseText = key;
                continue;
            }

            temp = acc + "[/" + tagPart + "]";
            key = temp;
            if ((i + 1) == partCount && phrases.containsKey(key)) {
                acc = temp;
                phraseText = key;
                continue;
            }

            key = key + "-*";
            if (phrases.containsKey(key)) {
                acc = temp;
                phraseText = key;
                continue;
            }

            break;
        }
        
        if (phraseText == null)
            return IRulebook.ROOT;
        
        final String param_part;
        if (methodIdea.getParamType().equals("")) { 
            param_part = "()";
        } else {
            param_part = "(" + methodIdea.getParamType() + "..." + ")";
        }
        
        final String return_part = methodIdea.getReturnType() + " ";

        String ppKey = return_part + phraseText + param_part;
        if (phrases.containsKey(ppKey)) {
            return phrases.get(ppKey);
        }

        ppKey = return_part + phraseText;
        if (phrases.containsKey(ppKey)) {
            return phrases.get(ppKey);
        }

        ppKey = phraseText + param_part;
        if (phrases.containsKey(ppKey)) {
            return phrases.get(ppKey);
        }
        
        ppKey = phraseText;
        return phrases.get(ppKey);
    } 
}
