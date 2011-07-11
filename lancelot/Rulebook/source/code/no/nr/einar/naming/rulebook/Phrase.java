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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class Phrase {
	
	private final List<IPhrasePart> phrase;
	private final List<Phrase> refinements = new ArrayList<Phrase>();
	private final Set<Rule> rules = new HashSet<Rule>();

	public Phrase(final String phrase) {
		this.phrase = derivePhrase(phrase);
	}
	
	private List<IPhrasePart> derivePhrase(final String s) {
		final List<IPhrasePart> $ = new ArrayList<IPhrasePart>();
		final String[] parts = s.split("-");
		for (final String part: parts) {
			if ("*".equals(part)) {
				$.add(new WildcardPart());
			} else if (part.charAt(0) == '[') {
				int startIndex = 1;
				if (part.charAt(1) == '/') {
					++startIndex;
				}
				final String tag = part.substring(startIndex, part.length() - 1);
				$.add(new TagPart(tag));
			} else {
				$.add(new ConcretePart(part));
			}
		}
		return $;
	}

	public String toString() {
		final StringBuilder $ = new StringBuilder();
		for (final IPhrasePart p : phrase) {
			$.append(p);
		}
		return $.toString();
	}

	public void addRefinement(final Phrase p) {
		refinements.add(p);
	}

	public void addRule(final Rule r) {
		rules.add(r);
	}

	public List<Phrase> getRefinements() {
		return refinements;
	}

	public boolean captures(final MethodPhrase name) {
		final Iterator<IPhrasePart> pItor = phrase.iterator();
		IPhrasePart pPart = null;
		for (final NamePart nPart : name) {
			if (!(pPart instanceof WildcardPart)) {
				if (!(pItor.hasNext())) {
					return false;
				}
				pPart = pItor.next();
			}
			if (!pPart.captures(nPart)) {
				return false;
			}
		}
		return true;
	}

	public Set<Rule> getRules() {
		return rules;
	}

}
