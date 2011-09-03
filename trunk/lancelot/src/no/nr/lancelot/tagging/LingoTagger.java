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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nr.lancelot.frontend.LancelotRegistry;

public final class LingoTagger extends ChainTagger {
    
    private final Map<String, Set<Tag>> dict;

    public LingoTagger(final FragmentTagger nextTagger) {
        super(nextTagger);
        dict = createLingoDictionary();
    }

    private static Map<String, Set<Tag>> createLingoDictionary() {
        return LancelotRegistry.getInstance().getLingoDictionary();
    }

    @Override
    public List<Tag> tag(final String s) {
        debug(this.getClass().toString());
        
        final List<Tag> $ = new ArrayList<Tag>();
        if (dict.containsKey(s)) {
            $.addAll(dict.get(s));
        } else {
            final String upper = s.toUpperCase();
            if (dict.containsKey(upper)) {
                $.addAll(dict.get(upper));
            }
        }
        if ($.isEmpty()) {
            $.addAll(nextTagger.tag(s));
        }
        
        debug(this.getClass().toString() + " : " + $.size());

        return $;
    }

}
