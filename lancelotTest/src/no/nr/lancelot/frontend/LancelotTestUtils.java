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
    public static final String RESOURCES_URI_PREFIX,
                               TEST_RESOURCES_URL_PREFIX;

    private static final URL RULEBOOK_URI,
                             WORDNET_URL;
    private static final File LINGO_FILE,
                              REVERSE_MAP_FILE;
    
    static {
        final String classLocation = LancelotTestUtils.class.getResource(
            LancelotTestUtils.class.getSimpleName() + ".class"
        ).getFile();
        final File classDirectory = new File(classLocation).getParentFile();
        
        final File LANCELOT_TEST_INSTALL_PATH = findAncestorByName(classDirectory, "lancelotTest");
        final File LANCELOT_INSTALL_PATH = findFileByName(
            LANCELOT_TEST_INSTALL_PATH.getParentFile(), 
            "lancelot"
        );
        
        RESOURCES_URI_PREFIX = toURIString(
            findFileByName(
                LANCELOT_INSTALL_PATH, 
                "resources"
            )
        );
        TEST_RESOURCES_URL_PREFIX = toURIString(
        	findFileByName(
        		LANCELOT_TEST_INSTALL_PATH, 
        		"test-resources"
        	)
        );
        
        RULEBOOK_URI = createURI(RESOURCES_URI_PREFIX + "rules.xml");
        WORDNET_URL = createURI(RESOURCES_URI_PREFIX + "wordnet-3-dict/");
        
        final File resourcesDir = findFileByName(LANCELOT_INSTALL_PATH, "resources");
        LINGO_FILE = findFileByName(resourcesDir, "manual_dict.txt"); 
        REVERSE_MAP_FILE = findFileByName(resourcesDir, "reverse_map.txt"); 
    }
    
    public static void loadProductionConfiguration() 
    		throws RulebookInitException, LingoInitException, SemanticsMapInitException {
    	if (!LancelotRegistry.isInitialized())
    		LancelotRegistry.initialize(RULEBOOK_URI, WORDNET_URL, LINGO_FILE, REVERSE_MAP_FILE);
    }
    
    private static File findFileByName(final File parentFile, final String name) {
    	for (final File child : parentFile.listFiles()) {
    		if (child.getName().equals(name)) {
    			return child;
    		}
    	}
    	throw new RuntimeException("Could not locate file by name: '" + name + "'.");
    }
    
    private static File findAncestorByName(final File file, final String name) {
    	return file.getName().equals(name) ? 
    		file : 
    		findAncestorByName(file.getParentFile(), name);
    }
    
    private static String toURIString(final File file) {
		return file.toURI().toString();
	}

    public static URL createURI(final String spec) {
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
