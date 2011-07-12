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
package no.nr.lancelot.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SemanticsMap {
    public interface AttributeFlagFinder {
        int getAttributeCount();
        int translate(final String attributeName);
    }

    @SuppressWarnings("serial")
    public static class SemanticsMapInitException extends Exception {
        private SemanticsMapInitException(final Throwable t) {
            super(t);
        }
        
        private SemanticsMapInitException(final String msg) {
            super(msg);
        }
    }

    private final Map<Integer, List<String>> profileToSuggestionMap;

    public SemanticsMap(final File mapFile, final AttributeFlagFinder finder) 
    throws SemanticsMapInitException {
        if (mapFile == null) {
            throw new IllegalArgumentException();
        }
        
        if (finder == null) {
            throw new IllegalArgumentException();
        }
        
        try {
            final BufferedReader fileReader = new BufferedReader(new FileReader(mapFile));
                        
            final int[] attributeMapping = parseAttributes(fileReader, finder);
            this.profileToSuggestionMap = Collections.unmodifiableMap(
                parseSuggestions(fileReader, attributeMapping)
            );
        } catch (Exception e) {
            throw new SemanticsMapInitException(e);
        }
    }
    
    public List<String> findSuggestionsFor(final int semanticProfile) {
        final boolean knowsProfile = profileToSuggestionMap.containsKey(semanticProfile);
        if (knowsProfile)
            return profileToSuggestionMap.get(semanticProfile);
        return Collections.emptyList();
    }

    protected static int[] parseAttributes(
        final BufferedReader reader, 
        final AttributeFlagFinder finder
    ) throws IOException, SemanticsMapInitException {
        final int[] res = new int[finder.getAttributeCount()];
        
        final String attributesHeaderLine = reader.readLine();
        if (!"##ATTRIBUTES##".equals(attributesHeaderLine)) {
            throw new SemanticsMapInitException("Invalid start of file. Expected '##ATTRIBUTES##'");
        }
        
        for (int mapFileFlagId = 0; mapFileFlagId < finder.getAttributeCount(); ++mapFileFlagId) {
            final String attributeName = reader.readLine();
            
            if (attributeName == null) {
                throw new SemanticsMapInitException("Premature EOF!");
            }
            
            final int jarmonyAttributeFlag = finder.translate(attributeName);
            res[mapFileFlagId] = jarmonyAttributeFlag;
        }
        
        return res;
    }
    
    private static Map<Integer, List<String>> parseSuggestions(
        final BufferedReader reader, 
        final int[] attributeMapping
    ) throws IOException, SemanticsMapInitException {
        final Map<Integer, List<String>> res = new HashMap<Integer, List<String>>();

        final String profilesHeaderLine = reader.readLine();
        if (!"##PROFILES##".equals(profilesHeaderLine)) {
            throw new SemanticsMapInitException("Invalid input. Expected '##PROFILES##'");
        }
        
        for (;;) {
            final String line = reader.readLine();
            final boolean isEOF = line == null;
            if (isEOF) {
                return res;
            }

            final String[] parts = line.split("\\s+");
            
            final int mapFileProfile = Integer.parseInt(parts[0]);
            final int jarmonyProfile = convertProfile(mapFileProfile, attributeMapping);
            
            final List<String> suggestions = new ArrayList<String>();
            for (int i = 1; i < parts.length; ++i) {
                suggestions.add(parts[i]);
            }
            
            res.put(jarmonyProfile, Collections.unmodifiableList(suggestions));
        }
    }

    protected static int convertProfile(final int mapFileProfile, final int[] attributeMapping) {
        int res = 0;
        
        for (int i = 0; i < attributeMapping.length; ++i) {
            if ((mapFileProfile & (1 << i)) != 0) {
                res |= attributeMapping[i];
            }
        }
        
        return res;
    }
}
