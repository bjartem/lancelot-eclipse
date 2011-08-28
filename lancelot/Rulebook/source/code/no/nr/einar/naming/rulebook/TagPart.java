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

public final class TagPart implements IPhrasePart {

    private final String tag;

    public TagPart(final String tag) {
        this.tag = tag;
    }

    public boolean captures(final NamePart part) {
        return tag.equals(part.getTag());
    }
    
    @Override
    public int hashCode() {
    	return tag.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
    	if (other == null || !(other.getClass() == TagPart.class)) {
    		return false;
    	}
    	
    	return this.tag.equals(((TagPart) other).tag);
    }
    
    @Override
    public String toString() {
        return "[" + tag + "]";
    }

}
