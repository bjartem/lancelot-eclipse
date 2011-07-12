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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Xslt {
        
    private final StreamSource source;
        
    public Xslt(final String xsltFile) {
        
        this.source = new StreamSource(getClass().getResourceAsStream(xsltFile));
    }
    
    public String transform(final String xml) {
        final Source xmlSource = new StreamSource(new StringReader(xml));
        return transform(xmlSource);
    }
    
    private String transform(final Source source) {
        final OutputStream out = new ByteArrayOutputStream();
        try {
            transformer().transform(source, new StreamResult(out));
        } catch (final TransformerException e) {
            throw new RuntimeException("Transformation went bad.");
        }
        return out.toString();
    }

    private Transformer transformer() {
        try {
            return TransformerFactory.newInstance().newTransformer(source);
        } catch (final TransformerConfigurationException e) {
            throw new RuntimeException("Unable to create Transformer!");
        }
    }

}
