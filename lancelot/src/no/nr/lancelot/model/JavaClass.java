package no.nr.lancelot.model;

import java.util.Arrays;
import java.util.Iterator;

public final class JavaClass implements Iterable<JavaMethod> {

    private static String longestShortName = "";
    private static String longestNamespace = "";

    public static String getLongestShortName() {
        return longestShortName;
    }
    
    public static String getLongestNamespace() {
        return longestNamespace;
    }
    
    private final String qualifiedName;
    private final JavaMethod[] methods;

    public JavaClass(final String qualifiedName,
            final JavaMethod[] methods) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("Class name cannot be null!");
        }
        if (methods == null) {
            throw new IllegalArgumentException("Methods cannot be null!");
        }
        if (qualifiedName.length() == 0) {
            throw new IllegalArgumentException("Short name cannot be empty!");
        }
        for (final IJavaMethod method : methods) {
            if (method == null) {
                throw new IllegalArgumentException("The methods list cannot contain null references!");
            }
        }
        this.qualifiedName = qualifiedName;
        this.methods = clone(methods);
    }

    public String getNamespace() {
        final int lastSlashIndex = qualifiedName.lastIndexOf('/');
        if (lastSlashIndex < 0) {
            return "";
        } else {
            final String result = qualifiedName.substring(0, lastSlashIndex);
            if (result.length() > longestNamespace.length()) {
                longestNamespace = result;
            }
            return result;
        }
    }

    public String getShortName() {
        final int lastSlashIndex = qualifiedName.lastIndexOf('/');
        final String result = qualifiedName.substring(lastSlashIndex + 1);
        if (result.length() > longestShortName.length()) {
            longestShortName = result;
        }
        return result;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public JavaMethod[] getMethods() {
        return clone(methods);
    }

    private JavaMethod[] clone(final JavaMethod[] original) {
        final JavaMethod[] result = new JavaMethod[original.length];
        for (int i = 0; i < original.length; i++) {
            result[i] = original[i];
        }
        return result;
    }

    public Iterator<JavaMethod> iterator() {
        return Arrays.asList(methods).iterator();
    }

}
