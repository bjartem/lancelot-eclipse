package no.nr.lancelot.analysis;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import no.nr.lancelot.analysis.code.asm.ClassStreamAnalyzer;
import no.nr.lancelot.model.JavaClass;
import no.nr.lancelot.model.JavaJar;

public final class JarFileAnalyzer extends AbstractJarFileAnalyzer {

    public JavaJar analyze(final JarFile jarFile) throws IOException {
        if (jarFile == null) {
            throw new IllegalArgumentException("Jar file cannot be null!");
        }
        final String[] pathTuple = splitJarFilePath(jarFile);
        final JavaJar jar = new JavaJar(pathTuple[0], pathTuple[1]);
        final ClassStreamAnalyzer csa = new ClassStreamAnalyzer();
        for (final JarEntry entry : new EnumerationIterator<JarEntry>(jarFile.entries())) {
            final File classFilePath = new File(entry.getName());
            if (classFilePath.getName().endsWith(".class")) {
                final JavaClass javaClass = csa.analyze(jarFile.getInputStream(entry));
                jar.addJavaClass(javaClass);
            }
        }
        return jar;
    }

}
