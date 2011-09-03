package no.nr.lancelot.analysis.code.asm;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class OpcodeSequence {
    
    private final int[] opcodes;
    private final String name;
    private final String className;
    
    @SuppressWarnings("EI_EXPOSE_REP2")
    public OpcodeSequence(final int[] opcodes, final String name, final String className) {
        this.opcodes = opcodes;
        this.name = name;
        this.className = className;
    }
    
    public String getSampleName() {
        return name;
    }
    
    public String getSampleClass() {
        return className;
    }
    
    public int hashCode() {
        int $ = 7;
        for (int i = 0; i < opcodes.length; i++) {
            $ += (31 * opcodes[i]);
        }
        return $;
    }
    
    public boolean equals(final Object o) {
        if (! (o instanceof OpcodeSequence)) {
            return false;
        }
        final OpcodeSequence that = (OpcodeSequence) o;
        if (opcodes.length != that.opcodes.length) {
            return false;
        }
        for (int i = 0; i < opcodes.length; i++) {
            if (opcodes[i] != that.opcodes[i]) {
                return false;
            }
        }
        return true;
    }
    
    public String toString() {
        final StringBuilder $ = new StringBuilder();
        for (int i = 0; i < opcodes.length; i++) {
            $.append(Bytecode.bytecode(opcodes[i]) + "\n");
        }
        return $.toString();
    }

}
