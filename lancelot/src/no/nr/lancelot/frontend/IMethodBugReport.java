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

import java.util.List;

import no.nr.lancelot.model.JavaMethod;
import no.nr.lancelot.rulebook.Rule;
import no.nr.lancelot.rulebook.Severity;

public interface IMethodBugReport {
    JavaMethod getMethod();
    List<Rule> getViolations();
    List<String> getAlternativeNameSuggestions();
    String getTextualDescription();
    Severity getMaximumSeverity();
}
