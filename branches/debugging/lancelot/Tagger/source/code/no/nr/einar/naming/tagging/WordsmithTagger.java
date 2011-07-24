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

import java.util.ArrayList;
import java.util.List;

public final class WordsmithTagger extends ChainTagger {

    public WordsmithTagger(final FragmentTagger nextTagger) {
        super(nextTagger);
    }

    @Override
    public List<Tag> tag(final String s) {
        debug(this.getClass().toString());
        
        final List<Tag> $ = new ArrayList<Tag>();
        if (isInventedAdjective(s)) {
            $.add(Tag.Adjective);
        }
        if ($.isEmpty()) {
            $.addAll(nextTagger.tag(s));
        }
        return $;
    }
    
    private boolean isInventedAdjective(final String s) {
        if (tooShort(s)) {
            return false;
        }
        if (s.endsWith("able")) {
            if (s.endsWith("table")) {
                return s.endsWith("ttable");
            }
            return true;
        }
        return false;
    }
    
    private boolean tooShort(final String s) {
        final int SHORTEST_INVENTED_ADJECTIVE_LENGTH = 6;
        return s.length() < SHORTEST_INVENTED_ADJECTIVE_LENGTH;
    }
        
}
