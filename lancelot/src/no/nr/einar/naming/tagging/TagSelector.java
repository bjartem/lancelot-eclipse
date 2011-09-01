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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class TagSelector {
    
    private static class FragmentSelectorFactory {
        
        private static final int firstIndex = 0;
        private final int lastIndex;
        
        public FragmentSelectorFactory(final int lastIndex) {
            this.lastIndex = lastIndex;
        }
        
        public FragmentTagSelector create(
                final String fragment, 
                final Set<Tag> candidateTags, 
                final List<Tag> selectedTags, 
                final int index) {
            final FragmentTagSelector defaultSelector = createDefault(fragment, candidateTags, selectedTags, index);
            return new BasicFragmentTagSelector(defaultSelector, fragment, candidateTags);
        }
        
        private FragmentTagSelector createDefault(
                final String fragment, 
                final Set<Tag> candidateTags, 
                final List<Tag> selectedTags, 
                final int index) {
            if (index == firstIndex) {
                return new FirstFragmentTagSelector(fragment, candidateTags);
            } else if (index == lastIndex) {
                return new LastFragmentTagSelector(fragment, candidateTags, selectedTags);
            } else {
                return new RegularFragmentTagSelector(fragment, candidateTags);
            }
        }
        
    }

    public List<Tag> select(final List<String> fragments,
            final List<List<Tag>> possibleTags) {
        assert fragments.size() == possibleTags.size();
        
        final List<Tag> $ = new ArrayList<Tag>();
        final Iterator<String> fragmentItor = fragments.iterator();
        final Iterator<List<Tag>> tagListItor = possibleTags.iterator();
        int fragNo = 0;
        final FragmentSelectorFactory factory = new FragmentSelectorFactory(fragments.size() - 1);
        while (fragmentItor.hasNext() && tagListItor.hasNext()) {
            final String fragment = fragmentItor.next();
            final List<Tag> tagList = tagListItor.next();
            final Set<Tag> tagSet = new HashSet<Tag>(tagList);
            final FragmentTagSelector selector = factory.create(fragment, tagSet, $, fragNo);
            $.add(selector.select());
            ++fragNo;
        }
        return $;
    }

}
