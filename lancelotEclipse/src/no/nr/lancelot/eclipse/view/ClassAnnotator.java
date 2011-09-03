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
package no.nr.lancelot.eclipse.view;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.view.SourceTypeFinder.TypeNotFoundException;
import no.nr.lancelot.frontend.IClassAnalysisReport;
import no.nr.lancelot.frontend.IMethodBugReport;
import no.nr.lancelot.model.JavaMethod;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

final class ClassAnnotator {    
    private final IJavaProject javaProject;

    private final IClassAnalysisReport analysisReport;
    private final IType sourceType;
    private final MethodMap methodMap;
    
    public ClassAnnotator(
        final IJavaProject javaProject, 
        final IClassFile classFile, 
        final IClassAnalysisReport analysisReport
    ) throws TypeNotFoundException, JavaModelException {
        if (javaProject == null || classFile == null || analysisReport == null)
            throw new IllegalArgumentException();
        
        this.javaProject = javaProject;
        this.analysisReport = analysisReport;
        this.sourceType = findSourceType();
        this.methodMap = new MethodMap(sourceType, classFile);
    }
    
    private IType findSourceType() throws TypeNotFoundException {
        final String packageName = analysisReport.getPackageName().replace('/', '.'),
                     typeQualifiedName = analysisReport.getClassName();
        
        return SourceTypeFinder.getInstance().findSourceType(
            javaProject, 
            packageName, 
            typeQualifiedName
        );
    }
    
    public void removeExistingMarkers() throws CoreException {
        final IResource sourceResource = sourceType.getResource();
        LancelotMarkerUtil.getInstance().removeMarkersOn(sourceResource);
    }
    
    public void markBugs() throws CoreException {
        for (final IMethodBugReport mnb : analysisReport.getMethodBugReports())
            markBug(mnb);
    }

    private void markBug(final IMethodBugReport bugReport) throws CoreException {
        final IMethod method = findEclipseMethod(bugReport);
        
        if (method == null) {
            LancelotPlugin.logError("MethodMap lookup failed for " + bugReport + ".");
            return;
        }
        
        if (shouldIgnore(method)) {
            return;
        }
    
        new MethodAnnotator(bugReport, sourceType, method).annotate();
    }
    
    private IMethod findEclipseMethod(final IMethodBugReport bugReport) 
    throws JavaModelException {
        final JavaMethod lancelotMethod = bugReport.getMethod();
        return methodMap.findMethod(lancelotMethod);
    }
    
    protected boolean shouldIgnore(final IJavaElement javaElement) throws JavaModelException {
        final IAnnotatable annotable = (IAnnotatable) javaElement.getAdapter(IAnnotatable.class);
        if (annotable == null)
            return false;
        
        for (final IAnnotation annotation : annotable.getAnnotations()) 
            if (isLancelotAnnotation(annotation))
                return true;
        
        final IJavaElement maybeParent = javaElement.getParent();
        if (maybeParent != null)
            return shouldIgnore(maybeParent);
        
        return false;
    }

    protected boolean isLancelotAnnotation(final IAnnotation annotation) throws JavaModelException {
        final String[][] typeNames = sourceType.resolveType(annotation.getElementName());
        if (typeNames == null) 
            return false;
        
        final String packageName = typeNames[0][0],
                     className   = typeNames[0][1];
        final boolean isCorrectType =    packageName.equals("java.lang") 
                                      && className.equals("SuppressWarnings");
        
        if (!isCorrectType)
            return false;
        
        try {
            final Object annotationValue = annotation.getMemberValuePairs()[0].getValue();
            
            final Object[] suppressions;
            if (annotationValue instanceof String)
                suppressions = new Object[]{ annotationValue };
            else
                suppressions = (Object[]) annotationValue;
            
            for (Object suppressed : suppressions)
                if (suppressed.equals(LancelotPlugin.SUPPRESS_WARNINGS_NAME))
                    return true;
        } catch (Exception e) {
            LancelotPlugin.logException(e, "Nasty region. Check array access and casts.");
        }
            
        return false;
    }
}
