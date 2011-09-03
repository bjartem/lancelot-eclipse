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

import static no.nr.lancelot.rulebook.Severity.FORBIDDEN;
import static no.nr.lancelot.rulebook.Severity.INAPPROPRIATE;
import static no.nr.lancelot.rulebook.Severity.NOTIFY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import no.nr.lancelot.model.Attribute;
import no.nr.lancelot.rulebook.Rule;
import no.nr.lancelot.rulebook.Severity;

@SuppressWarnings("serial")
public final class BugDescriptionFormulator {
    protected final static Map<Attribute, String> ATTRIBUTE_DESCRIPTIONS = 
        Collections.unmodifiableMap(
            new EnumMap<Attribute, String>(Attribute.class){{
                put(Attribute.RETURNS_VOID,           "return void");
                put(Attribute.RETURNS_INT,            "return int");
                put(Attribute.RETURNS_BOOLEAN,        "return boolean");
                put(Attribute.RETURNS_STRING,         "return String");
                put(Attribute.RETURNS_REFERENCE,      "return references");
                put(Attribute.NO_PARAMETERS,          "have no parameters");
                put(Attribute.RETURN_TYPE_IN_NAME,    "RETURN_TYPE_IN_NAME");
                put(Attribute.PARAMETER_TYPE_IN_NAME, "PARAMETER_TYPE_IN_NAME");

                put(Attribute.FIELD_WRITER,           "write to fields");
                put(Attribute.FIELD_READER,           "read field values");
                put(Attribute.PARAMETER_TO_FIELD,     "PARAMETER TO FIELD");
                put(Attribute.RETURNS_FIELD_VALUE,    "RETURNS FIELD VALUE");
                put(Attribute.TYPE_MANIPULATOR,       "perform runtime type manipulation");
                put(Attribute.RETURNS_CREATED_OBJECT, "RETURNS CREATED OBJECTE");
                
                put(Attribute.CONTAINS_LOOP,    "loop");
                put(Attribute.HAS_BRANCHES,     "branch");
                put(Attribute.MULTIPLE_RETURNS, "have multiple return points");

                put(Attribute.CREATES_REGULAR_OBJECTS,   "CREATES_REGULAR_OBJECTS");
                put(Attribute.CREATES_STRING_OBJECTS,    "CREATES_STRING_OBJECTS");
                put(Attribute.CREATES_CUSTOM_OBJECTS,    "CREATES_CUSTOM_OBJECTS");
                put(Attribute.CREATES_OWN_CLASS_OBJECTS, "CREATES_OWN_CLASS_OBJECTS");

                put(Attribute.THROWS_EXCEPTIONS,          "throw exceptions");
                put(Attribute.CATCHES_EXCEPTIONS,         "catches exceptions");
                put(Attribute.EXPOSES_CHECKED_EXCEPTIONS, "exposes checked exceptions");
                
                put(Attribute.RECURSIVE_CALL,           "use recursive calls");
                put(Attribute.SAME_NAME_CALL,           "call methods of the same name");
                put(Attribute.SAME_VERB_CALL,           "SAME_VERB_CALL");
                put(Attribute.METHOD_CALL_ON_FIELD,     "METHOD_CALL_ON_FIELD");
                put(Attribute.METHOD_CALL_ON_PARAMETER, "METHOD_CALL_ON_PARAMETER");
                put(Attribute.PARAMETER_TO_FIELD_CALL,  "PARAMETER_TO_FIELD_CALL");
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
                    prefixEach(NEVER,      filter(FORBIDDEN,      true)),
                    prefixEach(ALWAYS,     filter(FORBIDDEN,      false)),
                    prefixEach(SELDOM,     filter(INAPPROPRIATE,  true)),
                    prefixEach(VERY_OFTEN, filter(INAPPROPRIATE,  false)),
                    prefixEach(RARELY,     filter(NOTIFY,         true)),
                    prefixEach(OFTEN,      filter(NOTIFY,         false))
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
