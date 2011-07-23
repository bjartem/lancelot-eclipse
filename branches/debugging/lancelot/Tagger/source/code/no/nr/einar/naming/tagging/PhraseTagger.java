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

public final class PhraseTagger implements AmbiguousTagger {
    
    private final FragmentTagger tagger = 
        new ShortcircuitTagger(
            new WordNetTagger(
                new AdditionalWordClassTagger(
                    new WordsmithTagger(
                        new LingoTagger(
                            new MorphyTagger(null))))));

    @Override
    public List<List<Tag>> tag(final List<String> fragments) {
        final List<List<Tag>> $ = new ArrayList<List<Tag>>();
        for (final String f : fragments) {
            $.add(tagger.tag(f));
        }
        return $;
    }

}
