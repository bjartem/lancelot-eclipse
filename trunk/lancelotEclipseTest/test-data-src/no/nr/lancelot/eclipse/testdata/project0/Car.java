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

public class Car {
    private int n;
    
    public void shouldMark_A() {
        n = 12;
    }
    
    @SuppressWarnings("NamingBug")
    public void shouldNotMark_A() {
        int k = 12;
        if (k < 14)
            ++k;
    }
    
    public void shouldMark_B() {
        n = 12;
    }
    
    public void shouldMark_C() {
        n = 12;
    }
    
    @SuppressWarnings("NamingBug")
    public void shouldNotMark_B() {
        int k = 12;
        if (k < 14)
            ++k;
    }
}
