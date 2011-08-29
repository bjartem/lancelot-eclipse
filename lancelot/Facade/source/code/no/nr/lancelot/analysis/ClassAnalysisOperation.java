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

import no.nr.einar.naming.rulebook.IRulebook;
import no.nr.einar.naming.rulebook.MethodIdea;
import no.nr.einar.naming.rulebook.MethodPhrase;
import no.nr.einar.naming.rulebook.Rule;
import no.nr.einar.naming.rulebook.Rulebook;
import no.nr.einar.naming.rulebook.Type;
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
    private final IRulebook rulebook = LancelotRegistry.getInstance().getRulebook();
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
    
    int N = 0;
    public IClassAnalysisReport run() throws IOException {       
        final ClassStreamAnalyzer csa = new ClassStreamAnalyzer();
        final JavaClass javaClass = csa.analyze(new ByteArrayInputStream(byteCode));
        
        final List<IMethodBugReport> bugReports = new LinkedList<IMethodBugReport>();

        for (final JavaMethod method : javaClass) {
            final IMethodBugReport bugReportOrNull = checkForBugs(method);    
            if (bugReportOrNull != null) {
            	System.out.printf(
            		"Violation %d for %s. Matching phrase: %s\n", 
            		++N,
            		method,
            		((Rulebook) rulebook).findMatchingPhrase(deriveIdea(method, tagger))
            	);
                bugReports.add(bugReportOrNull);
            }
        }
        
        return new ClassAnalysisReport(javaClass, bugReports, key);
    }
    
    public IMethodBugReport checkForBugs(final JavaMethod method) {
        final MethodIdea idea = deriveIdea(method, tagger);
        final Set<Rule> violations = rulebook.findViolations(idea);
        if (violations.isEmpty())
        	return null;
        
        return new MethodBugReport(method, idea, violations);
    }
    
    // FIXME. MOVE AWAY TO SEPARATE CLASS.
    public static MethodIdea deriveIdea(final JavaMethod javaMethod, final PosTagger tagger) {
        final MethodPhrase phrase = derivePhrase(javaMethod, tagger);
        final long semantics = deriveSemantics(javaMethod);
        final Type returnType = deriveReturnType(javaMethod);
        final @Nullable Type paramTypeOrNull = deriveParamTypeOrNull(javaMethod);   
		return new MethodIdea(phrase, semantics, returnType, paramTypeOrNull);
    }

	protected static MethodPhrase derivePhrase(
		final JavaMethod javaMethod, 
		final PosTagger tagger
	) {
		final String name = javaMethod.getMethodName();
        
        final NameSplitter splitter = new NameSplitter();
        final List<String> parts = splitter.split(name);
        final List<Fragment> collapsedFragments = FragmentCollapser.collapse(parts, javaMethod);
        final List<String> fragments = new ArrayList<String>();
        
        for (final Fragment fragment : collapsedFragments) {
            fragments.add(fragment.getText());
        }
        
        final List<String> tags = tagger.tag(fragments);
        return new MethodPhrase(fragments, correctTags(collapsedFragments, tags));
	}

	private static long deriveSemantics(final JavaMethod javaMethod) {
		return javaMethod.getSemantics();
	}
	
	protected static Type deriveReturnType(final JavaMethod javaMethod) {
		final String returnType = javaMethod.getReturnType();
		return Type.fromFullyQualifiedName(returnType);
	}
	
	@Nullable
	protected static Type deriveParamTypeOrNull(final JavaMethod javaMethod) {
		final boolean hasParameters = javaMethod.getParameterTypes().length != 0;
		if (!hasParameters)
			return null;
		
		final String firstParameterTypeName = javaMethod.getParameterTypes()[0];
		return Type.fromFullyQualifiedName(firstParameterTypeName);
	}
	
    protected static List<String> correctTags(
    	final List<Fragment> fragments, 
    	final List<String> tags
    ) {
    	final List<String> result = new ArrayList<String>();

    	final Iterator<Fragment> fragmentItor = fragments.iterator();
        final Iterator<String> tagsItor = tags.iterator();
        
        while (fragmentItor.hasNext() && tagsItor.hasNext()) {
            final Fragment f = fragmentItor.next();
            final String t = tagsItor.next();
            result.add(f.isTypeName() ? "type" : t);
        }
        
        return result;
    }
}
