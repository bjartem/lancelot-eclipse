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

public final class WildcardPart implements IPhrasePart {

    public boolean captures(final NamePart part) {
        return true;
    }
    
    @Override
    public int hashCode() {
        return 42;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass() == WildcardPart.class;
    }
    
    @Override
    public String toString() {
        return "*";
    }

}
