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
package no.nr.lancelot.analysis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.nr.einar.naming.rulebook.MethodIdea;
import no.nr.einar.naming.rulebook.MethodPhrase;
import no.nr.einar.naming.rulebook.Rule;
import no.nr.einar.naming.rulebook.Rulebook;
import no.nr.einar.naming.tagging.CachingTagger;
import no.nr.einar.naming.tagging.PosTagger;
import no.nr.einar.pb.analysis.code.asm.ClassStreamAnalyzer;
import no.nr.einar.pb.analysis.name.collapser.Fragment;
import no.nr.einar.pb.analysis.name.collapser.FragmentCollapser;
import no.nr.einar.pb.analysis.name.splitter.NameSplitter;
import no.nr.einar.pb.model.JavaClass;
import no.nr.einar.pb.model.JavaMethod;
import edu.umd.cs.findbugs.annotations.Nullable;

public final class ClassAnalysisOperation {
    private final Rulebook rulebook = LancelotRegistry.getInstance().getRulebook();
    private final PosTagger tagger = new CachingTagger();
    
    private final String className;
    private final byte[] byteCode;
    private final Object key;
    
    public ClassAnalysisOperation(
        final String className, 
        final byte[] byteCode, 
        @Nullable final Object key
    ) {
        if (className == null) {
            throw new IllegalArgumentException("className cannot be null");
        }
        
        if (byteCode == null) {
            throw new IllegalArgumentException("byteCode cannot be null");
        }
        
        this.className = className;
        this.byteCode = Arrays.copyOf(byteCode, byteCode.length);
        this.key = key;
    }
    
    public String getClassName() {
        return className;
    }
    
    public IClassAnalysisReport run() throws IOException {       
        final ClassStreamAnalyzer csa = new ClassStreamAnalyzer();
        final JavaClass javaClass = csa.analyze(new ByteArrayInputStream(byteCode));
        
        final List<IMethodBugReport> bugReports = new LinkedList<IMethodBugReport>();
        
        for (final JavaMethod method : javaClass) {
            final IMethodBugReport possibleBug = verify(method);    
            if (possibleBug != null) {
                bugReports.add(possibleBug);
            }
        }
        
        return new ClassAnalysisReport(javaClass, bugReports, key);
    }
    
    public IMethodBugReport verify(final JavaMethod javaMethod) {
        final MethodIdea idea = deriveIdea(javaMethod, tagger);
        final Set<Rule> violations = rulebook.check(idea);
        return violations.isEmpty() ? null : new MethodBugReport(javaMethod, violations);
    }
    
    private static MethodIdea deriveIdea(final JavaMethod javaMethod, final PosTagger tagger) {
        final String name = javaMethod.getMethodName();
        final NameSplitter splitter = new NameSplitter();
        final List<String> parts = splitter.split(name);
        final List<Fragment> collapsedFragments = FragmentCollapser.collapse(parts, javaMethod);
        final List<String> fragments = new ArrayList<String>();
        for (final Fragment f: collapsedFragments) {
            fragments.add(f.getText());
        }
        final List<String> tags = tagger.tag(fragments);
        final MethodPhrase phrase = new MethodPhrase(fragments, correctTags(collapsedFragments, tags));
        return new MethodIdea(phrase, javaMethod.getSemantics());
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
