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

import java.util.List;
import java.util.Set;

public final class LastFragmentTagSelector extends FragmentTagSelector {
    
    private final FragmentTagSelector defaultSelector;
    private final List<Tag> selectedTags;

    public LastFragmentTagSelector(
            final String fragment, 
            final Set<Tag> candidateTags,
            final List<Tag> selectedTags) {
        super(fragment, candidateTags);
        this.defaultSelector = new DefaultFragmentTagSelector(fragment, candidateTags);
        this.selectedTags = selectedTags;
    }

    @Override
    public Tag select() {
        if (canBeEventPhrase()) {
            return Tag.Verb;
        }
        return defaultSelector.select();
    }

    private boolean canBeEventPhrase() {
        return isTwoFragmentPhrase() && isPreviousTag(Tag.Noun) && canBe(Tag.Verb);
    }

    private boolean isPreviousTag(final Tag tag) {
        assert selectedTags.size() > 0;
        final Tag prevTag = selectedTags.get(selectedTags.size() - 1);
        return prevTag == tag;
    }

    private boolean isTwoFragmentPhrase() {
        return selectedTags.size() == 1;
    }

}
