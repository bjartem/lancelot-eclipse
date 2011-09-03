package no.nr.lancelot.analysis.code.asm;

import java.util.Stack;

import no.nr.lancelot.analysis.code.dataflow.TraceValue;
import no.nr.lancelot.analysis.code.descriptor.MethodDescriptorParser;
import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

public final class MethodInsnNodeAnalyzer implements InstructionNodeAnalyzer {

    private final MethodInsnNode node;
    private Value instance = null;

    public MethodInsnNodeAnalyzer(final MethodInsnNode node) {
        this.node = node;
    }

    public void check(final Frame frame, final MethodAnalysisData data) {
        initInstanceField(frame, data);
        
//      if (isThisUsedAsParameter(frame, data)) {
//          data.setAttribute(Attribute.INVERTED_METHOD_CALL);
//      }

        if (isMethodCallOnParameter()) {
            data.setAttribute(Attribute.METHOD_CALL_ON_PARAMETER);
        }

        if (isMethodCallOnField()) {
            data.setAttribute(Attribute.METHOD_CALL_ON_FIELD);
            if (isParameterPassedOn(frame, data)) {
                data.setAttribute(Attribute.PARAMETER_TO_FIELD_CALL);
            }
        }

        if (isSameVerbCall(data)) {
            if (isSameNameCall(data)) {
                Attribute attr = Attribute.SAME_NAME_CALL;
                if (isRecursiveCall(data)) {
                    if (isTrueRecursiveCall()) {
                        attr = Attribute.RECURSIVE_CALL;                    
                    } else {
//                      attr = Attribute.FAKE_RECURSION;
                    }
                }
                data.setAttribute(attr);
            } else {
                data.setAttribute(Attribute.SAME_VERB_CALL);
            }
        }
    }

    private boolean isSameVerbCall(final MethodAnalysisData data) {
        final String definingMethodVerb = parseVerb(data.getMethodName());
        final String callingMethodVerb = parseVerb(node.name);
        return callingMethodVerb.equals(definingMethodVerb);
    }

    private String parseVerb(final String methodName) {
        int end = 0;
        for (int i = 0; i < methodName.length(); i++) {
            final char c = methodName.charAt(i);
            if (Character.isLowerCase(c)) {
                ++end;
            } else {
                break;
            }
        }
        final String $ = methodName.substring(0, end);
        return $;
    }

    private boolean isParameterPassedOn(Frame frame, MethodAnalysisData data) {
        return isParameter(frame, data, new ValuePredicate() {
            @Override
            public boolean check(Value v) {
                if (v instanceof TraceValue) {
                    return ((TraceValue) v).isMaybeParameterValue(); 
                }
                return false;
            }
        });
    }

    private static interface ValuePredicate {
        public boolean check(Value v);
    }

    private boolean isParameter(final Frame frame, final MethodAnalysisData data, final ValuePredicate predicate) {
        boolean $ = false;
        final int noParams = data.getMethodTypeTuple().getParameterTypes().length;
        if (frame != null) {
            if (noParams < frame.getStackSize()) {
                final Stack<Value> stack = new Stack<Value>();
                for (int i = 0; i < noParams; i++) {
                    final Value val = frame.pop();
                    $ = $ || predicate.check(val);
                    stack.push(val);
                }
                for (int i = 0; i < noParams; i++) {
                    frame.push(stack.pop());
                }
            }
        }
        return $;
    }

    private void initInstanceField(Frame frame, MethodAnalysisData data) {
        if (isStaticCall()) {
            return;
        }
        initInstanceFieldForReal(frame, data);
    }

    private void initInstanceFieldForReal(Frame frame, MethodAnalysisData data) {
        final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple(node.desc);
        final int noParams = tuple.getParameterTypes().length;
        if (frame != null) {
            if (noParams < frame.getStackSize()) {
                final Stack<Value> stack = new Stack<Value>();
                for (int i = 0; i < noParams; i++) {
                    stack.push(frame.pop());
                }
                instance = frame.pop();
                frame.push(instance);
                for (int i = 0; i < noParams; i++) {
                    frame.push(stack.pop());
                }
            }
        }
    }

    private boolean isMethodCallOnParameter() {
//      if (isStaticCall()) {
//          return false;
//      }
        return isCallOnParameter();
    }

    private boolean isMethodCallOnField() {
//      if (isStaticCall()) {
//          return false;
//      }
        return isCallOnField();
    }

    private boolean isTrueRecursiveCall() {
        return isStaticCall() || isCallOnThis();
    }

    private boolean isSameSignature(final MethodAnalysisData data) {
        final MethodTypeTuple mtt = MethodDescriptorParser.getMethodTypeTuple(node.desc);
        return equalSignatures(data.getMethodTypeTuple(), mtt);
    }

    private boolean isSameNameCall(final MethodAnalysisData data) {
        return data.getMethodName().equals(node.name);
    }
    
    private boolean isCallOnParameter() {
        if (instance instanceof TraceValue) {
            return ((TraceValue) instance).isMaybeParameterValue();
        }
        return false;
    }

    private boolean isCallOnThis() {
        if (instance instanceof TraceValue) {
            return ((TraceValue) instance).isMaybeThisReferenceValue();
        }
        return false;
    }

    private boolean isCallOnField() {
        if (instance instanceof TraceValue) {
            return ((TraceValue) instance).isMaybeFieldValue();
        }
        return false;
    }

    private boolean isRecursiveCall(final MethodAnalysisData data) {
        return isCallOnDefiningClass(data) && isSameSignature(data);
    }

    private boolean isStaticCall() {
        return node.getOpcode() == Opcodes.INVOKESTATIC;
    }

    private boolean isCallOnDefiningClass(final MethodAnalysisData data) {
        assert data != null;
        assert data.getClassName() != null;
        assert node != null;

        return data.getClassName().equals(node.owner);
    }

    private static boolean equalSignatures(final MethodTypeTuple mtt1,
            final MethodTypeTuple mtt2) {
        return mtt1.getReturnType().equals(mtt2.getReturnType()) && sameTypes(mtt1.getParameterTypes(), mtt2.getParameterTypes());
    }

    private static boolean sameTypes(final String[] types1,
            final String[] types2) {
        if (types1.length != types2.length) {
            return false;
        }
        for (int i = 0; i < types1.length; i++) {
            if (!types1[i].equals(types2[i])) {
                return false;
            }
        }
        return true;
    }

}
