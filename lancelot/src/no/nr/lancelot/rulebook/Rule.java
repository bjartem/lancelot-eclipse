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

import no.nr.lancelot.model.Attribute;

public final class Rule {
    private final Attribute attribute;
    private final Severity severity;
    private final boolean set;

    public Rule(final Attribute attribute, final Severity severity, final boolean set) {
        this.attribute = attribute;
        this.severity = severity;
        this.set = set;
    }
    
    public boolean ifSet() {
        return set;
    }
    
    public int hashCode() {
        int $ = 1;
        $ = $ * 31 + attribute.hashCode();
        $ = $ * 29 + severity.hashCode();
        return $;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof Rule)) {
            return false;
        }
        final Rule w = (Rule) o;
        return getAttribute().equals(w.getAttribute()) 
            && getSeverity().equals(w.getSeverity())
            && ifSet() == w.ifSet();
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public Severity getSeverity() {
        return severity;
    }
    
    public String toString() {
        return attribute.name() + " - " + (set ? "ON" : "OFF") + " - " + severity.name();
    }

    public boolean covers(final long semantics) {
        final long actualVal = semantics & attribute.getFlag();
        final boolean actual = actualVal != 0;
        return actual == set;
    }
}
