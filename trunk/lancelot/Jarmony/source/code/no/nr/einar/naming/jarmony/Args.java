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

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class Args implements Iterable<Configuration> {
    
    private final Iterator<File> repository;
    private final String[] args;
    
    public Args(final String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException();
        }
        final String path = args[0];
        final File f = new File(path);
        if (! f.exists()) {
            throw new IllegalArgumentException();
        }
        this.args = args;
        this.repository = createRepository(f);
    }

    private Iterator<File> createRepository(final File f) {
        final List<File> list = f.isFile() ? Arrays.asList(f) : Arrays.asList(f.listFiles());
        return list.iterator();
    }

    private void applyArgs(final Configuration $) {
        for (int i = 1; i < args.length; i++) {
            final String option = args[i];
            ++i;
            final String value = args[i];
            applyArg($, option, value);
        }
    }

    private void applyArg(final Configuration $, final String option, final String value) {
        if (isTargetOption(option)) {
            applyTarget($, value);
        } else if (isFormatOption(option)) {
            applyFormat($, value);
        } else if (isOutputFileOption(option)) {
            applyOutputFile($, value);
        } else if (isXsltFileOption(option)) {
            applyXsltFile($, value);
        }
    }
    
    private boolean isTargetOption(final String option) {
        return option.equals("-t") || option.equals("-target");
    }
    
    private void applyTarget(final Configuration $, final String value) {
        $.setOutputTarget(Configuration.OutputTarget.valueOf(value));        
    }

    private boolean isFormatOption(final String option) {
        return option.equals("-f") || option.equals("-format");
    }
    
    private void applyFormat(Configuration $, String value) {
        $.setOutputFileFormat(Configuration.OutputFileFormat.valueOf(value));
    }
    
    private boolean isOutputFileOption(final String option) {
        return option.equals("-o") || option.equals("-outfile");
    }
    
    private void applyOutputFile(Configuration $, String value) {
        $.setOutputFile(value);
    }
    
    private boolean isXsltFileOption(final String option) {
        return option.equals("-x") || option.equals("-xslt");
    }
    
    private void applyXsltFile(Configuration $, String value) {
        $.setXsltFile(value);
    }

    @Override
    public Iterator<Configuration> iterator() {
        return new Iterator<Configuration>() {

            @Override
            public boolean hasNext() {
                return repository.hasNext();
            }

            @Override
            public Configuration next() {
                final File f = ensureJar();
                final Configuration $ = new Configuration(f.getAbsolutePath());
                applyArgs($);
                return $;
            }

            private File ensureJar() {
                File $ = null;
                while (true) {
                    $ = repository.next();
                    if ($.getName().endsWith(".jar")) {
                        break;
                    }
                }
                return $;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
    }

    
}
