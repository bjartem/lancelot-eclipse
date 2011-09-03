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

public final class NamePart {
    private final String text;
    private final String tag;

    public NamePart(final String text, final String tag) {
        this.text = text;
        this.tag = tag;
    }
    
    public String getText() {
        return text;
    }
    
    public String getTag() {
        return tag;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof NamePart)) {
            return false;
        }
        
        final NamePart otherNamePart = (NamePart) other;
        return this.text.equals(otherNamePart.text) && this.tag.equals(otherNamePart.tag);
    }

}
