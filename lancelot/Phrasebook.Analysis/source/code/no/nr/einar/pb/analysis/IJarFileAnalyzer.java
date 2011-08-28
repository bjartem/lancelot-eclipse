package no.nr.einar.pb.analysis;

import java.io.IOException;
import java.util.jar.JarFile;

import no.nr.einar.pb.model.JavaJar;

public interface IJarFileAnalyzer {

	JavaJar analyze(final JarFile jarFile) throws IOException;

}
