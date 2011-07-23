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
package no.nr.einar.naming.tagging;

import java.util.Set;

public abstract class OrderedFragmentTagSelector extends FragmentTagSelector {
    
    public OrderedFragmentTagSelector(
            final String fragment, 
            final Set<Tag> candidateTags) {
        super(fragment, candidateTags);
    }

    @Override
    public Tag select() {
        final Tag[] order = getSelectionOrder();
        for (int i = 0; i < order.length; i++) {
            final Tag tag = order[i];
            if (canBe(tag)) {
                return tag;
            }
        }
        return defaultSelect();
    }
    
    protected Tag defaultSelect() {
        return Tag.Unknown;
    }
    
    protected abstract Tag[] getSelectionOrder();

}
