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

public final class Configuration {
    
    public enum OutputFileFormat {
        HTML, XML;
    }

    public enum OutputTarget {
        File, Console
    }
    
    private static final OutputTarget DEFAULT_OUTPUT_TARGET = OutputTarget.File;
    private static final OutputFileFormat DEFAULT_OUTPUT_FILE_FORMAT = OutputFileFormat.HTML;
    public static final String DEFAULT_XSLT_FILE = "jarmony.xslt";

    private final String inputFile;
    private String outputFile;
    private OutputTarget outputTarget = DEFAULT_OUTPUT_TARGET;
    private OutputFileFormat outputFileFormat = DEFAULT_OUTPUT_FILE_FORMAT;
    private String xsltFile = DEFAULT_XSLT_FILE;

    public Configuration(final String file) {
        this.inputFile = file;
    }

    private String deriveOutputFile() {
        final String baseName = inputFile.substring(0, inputFile.lastIndexOf(".jar"));
        final String $ = baseName + "-jarmony-report." + getOutputFileSuffix();
        return $;
    }

    private String getOutputFileSuffix() {
        return outputFileFormat.name().toLowerCase();
    }

    public OutputFileFormat getOutputFileFormat() {
        return this.outputFileFormat;
    }

    public String getInputFile() {
        return this.inputFile;
    }

    public String getOutputFile() {
        if (this.outputFile == null) {
            this.outputFile = deriveOutputFile();
        }
        return this.outputFile;
    }

    public String getXsltFile() {
        return this.xsltFile;
    }

    public OutputTarget getOutputTarget() {
        return this.outputTarget;
    }

    public void setOutputTarget(final OutputTarget target) {
        this.outputTarget = target;
    }

    public void setOutputFile(final String file) {
        this.outputFile = file;
    }

    public void setOutputFileFormat(final OutputFileFormat format) {
        this.outputFileFormat = format;
    }

    public void setXsltFile(final String file) {
        this.xsltFile = file;
    }

    public boolean isHtmlOutput() {
        return this.outputFileFormat == OutputFileFormat.HTML;
    }

    public boolean isFileTarget() {
        return this.outputTarget == OutputTarget.File;
    }

    public boolean isConsoleTarget() {
        return this.outputTarget == OutputTarget.Console;
    }

}
