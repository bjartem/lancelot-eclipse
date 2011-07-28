package no.nr.einar.pb.analysis.code.test;

import no.nr.einar.pb.analysis.code.asm.InstructionNodeAnalyzer;
import no.nr.einar.pb.analysis.code.asm.InstructionNodeAnalyzerFactory;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class InstructionNodeAnalyzerFactoryTest {

	@Test(expected=IllegalArgumentException.class)
	public void nodeCannotBeNull() {
		InstructionNodeAnalyzerFactory.create(null);
	}

	@Test
	public void insnNode() {
		final InsnNode node = new InsnNode(0);
		create(node);
	}

	@Test
	public void varInsnNode() {
		final VarInsnNode node = new VarInsnNode(0, 0);
		create(node);
	}

	@Test
	public void typeInsnNode() {
		final TypeInsnNode node = new TypeInsnNode(0, null);
		create(node);
	}

	@Test
	public void methodInsnNode() {
		final MethodInsnNode node = new MethodInsnNode(0, null, null, null);
		create(node);
	}

	@Test
	public void fieldInsnNode() {
		final FieldInsnNode node = new FieldInsnNode(Opcodes.GETFIELD, null, null, null);
		create(node);
	}

	@Test
	public void jumpInsnNode() {
		final JumpInsnNode node = new JumpInsnNode(0, null);
		create(node);
	}

	@Test
	public void labelNode() {
		final LabelNode node = new LabelNode();
		create(node);
	}

	private static InstructionNodeAnalyzer create(final AbstractInsnNode node) {
		return InstructionNodeAnalyzerFactory.create(node);
	}

}
