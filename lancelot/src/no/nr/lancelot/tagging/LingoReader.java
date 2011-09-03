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
package no.nr.lancelot.tagging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LingoReader {
    @SuppressWarnings("serial")
    public static final class LingoInitException extends Exception {
        private LingoInitException(final Throwable t) {
            super(t);
        }
    }
    
    private final File file;
    
    private final Map<String, Set<Tag>> dict = new HashMap<String, Set<Tag>>();
    
    public LingoReader(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        
        this.file = file;
    }
    
    public Map<String, Set<Tag>> read() throws LingoInitException {
        readFromFile();
        return dict;
    }
    
    private void readFromFile() throws LingoInitException {
        try {
            final BufferedReader input =  new BufferedReader(new FileReader(file));
            read(input);
            input.close();
        } catch (IOException e) {
            throw new LingoInitException(e);
        } 
    }

    private void read(final BufferedReader input) throws IOException {
        while (true) {
            final String line = input.readLine();
            if (line == null) {
                break;
            }
            process(line);
        }
    }
    
    private void process(final String line) {
        final String word = getWord(line);
        final Set<Tag> tags = getTags(line.substring(word.length()));
        this.dict.put(word, tags);
    }

    private String getWord(final String line) {
        final int firstSpace = line.indexOf(' ');
        if (firstSpace < 0) {
            return line;
        }
        return line.substring(0, firstSpace);
    }
    
    private Set<Tag> getTags(final String s) {
        final int lparens = s.indexOf('(');
        if (lparens < 0) {
            return getNounList();
        }
        final int rparens = s.indexOf(')');
        assert rparens > lparens;
        return getTagsFromDefinition(s.substring(lparens + 1, rparens));
    }

    @SuppressWarnings("serial")
    private Set<Tag> getNounList() {
        return new HashSet<Tag>() {{
            add(Tag.Noun);
        }};
    }

    private Set<Tag> getTagsFromDefinition(final String s) {
        if (s.startsWith("pl of")) {
            return getNounList();
        }
        return getTagsFromDefinition(s.split(", "));
    }

    private Set<Tag> getTagsFromDefinition(final String[] ss) {
        final Set<Tag> $ = new HashSet<Tag>();
        for (int i = 0; i < ss.length; i++) {
            $.add(toTag(ss[i]));
        }
        return $;
    }

    private Tag toTag(final String s) {
        return Tag.valueOf(capitalize(s));
    }

    private String capitalize(final String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
