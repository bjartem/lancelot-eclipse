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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.model.JavaMethod;
import no.nr.lancelot.rulebook.MethodIdea;
import no.nr.lancelot.rulebook.Rule;
import no.nr.lancelot.rulebook.Severity;

public final class MethodBugReport implements IMethodBugReport {
    private final JavaMethod method;
    private final MethodIdea methodIdea;
    private final List<Rule> violations;
    private final String textualDescription;

    
    public MethodBugReport(
        final JavaMethod method, 
        final MethodIdea methodIdea, 
        final Collection<Rule> violations
    ) {
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null!");
        }
        if (methodIdea == null) {
            throw new IllegalArgumentException("methodIdea cannot be null!");
        }
        if (violations == null) {
            throw new IllegalArgumentException("violations cannot be null!");
        }
    
        if (violations.isEmpty()) {
            throw new IllegalArgumentException("violations cannot be empty!");
        }
        
        this.method = method;
        this.methodIdea = methodIdea;
        this.violations = Collections.unmodifiableList(new LinkedList<Rule>(violations));
        this.textualDescription = computeTextualDescription(method, violations);
    }

    private String computeTextualDescription(
        final JavaMethod method,
        final Collection<Rule> violations
    ) {
        return new BugDescriptionFormulator(
            method.getMethodName(), 
            violations
        ).getDescription();
    }

    @Override
    public JavaMethod getMethod() {
        return method;
    }
    
    @Override
    public List<Rule> getViolations() {
        return violations;
    }
    
    @Override
    public List<String> getAlternativeNameSuggestions() {
        final SemanticsMap semanticsMap = LancelotRegistry.getInstance().getSemanticsMap();
        return semanticsMap.findSuggestionsFor(methodIdea);
    }

    @Override
    public String getTextualDescription() {
        return textualDescription;
    }
    
    /* Note that we use |violations| >= 1, to reason that
     * the result is guaranteed to be at least NOTIFY.
     */
    @Override
    public Severity getMaximumSeverity() {
        for (final Rule rule : violations) {
            if (rule.getSeverity() == Severity.FORBIDDEN) {
                return Severity.FORBIDDEN;
            }
        }

        for (final Rule rule : violations) {
            if (rule.getSeverity() == Severity.INAPPROPRIATE) {
                return Severity.INAPPROPRIATE;
            }
        }

        return Severity.NOTIFY;
    }
    
    @Override
    public String toString() {
        return String.format(
            "MethodBugReport[method: %s violation count: %d]",
            method.getMethodName(),
            violations.size()
        );
    }
}
