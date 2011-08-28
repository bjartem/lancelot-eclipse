package no.nr.einar.pb.analysis.code.asm.test;

import static org.junit.Assert.*;
import no.nr.einar.pb.analysis.code.asm.DelegationFinder;

import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class DelegationFinderTest {
	
	
	@Test
	public void t() {
		final DelegationFinder testObj = new DelegationFinder(false);
		final AbstractInsnNode[] insns = new AbstractInsnNode[5];
		insns[0] = new VarInsnNode(Opcodes.ALOAD, 0);
		insns[1] = new FieldInsnNode(Opcodes.GETFIELD, "MyClass", "myField", "java/lang/String;");
		insns[2] = new VarInsnNode(Opcodes.ALOAD, 1);
		insns[3] = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "MyClass", "myMethod", "V(java/lang/String;)");
		insns[4] = new InsnNode(Opcodes.IRETURN);
		assertTrue(testObj.delegates(insns));
	}
	
	@Test
	public void t1() {
		final DelegationFinder testObj = new DelegationFinder(false);
		final AbstractInsnNode[] insns = new AbstractInsnNode[4];
		insns[0] = new VarInsnNode(Opcodes.ALOAD, 0);
		insns[1] = new VarInsnNode(Opcodes.ALOAD, 1);
		insns[2] = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "MyClass", "myMethod", "V(java/lang/String;)");
		insns[3] = new InsnNode(Opcodes.IRETURN);
		assertTrue(testObj.delegates(insns));
	}
	
	@Test
	public void t2() {
		final DelegationFinder testObj = new DelegationFinder(true);
		final AbstractInsnNode[] insns = new AbstractInsnNode[4];
		insns[0] = new FieldInsnNode(Opcodes.GETSTATIC, "MyClass", "myField", "java/lang/String;");
		insns[1] = new VarInsnNode(Opcodes.ALOAD, 0);
		insns[2] = new MethodInsnNode(Opcodes.INVOKESTATIC, "MyClass", "myMethod", "L(java/lang/String;)");
		insns[3] = new InsnNode(Opcodes.LRETURN);
		assertTrue(testObj.delegates(insns));
	}
	
	@Test
	public void t3() {
		final DelegationFinder testObj = new DelegationFinder(true);
		final AbstractInsnNode[] insns = new AbstractInsnNode[2];
		insns[0] = new MethodInsnNode(Opcodes.INVOKESTATIC, "MyClass", "myMethod", "V()");
		insns[1] = new InsnNode(Opcodes.RETURN);
		assertTrue(testObj.delegates(insns));
	}
	
	@Test
	public void t4() {
		final DelegationFinder testObj = new DelegationFinder(false);
		final AbstractInsnNode[] insns = new AbstractInsnNode[5];
		insns[0] = new VarInsnNode(Opcodes.ALOAD, 0);
		insns[1] = new FieldInsnNode(Opcodes.GETFIELD, "MyClass", "myField", "java/lang/String;");
		insns[2] = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "MyClass", "myMethod", "V(java/lang/String;)");
		insns[3] = new TypeInsnNode(Opcodes.CHECKCAST, "com/foosoft/ResultType;");
		insns[4] = new InsnNode(Opcodes.ARETURN);
		assertTrue(testObj.delegates(insns));
	}

}
