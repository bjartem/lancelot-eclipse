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

public final class MethodIdea {
	
	private final MethodPhrase phrase;
	private final int semantics;

	public MethodIdea(final MethodPhrase phrase, final int semantics) {
		this.phrase = phrase;
		this.semantics = semantics;
	}
	
	public MethodPhrase getPhrase() {
		return phrase;
	}
	
	public int getSemantics() {
		return semantics;
	}
	
	public String toString() {
		return phrase.toString() + " [" + semantics + "]";
	}

}
