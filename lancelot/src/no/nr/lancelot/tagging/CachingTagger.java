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
    
    private static String getName(final List<String> fragments) {
        final StringBuffer $ = new StringBuffer();
        for (final String f : fragments) {
            if ($.length() > 0) {
                $.append("-");
            }
            $.append(f);
        }
        return $.toString();
    }

    public List<String> tag(final List<String> fragments) {
        final String name = getName(fragments);
        
        List<String> tagResult = memo.get(name);

        final boolean isSeenBefore = tagResult != null;
        if (isSeenBefore) {
            return tagResult;
        }
        
        tagResult = tagger.tag(fragments);
        memo.put(name, tagResult);
        return tagResult;
    }
}
