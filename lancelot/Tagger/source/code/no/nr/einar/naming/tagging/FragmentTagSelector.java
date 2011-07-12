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

public abstract class FragmentTagSelector {
    
    protected final String fragment;
    protected final Set<Tag> candidateTags;
    
    public FragmentTagSelector(final String fragment, final Set<Tag> candidateTags) {
        this.fragment = fragment;
        this.candidateTags = candidateTags;
    }
        
    public abstract Tag select();
    
    protected boolean canBe(final Tag tag) {
        return candidateTags.contains(tag);
    }

}
