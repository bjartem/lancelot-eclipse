package no.nr.lancelot.analysis.name.splitter;

import java.util.ArrayList;
import java.util.List;

public final class NameSplitter {

    private NameSplittingState state = NameSplittingState.START;

    public List<String> split(final String name) {
        final List<String> result = new ArrayList<String>();
        final char[] chars = name.toCharArray();
        int wordStartIndex = 0;
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (state == NameSplittingState.START) {
                if (i > wordStartIndex) {
                    addWord(result, chars, wordStartIndex, i);
                    wordStartIndex = i;
                }
                if (Character.isUpperCase(c)) {
                    state = NameSplittingState.CAPITALIZED;
                } else if (Character.isLowerCase(c)) {
                    state = NameSplittingState.NORMAL;
                } else if (Character.isDigit(c)) {
                    state = NameSplittingState.NUMBER;
                } else {
                    throw new RuntimeException("NameSplitter encountered unexpected character: " + c);
                }
            } else if (state == NameSplittingState.CAPITALIZED) {
                if (Character.isUpperCase(c)) {
                    if (i + 1 == chars.length || i + 1 < chars.length && (Character.isUpperCase(chars[i + 1]) || Character.isDigit(chars[i + 1]))) {
                        state = NameSplittingState.ACRONYM;
                    } else {
                        state = NameSplittingState.START;
                    }
                } else if (Character.isLowerCase(c)) {
                    state = NameSplittingState.NORMAL;
                } else {
                    state = NameSplittingState.START;
                }
            } else if (state == NameSplittingState.ACRONYM) {
                if (Character.isUpperCase(c)) {
                    if (i + 1 < chars.length && !Character.isUpperCase(chars[i + 1])) {
                        // Ignore if i+1 is the last character, and this character is a pluralization (an s).
                        if (i + 1 < chars.length - 1 || chars[i + 1] != 's') {
                            // FIXME Hack to fix problem with numbers, that should not be rewound.
                            if (Character.isDigit(chars[i + 1])) {
                                ++i;
                            }
                            state = NameSplittingState.START;
                        }
                    }
                } else if (Character.isDigit(c)) {
                    state = NameSplittingState.START;
                }
            } else if (state == NameSplittingState.NORMAL) {
                if (!Character.isLowerCase(c)) {
                    state = NameSplittingState.START;
                }
            } else if (state == NameSplittingState.NUMBER) {
                if (!Character.isDigit(c)) {
                    state = NameSplittingState.START;
                }
            } else {
                throw new RuntimeException("Fatal error -- didn't recognize name splitting state!");
            }
            if (state == NameSplittingState.START) {
                --i;
            }
        }
        
        addWord(result, chars, wordStartIndex, chars.length);
        return result;
    }

    private void addWord(final List<String> words, final char[] chars, final int startIndex, final int endIndex) {
        final String temp = new String(chars, startIndex, endIndex - startIndex);
        final String word = isAcronym(temp) ? temp : temp.toLowerCase();
        words.add(word);
    }

    private boolean isAcronym(final String word) {
        return (word.length() > 1 && Character.isUpperCase(word.charAt(1)));
    }
}
