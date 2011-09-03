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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class JavaTagger implements PosTagger {
    private final MainTagger tagger = new MainTagger(); 
    
    @Override
    public List<String> tag(final List<String> fragments) {
        final List<List<Tag>> possibleTags = tagger.tag(fragments);
        final List<Tag> tags = select(possibleTags);
        
        final List<String> $ = new ArrayList<String>();
        for (final Tag t : tags) {
            $.add(t.name().toLowerCase());
        }
        return $;
    }

    private List<Tag> select(final List<List<Tag>> possibleTagsPerFragment) {
		final List<Tag> res = new LinkedList<Tag>();

		final Iterator<List<Tag>> possibleTagsPerFragmentIter = possibleTagsPerFragment.iterator();
		
		for (int i = 0, n = possibleTagsPerFragment.size(); i < n; ++i) {
			List<Tag> possibleTags = possibleTagsPerFragmentIter.next();
			
			boolean hasNoun = false,
					hasVerb = false,
					hasAdjective = false,
					hasAdverb = false,
					hasNumber = false,
					hasPronoun = false,
					hasPreposition = false,
					hasConjunction = false,
					hasArticle = false;
			
			for (final Tag possibleTag : possibleTags) {
				switch (possibleTag) {
				case Noun:        hasNoun = true;        break;
				case Verb:        hasVerb = true;        break;
				case Adjective:   hasAdjective = true;   break;
				case Adverb:      hasAdverb = true;      break;
				
				case Number:      hasNumber = true;      break;
				case Pronoun:     hasPronoun = true;     break;
				case Preposition: hasPreposition = true; break;
				case Conjunction: hasConjunction = true; break;
				case Article:     hasArticle = true;     break;
				
				case Unknown: // Fall through;
				case Special: break; 
				
				default: throw new RuntimeException("Unmatched tag: " + possibleTag);
				}
			}
			
			if      (hasNumber)      { res.add(Tag.Number);      }
			else if (hasPronoun)     { res.add(Tag.Pronoun);     }
			else if (hasPreposition) { res.add(Tag.Preposition); }
			else if (hasConjunction) { res.add(Tag.Conjunction); }
			else if (hasArticle)     { res.add(Tag.Article);     }
			
			final boolean wasFoundAmongFirst = res.size() == i+1; 
			if (wasFoundAmongFirst) {
				continue;
			}
			
			final boolean isFirst = i == 0;
			if (isFirst) {
				if      (hasVerb)      { res.add(Tag.Verb);      }
				else if (hasAdjective) { res.add(Tag.Adjective); }
				else if (hasAdverb)    { res.add(Tag.Adverb);    }
				else if (hasNoun)      { res.add(Tag.Noun);      }
			} else {
				if      (hasNoun)      { res.add(Tag.Noun);      }
				else if (hasAdjective) { res.add(Tag.Adjective); }
				else if (hasAdverb)    { res.add(Tag.Adverb);    }
				else if (hasVerb)      { res.add(Tag.Verb);      }
			}

			final boolean stillNotFound = res.size() == i;
			if (stillNotFound) {
				res.add(Tag.Unknown);
			}
		}
		
		return res;
	}
}
