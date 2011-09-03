package no.nr.lancelot.analysis.code.asm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class OpcodeNode implements Iterable<OpcodeNode> {
    
    private final int opcode;
    private final List<OpcodeNode> children = new ArrayList<OpcodeNode>();
    private int occurrences = 0;
    private int terminated = 0;
    private boolean isTerminating = false;
    private OpcodeNode parent = null;
    
    public OpcodeNode(final int opcode) {
        this.opcode = opcode;
    }

    public int getOccurrences() {
        return occurrences;
    }
    
    public int getOpcode() {
        return opcode;
    }
    
    public boolean isTerminating() {
        return isTerminating;
    }

    public void addChild(final OpcodeNode child) {
        children.add(child);
        child.setParent(this);
    }
    
    private OpcodeNode getParent() {
        return this.parent;
    }
    
    private void setParent(final OpcodeNode parent) {
        this.parent = parent;
    }
    
    public int getNumberOfImplemenations() {
        return terminated;
    }
    
    public OpcodeNode findChild(final int childOpcode) {
        for (final OpcodeNode child : children) {
            if (child.getOpcode() == childOpcode) {
                return child;
            }
        }
        return null;
    }

    public void markAsTerminating() {
        isTerminating = true;
        ++terminated;
    }

    public void registerOccurrence() {
        ++occurrences;
    }

    @Override
    public Iterator<OpcodeNode> iterator() {
        return children.iterator();
    }
    
    public List<OpcodeNode> getOpcodeSequence() {
        final Stack<OpcodeNode> stack = new Stack<OpcodeNode>();
        OpcodeNode node = this;
        while (node != null) {
            stack.push(node);
            node = node.getParent();
        }
        final List<OpcodeNode> $ = new ArrayList<OpcodeNode>();
        while (! stack.isEmpty()) {
            $.add(stack.pop());
        }
        return $;
    }

}
