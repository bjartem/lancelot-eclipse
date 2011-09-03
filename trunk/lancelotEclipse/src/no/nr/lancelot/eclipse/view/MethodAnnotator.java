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

import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;

import java.util.List;

import no.nr.lancelot.frontend.IMethodBugReport;
import no.nr.lancelot.rulebook.Severity;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

final class MethodAnnotator {
    private final IMethodBugReport bugReport;
    private final IResource sourceResource;
    private final IMethod method;
    private final int charStart;
    private final int charEnd;
    private final String location;
    
    public MethodAnnotator(
        final IMethodBugReport bugReport, 
        final IType sourceType, 
        final IMethod method
    ) throws JavaModelException { 
        if (bugReport == null || sourceType == null || method == null)
            throw new IllegalArgumentException();
        
        this.bugReport = bugReport;
        this.sourceResource = sourceType.getResource();
        this.method = method;
        
        final ISourceRange nameRange = method.getNameRange();
        this.charStart = nameRange.getOffset();
        this.charEnd = charStart + nameRange.getLength();
        this.location = "Line " + findLineNumber(
            sourceType.getCompilationUnit().getSource().toCharArray(), 
            charStart
        ) + ".";
    }

    public void annotate() throws CoreException {
        LancelotMarkerUtil
            .createMarker()
                .forResource(sourceResource)
                .startingAt(charStart)
                .endingAt(charEnd)
                .withLocationText(location)
                .withSeverity(toEclipseSeverity(bugReport.getMaximumSeverity()))
                .withMessage(bugReport.getTextualDescription())
                .withAlternativeNames(serializeAlternativeNames())
                .withMethodHandle(method.getHandleIdentifier())
                .create();        
    }
    
    private String serializeAlternativeNames() {
        return join(
            bugReport.getAlternativeNameSuggestions(), 
            LancelotMarkerUtil.ALTERNATIVE_NAMES_SEPARATOR
        );
    }

    protected static String join(final List<String> strings, final char separator) {
        final StringBuilder sb = new StringBuilder();
        
        int i = 0, n = strings.size();
        for (final String s : strings) {
            sb.append(s);
            final boolean hasMore = ++i < n;
            if (hasMore)
                sb.append(separator);
        }
        
        return sb.toString();
    }

    // TODO! MOVE to utility class.
    public static int findLineNumber(final char[] source, final int pos) throws JavaModelException {
        if (source == null)
            throw new IllegalArgumentException();
        
        int lineNum = 1;
        for (int p = 0; p <= pos; ++p)
            // Ignore CRs. It's enough to look for LF for all sensible systems of today ;-)
            if (source[p] == '\n')
                ++lineNum;
        return lineNum;
    }

    private int toEclipseSeverity(final Severity lancelotSeverity) {
        switch (lancelotSeverity) {
            case FORBIDDEN:     return SEVERITY_ERROR;
            case INAPPROPRIATE: return SEVERITY_WARNING;
            case NOTIFY:        return SEVERITY_WARNING;
            default:            throw new RuntimeException("Unhandled Severity case: " + 
                                                              lancelotSeverity);
        }
    }
}
