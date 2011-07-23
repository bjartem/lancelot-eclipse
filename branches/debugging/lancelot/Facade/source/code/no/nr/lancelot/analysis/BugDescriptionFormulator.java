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


import static no.nr.einar.naming.rulebook.Severity.INAPPROPRIATE;
import static no.nr.einar.naming.rulebook.Severity.NOTIFY;
import static no.nr.einar.naming.rulebook.Severity.RECONSIDER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import no.nr.einar.naming.rulebook.Rule;
import no.nr.einar.naming.rulebook.Severity;
import no.nr.einar.pb.model.Attribute;

@SuppressWarnings("serial")
public final class BugDescriptionFormulator {
    protected final static Map<Attribute, String> ATTRIBUTE_DESCRIPTIONS = 
        Collections.unmodifiableMap(
            new EnumMap<Attribute, String>(Attribute.class){{
                put(Attribute.CONTAINS_LOOP,         "loop");
                put(Attribute.CREATES_OBJECTS,       "create objects");
                put(Attribute.FIELD_READER,          "read field values");
                put(Attribute.FIELD_WRITER,          "write to fields");
                put(Attribute.HAS_BRANCHES,          "branch");
                put(Attribute.LOCAL_ASSIGNMENT,      "use local variables");
                put(Attribute.MULTIPLE_RETURNS,      "have multiple return points");
                put(Attribute.NO_PARAMETERS,         "have no parameters");
                put(Attribute.PARAMETER_FALLTHROUGH, "use a parameter as return value");
                put(Attribute.PARAMETER_TO_FIELD,    "write a parameter to a field");
                put(Attribute.RECURSIVE_CALL,        "use recursive calls");
                put(Attribute.RETURNS_FIELD_VALUE,   "return a field value");
                put(Attribute.RETURNS_VOID,          "return void");
                put(Attribute.SAME_NAME_CALL,        "call methods of the same name");
                put(Attribute.STATIC,                "are static");
                put(Attribute.THROWS_EXCEPTIONS,     "throw exceptions");
                put(Attribute.TYPE_MANIPULATOR,      "perform runtime type manipulation");
            }}
        );

    protected static final String PERIOD = ". ";
    protected static final String COLON = ", ";
    protected static final String AND = "and ";
    
    protected static final String ALWAYS = "always ";
    protected static final String VERY_OFTEN = "very often ";
    protected static final String OFTEN = "often ";
    protected static final String RARELY = "rarely ";
    protected static final String SELDOM = "seldom ";
    protected static final String NEVER = "never ";
    
    protected final Collection<Rule> violations;

    protected final String description;

    public BugDescriptionFormulator(final String methodName, final Collection<Rule> violations) {
        if (methodName == null) {
            throw new IllegalArgumentException("methodName cannot be null");
        }
        if (violations == null) {
            throw new IllegalArgumentException("violations cannot be null");
        }
        
        this.violations = violations;
        this.description = generate();
    }
    
    public String getDescription() {
        return description;
    }

    @SuppressWarnings("unchecked")
    protected String generate() {
        return concat(
            "Methods with this name ",
            conjugate(
                append(
                    prefixEach(NEVER,      filter(INAPPROPRIATE, true)),
                    prefixEach(ALWAYS,     filter(INAPPROPRIATE, false)),
                    prefixEach(SELDOM,     filter(RECONSIDER,    true)),
                    prefixEach(VERY_OFTEN, filter(RECONSIDER,    false)),
                    prefixEach(RARELY,     filter(NOTIFY,        true)),
                    prefixEach(OFTEN,      filter(NOTIFY,        false))
                ),
                COLON,
                AND
            )
        );
    }
    
    private ArrayList<String> append(final List<String>... lists) {
        final ArrayList<String> res = new ArrayList<String>();
        for (final List<String> list : lists) {
            res.addAll(list);
        }
        return res;
    }

    private List<String> prefixEach(final String prefix, final List<String> strings) {
        final List<String> res = new ArrayList<String>();
        for (final String s : strings) {
            res.add(prefix + s);
        }
        return res;
    }

    protected final ArrayList<String> filter(final Severity severity, final boolean ifSet) {
        final ArrayList<String> res = new ArrayList<String>();
        for (final Rule rule : violations) {
            final boolean filterMatches =    rule.getSeverity().equals(severity) 
                                          && rule.ifSet() == ifSet;
            if (filterMatches) {
                res.add(ATTRIBUTE_DESCRIPTIONS.get(rule.getAttribute()));
            }
        }
        return res;
    }

    protected static String concat(final String... strings) {
        final StringBuilder sb = new StringBuilder();
        for (final String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }

    protected static String conjugate(
        final ArrayList<String> strings, 
        final String separator, 
        final String conjunction
    ) {
        final StringBuilder sb = new StringBuilder();
        
        for (int i = 0, count = strings.size(); i < count; ++i) {
            final boolean isFirst = i == 0,
                          isLast  = i == count-1;
            
            if (!isFirst) { 
                sb.append(separator); 
            }
            
            if (!isFirst && isLast) {
                sb.append(conjunction);
            }

            sb.append(strings.get(i));
        }
        
        return sb.toString();
    }
}
