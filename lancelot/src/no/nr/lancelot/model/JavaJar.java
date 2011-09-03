package no.nr.lancelot.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JavaJar implements Iterable<JavaClass> {
    
    private static String longestName = "";
    private static String longestDir = "";
    
    public static String getLongestDir() {
        return longestDir;
    }

    public static String getLongestName() {
        return longestName;
    }
    
    private final String jarFileName;
    private final String jarFileDir;
    private final List<JavaClass> classList = new ArrayList<JavaClass>();
    
    public JavaJar(final String jarFileName, final String jarFileDir) {
        this.jarFileName = jarFileName;
        this.jarFileDir = jarFileDir;
        if (jarFileName.length() > longestName.length()) {
            longestName = jarFileName;
        }if (jarFileDir.length() > longestDir.length()) {
            longestDir = jarFileDir;
        }
    }
    
    public String getJarFileName() {
        return jarFileName;
    }
    
    public String getJarFileDirectory() {
        return jarFileDir;
    }

    public void addJavaClass(final JavaClass javaClass) {
        classList.add(javaClass);
    }

    public int getNumberOfClassFiles() {
        return classList.size();
    }

    public Iterator<JavaClass> iterator() {
        return classList.iterator();
    }


}
