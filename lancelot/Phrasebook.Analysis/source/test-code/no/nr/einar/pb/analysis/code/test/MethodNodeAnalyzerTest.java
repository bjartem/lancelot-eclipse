package no.nr.einar.pb.analysis.code.test;

import no.nr.einar.pb.analysis.code.asm.MethodNodeAnalyzer;

import org.junit.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class MethodNodeAnalyzerTest {

	@Test(expected=IllegalArgumentException.class)
	public void methodNodeCannotBeNull() {
		final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
		mna.analyze(new ClassNode(), null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void methodNodeNameCannotBeNull() {
		final MethodNode node = new MethodNode();
		final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
		mna.analyze(new ClassNode(), node);
	}

	@Test(expected=IllegalArgumentException.class)
	public void methodNodeNameCannotBeEmpty() {
		final MethodNode node = new MethodNode();
		node.name = "";
		final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
		mna.analyze(new ClassNode(), node);
	}

	@Test(expected=IllegalArgumentException.class)
	public void methodNodeDescriptionCannotBeNull() {
		final MethodNode node = new MethodNode();
		node.name = "foo";
		final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
		mna.analyze(new ClassNode(), node);
	}

	@Test(expected=IllegalArgumentException.class)
	public void methodNodeDescriptionMustBeValid() {
		final MethodNode node = new MethodNode();
		node.name = "foo";
		node.instructions.add(new InsnNode(0));
		node.desc = "invalid descriptor";
		final MethodNodeAnalyzer mna = new MethodNodeAnalyzer();
		mna.analyze(new ClassNode(), node);
	}

}
