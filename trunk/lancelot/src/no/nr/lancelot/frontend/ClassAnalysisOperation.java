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
package no.nr.lancelot.frontend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nr.lancelot.analysis.code.asm.ClassStreamAnalyzer;
import no.nr.lancelot.analysis.name.collapser.Fragment;
import no.nr.lancelot.analysis.name.collapser.FragmentCollapser;
import no.nr.lancelot.analysis.name.splitter.NameSplitter;
import no.nr.lancelot.model.JavaClass;
import no.nr.lancelot.model.JavaMethod;
import no.nr.lancelot.rulebook.IRulebook;
import static no.nr.lancelot.rulebook.IRulebook.IRulebookLookupResult;
import no.nr.lancelot.rulebook.MethodIdea;
import no.nr.lancelot.rulebook.MethodPhrase;
import no.nr.lancelot.rulebook.Rule;
import no.nr.lancelot.tagging.CachingTagger;
import no.nr.lancelot.tagging.PosTagger;
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
    
    public IClassAnalysisReport run() throws IOException {       
        final ClassStreamAnalyzer csa = new ClassStreamAnalyzer();
        final JavaClass javaClass = csa.analyze(new ByteArrayInputStream(byteCode));
        
        final List<IMethodBugReport> bugReports = new LinkedList<IMethodBugReport>();
        
        int numMethodsTotal = 0,
            numMethodsCovered = 0,
            numMethodsBuggy = 0;
        
        for (final JavaMethod method : javaClass) {
            numMethodsTotal++;
            
            final MethodIdea idea = deriveIdea(method, tagger);
            final IRulebookLookupResult rulebookLookupResult = rulebook.lookup(idea);
            if (!rulebookLookupResult.isCovered()) {
                continue;
            }
            
            numMethodsCovered++;
            if (!rulebookLookupResult.isBuggy()) {
                continue;
            }
            
            numMethodsBuggy++;
            final IMethodBugReport bugReport = new MethodBugReport(
            	method, 
            	idea, 
            	rulebookLookupResult
            );    
            bugReports.add(bugReport);
        }
        
        return new ClassAnalysisReport(
            javaClass, 
            bugReports, 
            key, 
            new IClassAnalysisReport.BugStatisticsData(
                numMethodsTotal, 
                numMethodsCovered, 
                numMethodsBuggy
            )
        );
    }
    
    public static MethodIdea deriveIdea(final JavaMethod javaMethod, final PosTagger tagger) {
        final MethodPhrase phrase = derivePhrase(javaMethod, tagger);
        final long semantics = deriveSemantics(javaMethod);
        final String returnType = deriveReturnType(javaMethod);
        final String paramTypeOrNull = deriveParamType(javaMethod);   
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
            if (fragment.isTypeName()) {
                javaMethod.flagAsTypeName(fragment.getText());
            }
        }
        
        final List<String> tags = tagger.tag(fragments);
        return new MethodPhrase(fragments, fixAdditionalTypeTags(collapsedFragments, tags));
    }

    private static long deriveSemantics(final JavaMethod javaMethod) {
        return javaMethod.getSemantics();
    }
    
    protected static String deriveReturnType(final JavaMethod javaMethod) {
        final String returnType = javaMethod.getReturnType();
        return fromFullyQualifiedName(returnType);
    }
    
    @Nullable
    protected static String deriveParamType(final JavaMethod javaMethod) {
        final boolean hasParameters = javaMethod.getParameterTypes().length != 0;
        if (!hasParameters) {
            return "";
        }
        
        final String firstParameterTypeName = javaMethod.getParameterTypes()[0];
        return fromFullyQualifiedName(firstParameterTypeName);
    }
    
    public static String fromFullyQualifiedName(final String fqn) {
        final String reverseMappingOrNull = FULLY_QUALIFIED_REVERSE_MAP.get(fqn);
        return reverseMappingOrNull != null ? reverseMappingOrNull : "reference";
    }
    
    @SuppressWarnings("serial")
    private static final Map<String, String> FULLY_QUALIFIED_REVERSE_MAP =
        Collections.unmodifiableMap(
            new HashMap<String, String>(){{
                put("java.lang.Object", "Object");
                put("java.lang.String", "String");
        
                put("boolean", "boolean");
                put("byte",    "primitive");
                put("char",    "primitive");
                put("short",   "primitive");
                put("int",     "int");
                put("long",    "primitive"); 
                
                put("float",   "primitive");
                put("double",  "primitive");
        
                put("void",    "void");
            }}
        );
    
    protected static List<String> fixAdditionalTypeTags(
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
