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

public final class MethodIdea {
    private final MethodPhrase phrase;
    private final long semantics;

    private final String returnType;
    private final String paramType;

    public MethodIdea(
        final MethodPhrase phrase, 
        final long semantics,
        final String returnType, 
        final String paramTypeOrNull 
    ) {
        if (phrase == null) {
            throw new IllegalArgumentException();
        }
        
        this.phrase = phrase;
        this.semantics = semantics;
    
        this.returnType = returnType;
        this.paramType = paramTypeOrNull;
    }
    
    public MethodPhrase getPhrase() {
        return phrase;
    }
    
    public long getSemantics() {
        return semantics;
    }
    
    public String getReturnType() {
        return returnType;
    }

    public String getParamType() {
        return paramType;
    }

    @Override
    public String toString() {
        return String.format(
            "MethodIdea[phrase:%s returnType:%s paramType:%s semantics:%d]",
            phrase, returnType, paramType, semantics
        ); 
    }
}
