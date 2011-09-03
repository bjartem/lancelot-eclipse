package no.nr.lancelot.model;

public final class JarSummary {

    private final String name;

    public JarSummary(final String jarFileName) {
        if (jarFileName == null) {
            throw new IllegalArgumentException("Jar file name cannot be null!");
        }
        if (jarFileName.length() == 0) {
            throw new IllegalArgumentException("Jar file name cannot be empty!");
        }
        this.name = jarFileName;
    }

    public String getName() {
        return name;
    }

}
