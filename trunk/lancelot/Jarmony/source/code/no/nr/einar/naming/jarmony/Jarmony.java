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
package no.nr.einar.naming.jarmony;

import java.io.IOException;

public final class Jarmony {
    
    public static void main(final String[] args) throws IOException {
        for (final Configuration config : new Args(args)) {
            new Analyzer(config).analyze();
        }
    }    

}
