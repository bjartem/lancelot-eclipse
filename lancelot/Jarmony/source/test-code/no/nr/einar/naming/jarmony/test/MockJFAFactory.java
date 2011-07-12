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
package no.nr.einar.naming.jarmony.test;

import java.io.IOException;
import java.util.jar.JarFile;

import org.junit.BeforeClass;

import no.nr.einar.naming.jarmony.JFAFactory;
import no.nr.einar.pb.analysis.IJarFileAnalyzer;
import no.nr.einar.pb.model.JavaJar;
import no.nr.lancelot.analysis.LancelotTestUtils;

public final class MockJFAFactory implements JFAFactory {
    
    public IJarFileAnalyzer create() {
        return new IJarFileAnalyzer() {
            @Override
            public JavaJar analyze(final JarFile jarFile) throws IOException {
                return new JavaJar("", "foo.jar");
            }
        };
    }

}
