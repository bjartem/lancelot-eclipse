package no.nr.lancelot.analysis.code.asm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class OpcodeTree {
    
    private static final int MAX_NUMBER_OF_OPCODES = 10;
    
    private final OpcodeNode root = new OpcodeNode(-1);
    
    public void addBranch(final int[] opcodes) {
        if (opcodes.length > MAX_NUMBER_OF_OPCODES) {
            return;
        }
        OpcodeNode node = root;
        for (int i = 0; i < opcodes.length; i++) {
            node = childOf(node, opcodes[i], i == opcodes.length - 1);
        }
    }

    private static OpcodeNode childOf(final OpcodeNode node, final int opcode, final boolean isLastOpcode) {
        final OpcodeNode $ = obtainChild(node, opcode);
        $.registerOccurrence();
        if (isLastOpcode) {
            $.markAsTerminating();
        }
        return $;
    }
    
    private static OpcodeNode obtainChild(final OpcodeNode node, final int opcode) {
        OpcodeNode $ = node.findChild(opcode);
        if ($ == null) {
            $ = new OpcodeNode(opcode);
            node.addChild($);
        }
        return $;
    }
    
    public Set<List<OpcodeNode>> getImplementations() {
        final Set<List<OpcodeNode>> $ = new HashSet<List<OpcodeNode>>();
        final Set<OpcodeNode> terminators = gatherTerminators();
        for (final OpcodeNode t : terminators) {
            $.add(t.getOpcodeSequence());
        }
        return $;
    }
    
    private Set<OpcodeNode> gatherTerminators() {
        final Set<OpcodeNode> $ = new HashSet<OpcodeNode>();
        final Stack<Iterator<OpcodeNode>> stack = new Stack<Iterator<OpcodeNode>>(); 
        OpcodeNode node = root;
        Iterator<OpcodeNode> itor = root.iterator();
        while (!stack.isEmpty() || itor.hasNext()) {
            if (itor.hasNext()) {
                node = itor.next();
                if (node.isTerminating()) {
                    $.add(node);
                }
                stack.push(itor);
                itor = node.iterator();
            } else {
                itor = stack.pop();
            }
        }
        return $;
    }

}
