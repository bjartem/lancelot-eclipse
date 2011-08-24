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
package no.nr.einar.naming.rulebook;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.Nullable;

public final class PhraseBuilder {  
	private PhraseBuilder() {}
	
	public static Phrase create(
    	final String phraseText, 
    	final List<Phrase> refinements, 
    	final Set<Rule> rules
    ) {
		final PhrasePatternMatch phraseMatch = new PhrasePatternMatch(phraseText);

		return new Phrase(
			derivePhraseParts(phraseMatch.nameString),
			deriveType(phraseMatch.returnTypeStringOrNull),
			ParameterRequirement.derive(phraseMatch.paramTypeStringOrNull),
			refinements,
			rules
		);
    }
    
    protected static final List<IPhrasePart> derivePhraseParts(final String name) {
    	final List<IPhrasePart> phraseParts = new ArrayList<IPhrasePart>();
        final String[] parts = name.split("-");
        
        for (final String part: parts) {
            if ("*".equals(part)) {
                phraseParts.add(new WildcardPart());
            } else if (part.charAt(0) == '[') {
                int startIndex = 1;
                if (part.charAt(1) == '/') {
                    ++startIndex;
                }
                final String tag = part.substring(startIndex, part.length() - 1);
                phraseParts.add(new TagPart(tag));
            } else {
                phraseParts.add(new ConcretePart(part));
            }
        }
        
        return phraseParts;
	}
	
	static final Type deriveType(final String typeName) {
		return Type.fromString(typeName);
	}
	
	private static final class PhrasePatternMatch {
    	private static final Pattern phrasePattern = makePhrasePattern();
    	
    	public final String nameString;
    	public final @Nullable String returnTypeStringOrNull,
    	                              paramTypeStringOrNull;
    	
    	PhrasePatternMatch(final String phrase) {
    		final Matcher phraseMatcher = phrasePattern.matcher(phrase);
    		ensureMatch(phrase, phraseMatcher);
    		
    		this.nameString = phraseMatcher.group(3);
    		this.returnTypeStringOrNull = phraseMatcher.group(2);
    		this.paramTypeStringOrNull = phraseMatcher.group(5);
    	}
    	
    	private void ensureMatch(final String phrase, final Matcher phraseMatcher) {
    		if (!phraseMatcher.matches()) {
        		throw new RuntimeException("Unmatched phrase '" + phrase + "'!");
        	}
    	}

    	private static Pattern makePhrasePattern() {
    		final String RE_OPTIONAL_RETURN_TYPE = "((\\S+) )?",
    				     RE_NAME = "([^\\(]+)",
    				     RE_OPTIONAL_PARAM_TYPE = "(\\(([^\\)\\.]*?)(\\.\\.\\.)?\\))?";
    		return Pattern.compile(
    			"^" + RE_OPTIONAL_RETURN_TYPE + RE_NAME + RE_OPTIONAL_PARAM_TYPE + "$"
    		);
    	}
    }
}