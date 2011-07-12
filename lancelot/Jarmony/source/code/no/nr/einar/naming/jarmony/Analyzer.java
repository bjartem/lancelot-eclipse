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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.jar.JarFile;

import no.nr.einar.naming.jarmony.test.TestUtils;
import no.nr.einar.pb.analysis.IJarFileAnalyzer;
import no.nr.einar.pb.model.JavaClass;
import no.nr.einar.pb.model.JavaJar;
import no.nr.einar.pb.model.JavaMethod;

public final class Analyzer {

    private final Configuration config;
    
    private JFAFactory factory = new JFAFactoryImpl();

    public Analyzer(final Configuration config) {
        if (config == null) {
            throw new IllegalArgumentException("The parameter 'config' cannot be null!");
        }
        this.config = config;
    }
    
    public void injectFactory(final JFAFactory factory) {
        this.factory = factory;
    }

    public void analyze() throws IOException {
        final JarFile jarFile = getJarFile(config.getInputFile());
        final IJarFileAnalyzer jfa = factory.create();
        final JavaJar jar = jfa.analyze(jarFile);
        final Verifier a = new Verifier(getName(jarFile));
        for (final JavaClass jc : jar) {
            for (final JavaMethod jm : jc) {
                a.verify(jc, jm);
            }
        }
        final PrintStream out = getOutputStream(config);
        final String xml = a.getXml();
        final String output = config.isHtmlOutput() ? toHtml(xml) : xml;
        out.println(output);
    }
    
    private static PrintStream getOutputStream(final Configuration config) throws FileNotFoundException {
        if (config.isConsoleTarget()) {
            return System.out;
        }
        final File outFile = new File(config.getOutputFile());
        final FileOutputStream outStream = new FileOutputStream(outFile);
        return new PrintStream(outStream);
    }

    private static JarFile getJarFile(final String inputFile) {
        JarFile $;
        try {
            $ = new JarFile(inputFile);
        } catch (final IOException e) {
            System.err.println(inputFile);
            throw new IllegalArgumentException(e.getMessage());
        }
        return $;
    }

    private String toHtml(final String xml) {
        final Xslt xslt = new Xslt(getXsltFile());
        return xslt.transform(xml);
    }
    
    private String getXsltFile() {
        return TestUtils.getXsltPath() + config.getXsltFile();
    }

    private static String getName(final JarFile jarFile) {
        final String fullName = jarFile.getName();
        return fullName.substring(fullName.lastIndexOf('/') + 1);
    }


}
