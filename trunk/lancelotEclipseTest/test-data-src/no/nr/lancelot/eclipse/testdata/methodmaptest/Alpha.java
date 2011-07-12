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
package no.nr.lancelot.eclipse.testdata.methodmaptest;

import java.util.List;
import java.util.Set;
import static no.nr.lancelot.eclipse.testdata.methodmaptest.Beta.BetaAlpha;

public class Alpha<K> {
    public int[] first(int a[][], double b, List<Set<String>> c, K d) {
        return null;
    }
    
    public List<K> second(K a, Set<List<Set<K>>> b) {
        return null;
    }
    
    public List<K> second(Set<List<Set<K>>> a, K b) {
        return null;
    }
    
    public void third(int a, BetaAlpha b, float c) throws Exception {
    }
    
    public void third(int a, BetaAlpha b, int c) throws Exception {
    }    
}
