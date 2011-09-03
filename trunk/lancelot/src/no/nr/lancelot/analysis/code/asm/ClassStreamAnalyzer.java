package no.nr.lancelot.analysis.code.asm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import no.nr.lancelot.model.JavaClass;
import no.nr.lancelot.model.JavaMethod;

public final class ClassStreamAnalyzer {
    @SuppressWarnings("unchecked")
    public JavaClass analyze(final InputStream inputStream) throws IOException {
        final ClassReader reader = new ClassReader(inputStream);
        final ClassNode classNode = new ClassNode();
        reader.accept(classNode, ClassReader.SKIP_DEBUG);
        final List<MethodNode> methodNodes = classNode.methods;
        final List<JavaMethod> methodList = new ArrayList<JavaMethod>();
        final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
        for (final MethodNode methodNode : methodNodes) {
            final JavaMethod method = mna.analyze(classNode, methodNode);
            if (method != null) {
                methodList.add(method);
            }
        }
        JavaMethod[] methods = new JavaMethod[methodList.size()];
        methods = methodList.toArray(methods);
        return new JavaClass(classNode.name, methods);
    }
}
