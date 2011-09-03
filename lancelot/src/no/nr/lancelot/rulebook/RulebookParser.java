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
package no.nr.lancelot.rulebook;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.nr.lancelot.model.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public final class RulebookParser {
    private final List<Phrase> parsedPhrases = new LinkedList<Phrase>();
    
    public RulebookParser(final URL rulebookUrl) 
    throws ValidityException, ParsingException, IOException {
        if (rulebookUrl == null) {
            throw new IllegalArgumentException("rulebookUrl cannot be null!");
        }
        
        parse(rulebookUrl);
    }
    
    public List<Phrase> getParsedPhrases() {
        return parsedPhrases;
    }
    
    private void parse(final URL rulebookUrl) 
    throws ValidityException, ParsingException, IOException {
        final Builder parser = new Builder();
        final Document doc = parser.build(rulebookUrl.toString());
        parsePhrases(getChildren(doc.getRootElement(), "Phrases"));
    }
    
    private void parsePhrases(final List<Element> xmlPhrases) {
        for (final Element xp : xmlPhrases)
            parsePhrase(xp);
    }

    private void parsePhrase(final Element xmlPhrase) {
        checkElementType(xmlPhrase, "Phrase");
        
        parsedPhrases.add(new Phrase(
            xmlPhrase.getAttributeValue("text"), 
            parseRules(getChildren(xmlPhrase, "Rules"))
        ));
        
        parsePhrases(getChildren(xmlPhrase, "Refinements"));
    }
    
    private Set<Rule> parseRules(final List<Element> xmlRules) {
        final Set<Rule> result = new HashSet<Rule>();

        for (final Element xr : xmlRules) {
            result.add(parseRule(xr));
        }
        
        return result;
    }

    private Rule parseRule(final Element xmlRule) {
        checkElementType(xmlRule, "Rule");
        
        return new Rule(
            parseAttribute(xmlRule.getAttributeValue("attribute")), 
            parseSeverity(xmlRule.getAttributeValue("severity")),
            parseIfSet(xmlRule.getAttributeValue("if"))
        );
    }

    private boolean parseIfSet(final String ifSetString) {
        if ("True".equals(ifSetString)) {
            return true;
        } else if ("False".equals(ifSetString)) {
            return false;
        } else {
            throw new RuntimeException("Did not recognize 'if' string '" + ifSetString + "'!");
        }
    }

    private Severity parseSeverity(final String severityString) {
        for (final Severity s : Severity.values()) {
            if (s.name().equals(severityString)) {
                return s;
            }
        }
        throw new RuntimeException("Did not recognize severity string '" + severityString + "'!");      
    }

    private Attribute parseAttribute(final String attributeString) {
        for (final Attribute a : Attribute.values()) {
            if (a.name().equalsIgnoreCase(attributeString)) {
                return a;
            }
        }
        throw new RuntimeException("Did not recognize attribute string '" + attributeString +"'!");
    }
    
    private static void checkElementType(final Element xmlPhrase, String expectedLocalName) {
        if (!xmlPhrase.getLocalName().equals(expectedLocalName))
            throw new RuntimeException();
    }

    private static List<Element> getChildren(final Element element, final String containerName) {
        final Elements containerMatches = element.getChildElements(containerName);
        if (containerMatches.size() > 1) {
            throw new RuntimeException("Found more than one matching container element!");
        }
        
        final List<Element> result = new LinkedList<Element>();
        
        final boolean containerExists = containerMatches.size() == 1;
        if (containerExists) {
            final Elements children = containerMatches.get(0).getChildElements();
            for (int i = 0, n = children.size(); i < n; ++i)
                result.add(children.get(i));
        }
        
        return result;
    }
}

