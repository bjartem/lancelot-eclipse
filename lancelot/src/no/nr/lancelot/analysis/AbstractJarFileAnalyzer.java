package no.nr.lancelot.analysis;

import java.io.File;
import java.util.jar.JarFile;

public abstract class AbstractJarFileAnalyzer implements IJarFileAnalyzer {

    protected final String[] splitJarFilePath(final JarFile jarFile) {
        final String jarFilePath = jarFile.getName();
        final File f = new File(jarFilePath);
        return new String[] { f.getName(), f.getParent() };
    }

}
