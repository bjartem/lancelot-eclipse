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

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import no.nr.einar.pb.model.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public final class Rulebook {
	@SuppressWarnings("serial")
	public static final class RulebookInitException extends Exception {
		private RulebookInitException(final Throwable t) {
			super(t);
		}
	}
	
	private final Phrase root;
	
	public Rulebook(final URL ruleBookUrl) throws RulebookInitException {
		if (ruleBookUrl == null) {
			throw new IllegalArgumentException("ruleBookUrl cannot be null");
		}
		root = load(ruleBookUrl);
	}
	
	private Phrase load(final URL ruleBookUrl) throws RulebookInitException {
		final Builder parser = new Builder();
	    try {
			final Document doc = parser.build(ruleBookUrl.toString());
			final Element root = doc.getRootElement();
			return traverse(root);
		} catch (Exception e) {
			throw new RulebookInitException(e);
		}
	}
	
	private Phrase traverse(final Element e) {
		final Phrase $ = new Phrase(e.getAttributeValue("text"));
		if (hasRules(e)) {
			final Elements rules = getChildren(e, "Rules");
			for (int i = 0; i < rules.size(); i++) {
				$.addRule(parseRuleElement(rules.get(i)));
			}		
		}
		if (hasRefinements(e)) {
			final Elements refinements = getChildren(e, "Refinements");
			for (int i = 0; i < refinements.size(); i++) {
				final Element refinement = refinements.get(i);
				$.addRefinement(traverse(refinement));
			}	
		}
		return $;
	}
	
	private Rule parseRuleElement(final Element e) {
		final Attribute a = Attribute.valueOf(Attribute.class, e.getAttributeValue("attribute"));
		final Severity s = Severity.valueOf(Severity.class, e.getAttributeValue("severity"));
		final boolean ifSet = Boolean.parseBoolean(e.getAttributeValue("if"));
		return new Rule(a, s, ifSet);
	}

	private Elements getChildren(final Element e, final String name) {
		return e.getChildElements(name).get(0).getChildElements();
	}
	
	private boolean has(final Element e, final String name) {
		final Elements children = e.getChildElements(name);
		return children.size() > 0;
	}

	private boolean hasRules(final Element e) {
		return has(e, "Rules");
	}

	private boolean hasRefinements(final Element e) {
		return has(e, "Refinements");
	}

	public Set<Rule> check(final MethodIdea method) {
		if (method == null) {
			throw new IllegalArgumentException();
		}
		final Set<Rule> rules = findMethodRules(method.getPhrase());
		final Set<Rule> $ = new HashSet<Rule>();
		final int semantics = method.getSemantics();
		for (final Rule r : rules) {
			if (r.covers(semantics)) {
				$.add(r);
			}
		}
		return $;
	}
	
	private Set<Rule> find(final MethodPhrase name, final Phrase p) {
		for (final Phrase ref : p.getRefinements()) {
			if (ref.captures(name)) {
				return find(name, ref);
			}
		}
		return p.getRules();
	}
 
	private Set<Rule> findMethodRules(final MethodPhrase phrase) {
		return find(phrase, root);
	}

}
