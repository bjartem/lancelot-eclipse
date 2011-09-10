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
    	if (fragments == null) {
    		throw new IllegalArgumentException("fragments cannot be null!");
    	}
    	
        final List<List<Tag>> possibleTags = tagger.tag(fragments);
        final List<Tag> tags = select(possibleTags);
        
        final List<String> res = new ArrayList<String>();
        for (final Tag t : tags) {
            res.add(t.name().toLowerCase());
        }
        return res;
    }

    private List<Tag> select(final List<List<Tag>> possibleTagsPerFragment) {
        final List<Tag> res = new LinkedList<Tag>();

        final Iterator<List<Tag>> possibleTagsPerFragmentIter = possibleTagsPerFragment.iterator();
        
        for (int i = 0, n = possibleTagsPerFragment.size(); i < n; ++i) {
            List<Tag> possibleTags = possibleTagsPerFragmentIter.next();
            
            boolean mayBeNoun = false,
                    mayBeVerb = false,
                    mayBeAdjective = false,
                    mayBeAdverb = false,
                    mayBeNumber = false,
                    mayBePronoun = false,
                    mayBePreposition = false,
                    mayBeConjunction = false,
                    mayBeArticle = false;
            
            for (final Tag possibleTag : possibleTags) {
                switch (possibleTag) {
                case Noun:        mayBeNoun = true;        break;
                case Verb:        mayBeVerb = true;        break;
                case Adjective:   mayBeAdjective = true;   break;
                case Adverb:      mayBeAdverb = true;      break;
                
                case Number:      mayBeNumber = true;      break;
                case Pronoun:     mayBePronoun = true;     break;
                case Preposition: mayBePreposition = true; break;
                case Conjunction: mayBeConjunction = true; break;
                case Article:     mayBeArticle = true;     break;
                
                case Unknown:     // Fall through;
                case Special:     break; 
                
                default:          throw new RuntimeException("Unmatched tag: " + possibleTag);
                }
            }
            
            if      (mayBeNumber)      { res.add(Tag.Number);      }
            else if (mayBePronoun)     { res.add(Tag.Pronoun);     }
            else if (mayBePreposition) { res.add(Tag.Preposition); }
            else if (mayBeConjunction) { res.add(Tag.Conjunction); }
            else if (mayBeArticle)     { res.add(Tag.Article);     }
            
            final boolean wasFoundAmongFirst = res.size() == i+1; 
            if (wasFoundAmongFirst) {
                continue;
            }
            
            final boolean isFirst = i == 0;
            if (isFirst) {
                if      (mayBeVerb)      { res.add(Tag.Verb);      }
                else if (mayBeAdjective) { res.add(Tag.Adjective); }
                else if (mayBeAdverb)    { res.add(Tag.Adverb);    }
                else if (mayBeNoun)      { res.add(Tag.Noun);      }
            } else {
                if      (mayBeNoun)      { res.add(Tag.Noun);      }
                else if (mayBeAdjective) { res.add(Tag.Adjective); }
                else if (mayBeAdverb)    { res.add(Tag.Adverb);    }
                else if (mayBeVerb)      { res.add(Tag.Verb);      }
            }

            final boolean stillNotFound = res.size() == i;
            if (stillNotFound) {
                res.add(Tag.Unknown);
            }
        }
        
        return res;
    }
}
