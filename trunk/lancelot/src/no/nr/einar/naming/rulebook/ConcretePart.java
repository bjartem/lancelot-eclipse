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

public final class ConcretePart implements IPhrasePart {
    
    private final String text;

    public ConcretePart(final String text) {
        this.text = text;
    }

    public boolean captures(final NamePart part) {
        return text.equals(part.getText());
    }
    
    @Override
    public int hashCode() {
    	return text.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
    	if (other == null || !(other.getClass() == ConcretePart.class)) {
    		return false;
    	}
    	
    	return this.text.equals(((ConcretePart) other).text);
    }
    
    @Override
    public String toString() {
        return text;
    }

}
