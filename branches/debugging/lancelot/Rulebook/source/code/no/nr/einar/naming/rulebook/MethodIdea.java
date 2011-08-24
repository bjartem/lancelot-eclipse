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
package no.nr.einar.naming.rulebook;

import edu.umd.cs.findbugs.annotations.Nullable;

public final class MethodIdea {
    private final MethodPhrase phrase;
    private final long semantics;

    private final Type returnType;
	private final boolean takesParameter;
	private final @Nullable Type paramTypeOrNull;

    public MethodIdea(
    	final MethodPhrase phrase, 
    	final long semantics,
    	final Type returnType, 
    	@Nullable final Type paramTypeOrNull 
    ) {
    	if (phrase == null) {
    		throw new IllegalArgumentException();
    	}
    	
        this.phrase = phrase;
        this.semantics = semantics;
    
        this.returnType = returnType;
        this.takesParameter = paramTypeOrNull != null;
        this.paramTypeOrNull = paramTypeOrNull;
    }
    
    public MethodPhrase getPhrase() {
        return phrase;
    }
    
    public long getSemantics() {
        return semantics;
    }
    
    public Type getReturnType() {
		return returnType;
	}

	public boolean takesParameter() {
		return takesParameter;
	}

	public Type getParamType() {
		return paramTypeOrNull;
	}

	public String toString() {
        return String.format(
        	"MethodIdea[phrase:%s returnType:%s paramType:%s semantics:%d]",
        	phrase, returnType, paramTypeOrNull, semantics
        ); 
    }
}
