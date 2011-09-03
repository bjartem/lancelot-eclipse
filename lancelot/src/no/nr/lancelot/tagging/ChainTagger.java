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
package no.nr.lancelot.tagging;

public abstract class ChainTagger implements FragmentTagger {

    protected final FragmentTagger nextTagger;
    
    public ChainTagger(final FragmentTagger nextTagger) {
        this.nextTagger = nextTagger;
    }
    
    protected void debug(final String message) {
//        System.out.println(message);
    }

}
