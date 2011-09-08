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
    
    public void setAge() {
        int n = 12;
        if (n < 14)
            ++n;
    }
    
    @SuppressWarnings("NamingBug")
    public void getHope() {
        int n = 12;
        if (n < 14)
            ++n;
        for (int k = 0; k != (1 << 12); k *= 2)
            --n;
    }
}