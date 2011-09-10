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
package no.nr.lancelot.eclipse.testdata.suppressing_annotation_test_proj;

@SuppressWarnings("NamingBug")
public class Horse {
    private int n;
    
    public void shouldNotMark_A() {
        n = 12;
    }
    
    public void shouldNotMark_B() {
        int k = 12;
        if (k < 14)
            ++k;
    }
   
    public int shouldNotMark_C() {
        int k = 12;
        if (k < 14)
            ++k;
        return k;
    }
}
