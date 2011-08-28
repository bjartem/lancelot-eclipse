package no.nr.einar.pb.analysis.code.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public final class MethodNodeFactory {

	private MethodNodeFactory() {}

	public static MethodNode create(
			final String className,
			final String methodName) {
		if (className == null) {
			throw new IllegalArgumentException("Class name cannot be null!");
		}
		if (methodName == null) {
			throw new IllegalArgumentException("Method name cannot be null!");
		}
		final String classFilePath = TestFileUtils.getClassFile(className);
		MethodNode result = null;
		try {
			result = createNode(classFilePath, methodName);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private static MethodNode createNode(final String classFilePath,
			final String methodName) throws IOException {
		final InputStream stream = new FileInputStream(classFilePath);
		final ClassReader reader = new ClassReader(stream);
		final ClassNode classNode = new ClassNode();
		reader.accept(classNode, ClassReader.SKIP_DEBUG);
		return findMethodNode(classNode, methodName);
	}

	@SuppressWarnings("unchecked")
	private static MethodNode findMethodNode(final ClassNode classNode,
			final String methodName) {
		final List<MethodNode> methods = classNode.methods;
		for (final MethodNode method : methods) {
			if (methodName.equals(method.name)) {
				return method;
			}
		}
		return null;
	}

}
