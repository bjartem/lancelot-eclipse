package no.nr.lancelot.eclipse.view;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.frontend.MethodLocationFinder;
import no.nr.lancelot.model.JavaMethod;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public final class MethodMap {
    private final IType type;
    private final IClassFile classFile;

    private final Map<String, List<IMethod>> nameToMethodMap;
    private Map<String, IMethod> descriptorToMethodMap = null;

    public MethodMap(final IType type, final IClassFile classFile) throws JavaModelException {
        if (type == null || classFile == null)
            throw new IllegalArgumentException();

        this.type = type;
        this.classFile = classFile;
        this.nameToMethodMap = builNameToMethoddMap();
    }

    private Map<String, List<IMethod>> builNameToMethoddMap() throws JavaModelException {
        final Map<String, List<IMethod>> tempRes = new HashMap<String, List<IMethod>>();

        for (final IMethod method : type.getMethods()) {
            final String name = method.getElementName();
            if (!tempRes.containsKey(name))
                tempRes.put(name, new LinkedList<IMethod>());
            tempRes.get(name).add(method);
        }

        return Collections.unmodifiableMap(tempRes);
    }

    public IMethod findMethod(final JavaMethod lancelotMethod) {
        final List<IMethod> candidates = nameToMethodMap.get(lancelotMethod.getMethodName());
        final int numCandidates = candidates == null ? 0 : candidates.size();

        if (numCandidates == 0)
            return null;
        else if (numCandidates == 1) 
            return candidates.get(0);
        else {
            ensureDescriptorToMethodMapIsBuilt();
            
            final String key = MethodLocationFinder.makeKey(
                lancelotMethod.getMethodName(), 
                lancelotMethod.getDescriptor()
            );
            if (descriptorToMethodMap.containsKey(key))
                return descriptorToMethodMap.get(key);
            
            return null;
        }
    }

    private void ensureDescriptorToMethodMapIsBuilt() {
        if (descriptorToMethodMap != null)
            return;
        descriptorToMethodMap = buildDescriptorToMethodMap();
    }

    private Map<String, IMethod> buildDescriptorToMethodMap() {
        final Map<String, IMethod> res = new HashMap<String, IMethod>();

        try {
            final Map<String, Integer> firstInstructionLocations = 
                MethodLocationFinder.findMethodLocations(classFile.getBytes());
            
            final boolean locationsAreMissing = firstInstructionLocations.size() < 
                                                                          type.getMethods().length;
            if (locationsAreMissing)
                return res;
            
            for (final IMethod method : type.getMethods()) {
                final int firstLinePos = calcDefinitionLine(method);
                long bestDist = Integer.MAX_VALUE;
                String bestKey = null;
                
                for (
                     final Map.Entry<String, Integer> firstInstLoc : 
                     firstInstructionLocations.entrySet()
                ) {
                    final int dist = firstInstLoc.getValue() - firstLinePos;
                    if (dist >= 0 && dist < bestDist) {
                        bestDist = dist;
                        bestKey = firstInstLoc.getKey();
                    }
                }
                
                if (bestKey != null) {
                    res.put(
                        bestKey, 
                        method
                    );
                    firstInstructionLocations.remove(bestKey);
                }
            }
        } catch (JavaModelException e) {
            LancelotPlugin.logException(e);
        } catch (IOException e) {
            LancelotPlugin.logException(e);
        }
        
        return res;
    }

    private int calcDefinitionLine(final IMethod method) throws JavaModelException {
        final char[] source = method.getCompilationUnit().getSource().toCharArray();
        final int startPos = method.getSourceRange().getOffset();
        return MethodAnnotator.findLineNumber(source, startPos);
    }
}
