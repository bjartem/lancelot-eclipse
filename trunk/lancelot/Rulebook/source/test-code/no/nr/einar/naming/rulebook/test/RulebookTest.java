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
package no.nr.einar.naming.rulebook.test;

import no.nr.einar.naming.rulebook.Rulebook;
import no.nr.lancelot.analysis.LancelotRegistry;
import no.nr.lancelot.analysis.LancelotTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public final class RulebookTest {
    
    @BeforeClass
    public static void initializeLancelot() throws Exception{
        LancelotTestUtils.loadDefaultConfiguration();
    }
        
    @Test(expected=IllegalArgumentException.class)
    public void methodCannotBeNull() {
        final Rulebook testObj = LancelotRegistry.getInstance().getRulebook();
        testObj.check(null);
    }

}