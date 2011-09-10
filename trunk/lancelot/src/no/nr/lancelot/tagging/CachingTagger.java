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
package no.nr.lancelot.tagging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CachingTagger implements PosTagger {
    private final Map<String, List<String>> memo = new HashMap<String, List<String>>();
    private final PosTagger tagger = new JavaTagger();
    
    public List<String> tag(final List<String> fragments) {
    	if (fragments == null) {
    		throw new IllegalArgumentException("fragments cannot be null!");
    	}
    	
    	final String key = keyFor(fragments);
    	
    	List<String> tagResult = memo.get(key);
    	
    	final boolean isSeenBefore = tagResult != null;
    	if (isSeenBefore) {
    		return tagResult;
    	}
    	
    	tagResult = tagger.tag(fragments);
    	memo.put(key, tagResult);
    	return tagResult;
    }
    
    private static String keyFor(final List<String> fragments) {
        final StringBuffer sb = new StringBuffer();
        for (final String f : fragments) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(f);
        }
        return sb.toString();
    }
}
