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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class MethodPhrase implements Iterable<NamePart> {
    private final List<NamePart> parts;

    public MethodPhrase(final List<String> fragments, final List<String> tags) {
        this.parts = deriveParts(fragments, tags);
    }
    
    private List<NamePart> deriveParts(final List<String> fragments, final List<String> tags) {
        final List<NamePart> $ = new ArrayList<NamePart>();
        final Iterator<String> fItor = fragments.iterator();
        final Iterator<String> tItor = tags.iterator();
        while (fItor.hasNext() && tItor.hasNext()) {
            $.add(new NamePart(fItor.next(), tItor.next()));
        }
        return $;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final NamePart part : parts) {
            if (sb.length() > 0) {
                sb.append("-");
            }
            sb.append(part.getText() + "/" + part.getTag());
        }
        return sb.toString();
    }
    
    public NamePart get(final int i) {
        return parts.get(i);
    }

    public Iterator<NamePart> iterator() {
        return parts.iterator();
    }
    
    public int length() {
        return parts.size();
    }
    
    @Override
    public int hashCode() {
        return parts.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof MethodPhrase)) {
            return false;
        }
        
        return this.parts.equals(((MethodPhrase) other).parts);
    }
}
