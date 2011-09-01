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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;

public final class WordNetDictionary {
    
    private static final IDictionary dict = createDict();
    
    private WordNetDictionary() {}
    
    public static IDictionary get() {
        return dict;
    }
    
    private static IDictionary createDict() {
        final String wnhome = "/Users/einar/phd/WordNet-3.0";
        final String path = wnhome + File.separator + "dict"; 
        final URL url = createURL(path);
        final IDictionary dict = new Dictionary(url); 
        dict.open();
        return dict; 
    }

    private static URL createURL(final String path) {
        try {
            return new URL("file", null, path);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to create URL.");
        } 
    }

}
