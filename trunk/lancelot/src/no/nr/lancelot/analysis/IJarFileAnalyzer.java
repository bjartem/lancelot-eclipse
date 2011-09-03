package no.nr.lancelot.analysis;

import java.io.IOException;
import java.util.jar.JarFile;

import no.nr.lancelot.model.JavaJar;

public interface IJarFileAnalyzer {

    JavaJar analyze(final JarFile jarFile) throws IOException;

}
