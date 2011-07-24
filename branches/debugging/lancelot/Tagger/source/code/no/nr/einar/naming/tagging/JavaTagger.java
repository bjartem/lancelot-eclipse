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

public final class JavaTagger implements PosTagger {

    public static void main(final String[] args) {
        final PosTagger tagger = new JavaTagger();
        final List<String> fragments = getFragments("get", "last", "name");
        final List<String> result = tagger.tag(fragments);
        System.out.println(result.size());
        assert result.size() == fragments.size();
        for (final String tag: result) {
            System.out.print(tag + " ");
        }
        System.out.println();
    }
    
    private static List<String> getFragments(final String ... parts) {
        final List<String> $ = new ArrayList<String>();
        for (final String p: parts) {
            $.add(p);
        }
        return $;
    }
    
    private final PhraseTagger tagger = new PhraseTagger();
    private final TagSelector selector = new TagSelector();
    
    @Override
    public List<String> tag(final List<String> fragments) {
        final List<List<Tag>> possibleTags = tagger.tag(fragments);
        final List<Tag> tags = selector.select(fragments, possibleTags);
        final List<String> $ = new ArrayList<String>();
        for (final Tag t: tags) {
            $.add(t.name().toLowerCase());
        }
        return $;
    }

}
