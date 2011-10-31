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
package no.nr.lancelot.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.nr.lancelot.rulebook.IRulebook;
import no.nr.lancelot.rulebook.MethodIdea;
import no.nr.lancelot.rulebook.MethodPhrase;
import no.nr.lancelot.tagging.JavaTagger;
import no.nr.lancelot.tagging.PosTagger;

public final class SemanticsMap {
    protected static final Pattern TAG_PATTERN = Pattern.compile("^\\[/?(\\w+)\\]$");
    private static final int MAX_SUGGESTION_COUNT = 4;
    
    @SuppressWarnings("serial")
    public static class SemanticsMapInitException extends Exception {
        private SemanticsMapInitException(final Throwable t) {
            super(t);
        }
        
        private SemanticsMapInitException(final String msg) {
            super(msg);
        }
    }

    private final Map<Long, List<String>> profileToSuggestionMap;

    public SemanticsMap(final File mapFile) 
    throws SemanticsMapInitException {
        if (mapFile == null) {
            throw new IllegalArgumentException();
        }
        
        try {
            final BufferedReader fileReader = new BufferedReader(new FileReader(mapFile));
            this.profileToSuggestionMap = Collections.unmodifiableMap(
                parseSuggestions(fileReader)
            );
        } catch (Exception e) {
            throw new SemanticsMapInitException(e);
        }
    }
    
    public List<String> findSuggestionsFor(final MethodIdea methodIdea) {
        if (methodIdea == null) {
            throw new IllegalArgumentException();
        }
        
        final long semantics = methodIdea.getSemantics();
        
        final boolean knowsProfile = profileToSuggestionMap.containsKey(semantics);
        if (knowsProfile) {
            return filterViableSuggestions(
                profileToSuggestionMap.get(semantics), 
                methodIdea,
                LancelotRegistry.getInstance().getRulebook()
            );
        }

        return Collections.emptyList();
    }

    protected static List<String> filterViableSuggestions(
        final List<String> suggestions, 
        final MethodIdea originalMethodIdea,
        final IRulebook rulebook
    ) {
        final List<String> res = new LinkedList<String>();
        
        for (final String suggestion : suggestions) {
            res.add(suggestion);
            if (res.size() >= MAX_SUGGESTION_COUNT) {
                break;
            }
        }
        
        return res;
    }
    
    protected static MethodPhrase createPhrase(final String suggestion) {
        final PosTagger tagger = new JavaTagger();
        
        final List<String> fragments = new LinkedList<String>(),
                           tags = new LinkedList<String>();
        
        for (final String part : suggestion.split("-")) {
            if (part.equals("*")) {
                fragments.add("foo");
                tags.add("noun"); // Could be anything.
                continue;
            } 
            
            final Matcher m = TAG_PATTERN.matcher(part);
            if (m.matches()) {
                fragments.add("foo");
                tags.add(m.group(1));
                continue;
            }
            
            assert ! part.contains("[");
            assert ! part.contains("/");
            
            fragments.add(part);
            tags.add(tagger.tag(Arrays.asList(new String[]{ part })).get(0));
        }
        
        return new MethodPhrase(fragments, tags);
    }

    private static Map<Long, List<String>> parseSuggestions(
        final BufferedReader reader
    ) throws IOException, SemanticsMapInitException {
        final Map<Long, List<String>> res = new HashMap<Long, List<String>>();

        final String profilesHeaderLine = reader.readLine();
        if (!"##PROFILES##".equals(profilesHeaderLine)) {
            throw new SemanticsMapInitException("Invalid input. Expected '##PROFILES##'");
        }
        
        for (;;) {
            final String line = reader.readLine();
            final boolean isAtEndOfFile = line == null;
            if (isAtEndOfFile) {
                return res;
            }

            final String[] parts = line.split("\\s+");
            
            final long profile = Long.parseLong(parts[0]);
            
            final List<String> suggestions = new ArrayList<String>();
            for (int i = 1; i < parts.length; ++i) {
                suggestions.add(parts[i]);
            }
            
            res.put(profile, Collections.unmodifiableList(suggestions));
        }
    }
}
