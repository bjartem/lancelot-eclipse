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

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import no.nr.einar.naming.rulebook.Rulebook;
import no.nr.einar.naming.rulebook.Rulebook.RulebookInitException;
import no.nr.einar.naming.tagging.LingoReader;
import no.nr.einar.naming.tagging.LingoReader.LingoInitException;
import no.nr.einar.naming.tagging.Tag;
import no.nr.einar.pb.model.Attribute;
import no.nr.lancelot.analysis.SemanticsMap.SemanticsMapInitException;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import static no.nr.lancelot.analysis.SemanticsMap.AttributeFlagFinder;

public final class LancelotRegistry {
    private static LancelotRegistry INSTANCE = null;
    
    private final Rulebook rulebook;
    private final IDictionary wordnetDictionary;
    private final Map<String, Set<Tag>> lingoDictionary;
    private final SemanticsMap semanticsMap;
    
    private LancelotRegistry(
        final URL rulebookUrl, 
        final URL wordnetDictionaryUrl, 
        final File lingoFile,
        final File semanticMapFile
    ) throws RulebookInitException, LingoInitException, SemanticsMapInitException {
        if (rulebookUrl == null) {
            throw new IllegalArgumentException("rulebookUrl cannot be null");
        }
        
        if (wordnetDictionaryUrl == null) {
            throw new IllegalArgumentException("wordnetDictionaryUrl cannot be null");
        }
        
        if (lingoFile == null) {
            throw new IllegalArgumentException("lingoFile cannot be null");
        }
        
        if (semanticMapFile == null) {
            throw new IllegalArgumentException("semanticMapFile cannot be null");
        }
        
        this.rulebook = createRulebook(rulebookUrl);
        this.wordnetDictionary = createWordnetDictionary(wordnetDictionaryUrl);
        this.lingoDictionary = createLingoDictionary(lingoFile);
        this.semanticsMap = createSemanticMap(semanticMapFile);
    }
    
    private Rulebook createRulebook(final URL rulebookUrl) throws RulebookInitException {
        return new Rulebook(rulebookUrl);
    }
    
    private IDictionary createWordnetDictionary(final URL wordnetDictionaryUrl) {
        final IDictionary dict = new Dictionary(wordnetDictionaryUrl);
        dict.open();
        return dict;
    }
    
    private Map<String, Set<Tag>> createLingoDictionary(final File lingoFile)
    throws LingoInitException {
        return new LingoReader(lingoFile).read();
    }

    private SemanticsMap createSemanticMap(final File semanticMapFile) 
    throws SemanticsMapInitException {
        return new SemanticsMap(semanticMapFile, new AttributeFlagFinder() {
            @Override
            public int translate(final String attributeName) {
                return Attribute.valueOf(attributeName).getFlag();
            }
            
            @Override
            public int getAttributeCount() {
                return Attribute.values().length;
            }
        });
    }

    public IDictionary getWordnetDictionary() {
        return wordnetDictionary;
    }
    
    public Map<String, Set<Tag>> getLingoDictionary() {
        return lingoDictionary;
    }

    public Rulebook getRulebook() {
        return rulebook;
    }
    
    public SemanticsMap getSemanticsMap() {
        return semanticsMap;
    }

    public static void initialize(
            final URL rulebookUrl, 
            final URL wordnetDictUrl, 
            final File lingoFile,
            final File semanticMapFile
    ) throws RulebookInitException, LingoInitException, SemanticsMapInitException {
        if (isInitialized())
            throw new RuntimeException("Already initialized!");
        INSTANCE = new LancelotRegistry(rulebookUrl, wordnetDictUrl, lingoFile, semanticMapFile);
    }
    
    public static LancelotRegistry getInstance() {
        if (!isInitialized())
            throw new RuntimeException("Lancelot is not initialized yet!");
        return INSTANCE;
    }
    
    public static boolean isInitialized() {
        return INSTANCE != null;
    }
}
