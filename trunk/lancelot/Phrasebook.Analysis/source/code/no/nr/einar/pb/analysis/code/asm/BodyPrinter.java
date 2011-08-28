package no.nr.einar.pb.analysis.code.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class BodyPrinter {
	
	private final AbstractInsnNode[] nodes;

	public BodyPrinter(final AbstractInsnNode[] nodes) {
		this.nodes = nodes;
	}
	
	public String toString() {
		final StringBuilder $ = new StringBuilder();
		for (int i = 0; i < nodes.length; i++) {
			$.append(createPrinter(nodes[i]).toString() + "\n");
		}
		return $.toString();
	}
	
	private NodePrinter createPrinter(final AbstractInsnNode node) {
		if (node instanceof InsnNode) {
			return new InsnNodePrinter((InsnNode) node);
		}
		if (node instanceof VarInsnNode) {
			return new VarInsnNodePrinter((VarInsnNode) node);
		}
		if (node instanceof TypeInsnNode) {
			return new TypeInsnNodePrinter((TypeInsnNode) node);
		}
		if (node instanceof MethodInsnNode) {
			return new MethodInsnNodePrinter((MethodInsnNode) node);
		}
		if (node instanceof LdcInsnNode) {
			return new LdcInsnNodePrinter((LdcInsnNode) node);
		}
		if (node instanceof FieldInsnNode) {
			return new FieldInsnNodePrinter((FieldInsnNode) node);
		}
		if (node instanceof JumpInsnNode) {
			return new JumpInsnNodePrinter((JumpInsnNode) node);
		}
		if (node instanceof IntInsnNode) {
			return new IntInsnNodePrinter((IntInsnNode) node);
		}
		if (node instanceof LabelNode) {
			return new LabelNodePrinter((LabelNode) node);
		}
		if (node instanceof IincInsnNode) {
			return new IincInsnNodePrinter((IincInsnNode) node);
		}
		if (node instanceof TableSwitchInsnNode) {
			return new TableSwitchInsnNodePrinter((TableSwitchInsnNode) node);
		}
		if (node instanceof LookupSwitchInsnNode) {
			return new LookupSwitchInsnNodePrinter((LookupSwitchInsnNode) node);
		}
		if (node instanceof FrameNode) {
			return new FrameNodePrinter((FrameNode) node);
		}
		throw new RuntimeException("No translation for node of type: " + node.getClass());
	}

	private static interface NodePrinter {
		public abstract String toString();
	}
	
	private static class InsnNodePrinter implements NodePrinter {
		private final InsnNode node;
		public InsnNodePrinter(final InsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode());
		}
	}
	
	private static class VarInsnNodePrinter implements NodePrinter {
		private final VarInsnNode node;
		public VarInsnNodePrinter(final VarInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.var;
		}
	}
	
	private static class TypeInsnNodePrinter implements NodePrinter {
		private final TypeInsnNode node;
		public TypeInsnNodePrinter(final TypeInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.desc;
		}
	}
	
	private static class MethodInsnNodePrinter implements NodePrinter {
		private final MethodInsnNode node;
		public MethodInsnNodePrinter(final MethodInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.owner + "." + node.name + node.desc;
		}
	}
	
	private static class LdcInsnNodePrinter implements NodePrinter {
		private final LdcInsnNode node;
		public LdcInsnNodePrinter(final LdcInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.cst;
		}
	}
	
	private static class FieldInsnNodePrinter implements NodePrinter {
		private final FieldInsnNode node;
		public FieldInsnNodePrinter(final FieldInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.name + " " + node.desc;
		}
	}
	
	private static class JumpInsnNodePrinter implements NodePrinter {
		private final JumpInsnNode node;
		public JumpInsnNodePrinter(final JumpInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.label.getLabel();
		}
	}
	
	private static class IntInsnNodePrinter implements NodePrinter {
		private final IntInsnNode node;
		public IntInsnNodePrinter(final IntInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.operand;
		}
	}

	private static class LabelNodePrinter implements NodePrinter {
		private final LabelNode node;
		public LabelNodePrinter(final LabelNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return node.getLabel().toString();
		}
	}
	
	private static class IincInsnNodePrinter implements NodePrinter {
		private final IincInsnNode node;
		public IincInsnNodePrinter(final IincInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.var + " " + node.incr;
		}
	}
	
	private static class TableSwitchInsnNodePrinter implements NodePrinter {
		private final TableSwitchInsnNode node;
		public TableSwitchInsnNodePrinter(final TableSwitchInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode()) + " " + node.min + " " + node.max;
		}
	}
	
	private static class LookupSwitchInsnNodePrinter implements NodePrinter {
		private final LookupSwitchInsnNode node;
		public LookupSwitchInsnNodePrinter(final LookupSwitchInsnNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return Bytecode.bytecode(node.getOpcode());
		}
	}
	
	private static class FrameNodePrinter implements NodePrinter {
		private final FrameNode node;
		public FrameNodePrinter(final FrameNode node) {
			this.node = node;
		}
		@Override
		public String toString() {
			return "Frame";
		}
	}

}
