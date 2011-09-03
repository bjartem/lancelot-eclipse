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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nr.lancelot.frontend.LancelotRegistry;
import no.nr.lancelot.frontend.SemanticsMap.SemanticsMapInitException;
import no.nr.lancelot.rulebook.Rulebook.RulebookInitException;
import no.nr.lancelot.tagging.LingoReader.LingoInitException;

public class LancelotTestUtils {
    public static final String CLASS_LOCATION, 
                               CLASS_DIRECTORY_PATH, 
                               LANCELOT_INSTALL_PATH, 
                               RESOURCES_URL_PREFIX,
                               TEST_RESOURCES_URL_PREFIX;

    public static final URL RULEBOOK_URL,
                            WORDNET_URL;
    public static final File LINGO_FILE,
                             REVERSE_MAP_FILE;
    
    static {
        CLASS_LOCATION = LancelotTestUtils.class.getResource(
            LancelotTestUtils.class.getSimpleName() + ".class"
        ).getFile();
        CLASS_DIRECTORY_PATH = new File(CLASS_LOCATION).getParent();
        LANCELOT_INSTALL_PATH = CLASS_DIRECTORY_PATH.replaceAll("bin/.*", "");
        
        RESOURCES_URL_PREFIX = "file://" + LANCELOT_INSTALL_PATH + "resources/";
        TEST_RESOURCES_URL_PREFIX = "file://" + LANCELOT_INSTALL_PATH + "test-resources/";
        
        RULEBOOK_URL = createUrl(RESOURCES_URL_PREFIX + "rules.xml");
        WORDNET_URL = createUrl(RESOURCES_URL_PREFIX + "wordnet-3-dict/");
        LINGO_FILE = new File(LANCELOT_INSTALL_PATH + "resources/manual_dict.txt");
        REVERSE_MAP_FILE = new File(LANCELOT_INSTALL_PATH + "resources/reverse_map.txt");
    }
    
    public static void loadProductionConfiguration() 
    throws RulebookInitException, LingoInitException, SemanticsMapInitException {
        if (!LancelotRegistry.isInitialized())
            LancelotRegistry.initialize(RULEBOOK_URL, WORDNET_URL, LINGO_FILE, REVERSE_MAP_FILE);
    }

    public static URL createUrl(final String spec) {
        try {
            return new URL(spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> List<T> createList(final T... objects) {
        return Arrays.asList(objects);
    }
    
    public static <T> Set<T> createSet(final T... objects) {
        return new HashSet<T>(createList(objects));
    }
}
