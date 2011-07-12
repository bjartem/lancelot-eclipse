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
package no.nr.einar.naming.jarmony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nr.einar.naming.rulebook.MethodIdea;
import no.nr.einar.naming.rulebook.MethodPhrase;
import no.nr.einar.naming.rulebook.Rule;
import no.nr.einar.naming.rulebook.Rulebook;
import no.nr.einar.naming.rulebook.Severity;
import no.nr.einar.naming.tagging.CachingTagger;
import no.nr.einar.naming.tagging.PosTagger;
import no.nr.einar.pb.analysis.name.collapser.Fragment;
import no.nr.einar.pb.analysis.name.collapser.FragmentCollapser;
import no.nr.einar.pb.analysis.name.splitter.NameSplitter;
import no.nr.einar.pb.model.JavaClass;
import no.nr.einar.pb.model.JavaMethod;
import no.nr.lancelot.analysis.LancelotRegistry;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

public final class Verifier {
    
    private final long startTime = System.nanoTime();
    
    private final Rulebook rules = LancelotRegistry.getInstance().getRulebook();
    private final PosTagger tagger = new CachingTagger();
    private final Element root;
    private final Element results;
    
    private final Set<JavaClass> classes = new HashSet<JavaClass>();
    private int totalNumberOfMethods = 0;
    private int numberOfViolatingMethods = 0;
    private int totalNumberOfViolations = 0;
    private final Map<Severity, Integer> severityDistribution = new HashMap<Severity, Integer>();
    
    public Verifier(final String jarName) {
        this.root = new Element("Jarmony");
        this.root.addAttribute(new Attribute("jar", jarName));
        this.results = new Element("Results");
        this.root.appendChild(this.results);
    }

    public String getXml() {
        root.appendChild(getStatistics());
        final Document doc = new Document(root);
        return doc.toXML();
    }

    private Element getStatistics() {
        final Element $ = new Element("Statistics");
        $.appendChild(getClassStatistics());
        $.appendChild(getMethodStatistics());
        $.appendChild(getViolationStatistics());
        $.appendChild(getTimeStatistics());
        return $;
    }

    private Element getClassStatistics() {
        final Element $ = new Element("ClassStatistics");
        $.addAttribute(new Attribute("count", Integer.toString(classes.size())));
        return $;
    }

    private Element getMethodStatistics() {
        final Element $ = new Element("MethodStatistics");
        $.addAttribute(new Attribute("count", Integer.toString(totalNumberOfMethods)));
        $.addAttribute(new Attribute("violations", Integer.toString(numberOfViolatingMethods)));
        return $;
    }

    private Element getViolationStatistics() {
        final Element $ = new Element("ViolationStatistics");
        $.addAttribute(new Attribute("count", Integer.toString(totalNumberOfViolations)));
        for (final Severity severity : Severity.values()) {
            int count = 0;
            if (severityDistribution.containsKey(severity)) {
                count = severityDistribution.get(severity);
            }
            $.addAttribute(new Attribute(severity.name(), Integer.toString(count)));
        }
        return $;
    }
    
    private Element getTimeStatistics() {
        final Element $ = new Element("TimeStatistics");
        $.addAttribute(new Attribute("seconds", Long.toString(elapsedTime())));
        return $;
    }

    private long elapsedTime() {
        final long ns = System.nanoTime() - startTime;
        final long ms = ns / 1000000L;
        final long s = ms / 1000L;
        return s;
    }

    public boolean verify(final JavaClass jc, final JavaMethod jm) {
        ++totalNumberOfMethods;
        register(jc);
        final MethodIdea m = deriveIdea(jm, tagger);
        final Set<Rule> violations = rules.check(m);
        if (violations.isEmpty()) {
            return false;
        }
        ++numberOfViolatingMethods;
        final String signature = createSignature(jm);
        final Element element = new Element("Method");
        element.addAttribute(new Attribute("signature", signature));
        element.addAttribute(new Attribute("class", jc.getQualifiedName()));
        for (final Rule rule : violations) {
            element.appendChild(violation(jc, jm, rule));
        }
        results.appendChild(element);
        return true;
    }

    private void register(final JavaClass jc) {
        if (! classes.contains(jc)) {
            classes.add(jc);
        }
    }

    private Element violation(final JavaClass jc, final JavaMethod jm, final Rule rule) {
        ++totalNumberOfViolations;
        registerSeverity(rule.getSeverity());
        final Element $ = new Element("Violation");
        $.addAttribute(new Attribute("attribute", Name.get(rule.getAttribute())));
        $.addAttribute(new Attribute("severity", rule.getSeverity().name()));
        $.addAttribute(new Attribute("if", Boolean.toString(rule.ifSet())));
        return $;
    }

    private void registerSeverity(final Severity severity) {
        int acc = 0;
        if (severityDistribution.containsKey(severity)) {
            acc = severityDistribution.remove(severity);
        }
        severityDistribution.put(severity, acc + 1);
    }

    private static String createSignature(final JavaMethod method) {
        final StringBuilder sb = new StringBuilder();
        for (final String paramType : method.getParameterTypes()) {
            
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(unqualifyJavaLang(paramType));
        }
        return unqualifyJavaLang(method.getReturnType()) + " " + method.getMethodName() + "(" + sb.toString() + ")";
    }

    private static String unqualifyJavaLang(final String type) {
        final String javaLang = "java.lang.";
        if (type.startsWith(javaLang)) {
            return type.substring(javaLang.length());
        }
        return type;
    }

    private static MethodIdea deriveIdea(final JavaMethod jm, final PosTagger tagger) {
        final String name = jm.getMethodName();
        final NameSplitter splitter = new NameSplitter();
        final List<String> parts = splitter.split(name);
        final List<Fragment> collapsedFragments = FragmentCollapser.collapse(parts, jm);
        final List<String> fragments = new ArrayList<String>();
        for (final Fragment f: collapsedFragments) {
            fragments.add(f.getText());
        }
        final List<String> tags = tagger.tag(fragments);
        final MethodPhrase phrase = new MethodPhrase(fragments, correctTags(collapsedFragments, tags));
        return new MethodIdea(phrase, jm.getSemantics());
    }

    private static List<String> correctTags(final List<Fragment> fragments, final List<String> tags) {
        final Iterator<Fragment> fItor = fragments.iterator();
        final Iterator<String> tItor = tags.iterator();
        final List<String> $ = new ArrayList<String>();
        while (fItor.hasNext() && tItor.hasNext()) {
            final Fragment f = fItor.next();
            final String t = tItor.next();
            $.add(f.isTypeName() ? "type" : t);
        }
        return $;
    }


}
