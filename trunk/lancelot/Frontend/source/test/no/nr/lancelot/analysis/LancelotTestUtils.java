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
import java.net.MalformedURLException;
import java.net.URL;

import no.nr.einar.naming.rulebook.Rulebook.RulebookInitException;
import no.nr.einar.naming.tagging.LingoReader.LingoInitException;
import no.nr.lancelot.analysis.LancelotRegistry;
import no.nr.lancelot.analysis.SemanticsMap.SemanticsMapInitException;

public class LancelotTestUtils {
    private static final URL RULEBOOK_URL,
                              WORDNET_URL;
    private static final File LINGO_FILE,
                              REVERSE_MAP_FILE;
    
    static {
        try {
            final String classLocation = LancelotTestUtils.class.getResource(
                                             LancelotTestUtils.class.getSimpleName() + ".class"
                                         ).getFile();
            final String classDirectoryPath = new File(classLocation).getParent();
            final String lancelotInstallPath = classDirectoryPath.replaceAll("bin/.*", "");
            final String resourcesUrlPrefix = "file://" + lancelotInstallPath + "resources/";
            
            RULEBOOK_URL = new URL(resourcesUrlPrefix + "rules.xml");
            WORDNET_URL = new URL(resourcesUrlPrefix + "wordnet-3-dict/");
            LINGO_FILE = new File(lancelotInstallPath + "resources/manual_dict.txt");
            REVERSE_MAP_FILE = new File(lancelotInstallPath + "resources/reverse_map.txt");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e + " throwed in initialization!");
        }
    }
    
    public static void loadDefaultConfiguration() 
    throws RulebookInitException, LingoInitException, SemanticsMapInitException {
        if (!LancelotRegistry.isInitialized())
            LancelotRegistry.initialize(RULEBOOK_URL, WORDNET_URL, LINGO_FILE, REVERSE_MAP_FILE);
    }
}
