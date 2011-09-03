package no.nr.lancelot.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JavaApplication implements Iterable<JarSummary> {

    private static String longestName = "";

    public static String getLongestName() {
        return longestName;
    }
    
    private final String appName;
    private final List<JarSummary> jars = new ArrayList<JarSummary>();

    public JavaApplication(final String appName) {
        this.appName = appName;
        if (appName.length() > longestName.length()) {
            longestName = appName;
        }
    }

    public String getName() {
        return appName;
    }

    public int getNumberOfJars() {
        return jars.size();
    }

    public Iterator<JarSummary> iterator() {
        return jars.iterator();
    }

    public void addJar(final JarSummary jarSummary) {
        jars.add(jarSummary);
    }

}
