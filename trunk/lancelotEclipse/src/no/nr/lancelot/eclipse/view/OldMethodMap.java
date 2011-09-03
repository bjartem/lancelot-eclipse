/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Norwegian Computing Center - initial API and implementation
 ******************************************************************************/
package no.nr.lancelot.eclipse.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/* Alas this is an ugly beast, and it's not even working 100%!
 */
public final class OldMethodMap {
    private final IType type;
    private final Map<String, List<IMethod>> nameToMethodMap;

    public OldMethodMap(final IType type) throws JavaModelException {
        if (type == null)
            throw new IllegalArgumentException();

        this.type = type;
        this.nameToMethodMap = buildMap();
    }

    private Map<String, List<IMethod>> buildMap() throws JavaModelException {
        final Map<String, List<IMethod>> tempRes = new HashMap<String, List<IMethod>>();

        for (final IMethod method : type.getMethods()) {
            final String name = method.getElementName();
            if (!tempRes.containsKey(name))
                tempRes.put(name, new LinkedList<IMethod>());
            tempRes.get(name).add(method);
        }

        return Collections.unmodifiableMap(tempRes);
    }

    public IMethod findMethod(
        final String methodName, 
        final String[] paramTypes, 
        final String returnType
    ) throws JavaModelException {
        final List<IMethod> candidates = nameToMethodMap.get(methodName);
        final int numCandidates = candidates == null ? 0 : candidates.size();

        if (numCandidates == 0)
            return null;
        else if (numCandidates == 1)
            return candidates.get(0);
        else
            return searchAmongOverloaded(candidates, paramTypes, returnType);
    }

    protected static IMethod searchAmongOverloaded(
        final List<IMethod> candidates, 
        final String[] paramTypes,
        final String returnType
    ) throws JavaModelException {
        IMethod bestGuess = null;
        int numGuesses = 0;

        for (final IMethod candidate : candidates) {
            final MatchType matchResult = match(candidate, paramTypes, returnType);
            if (matchResult == MatchType.DEFINITE_MATCH)
                return candidate;
            else if (matchResult == MatchType.POSSIBLE_MATCH) {
                bestGuess = candidate;
                numGuesses++;
            }
        }

        // If we only have one guess, it actually must be correct ;)
        if (numGuesses == 1)
            return bestGuess;

        // Give up.
        return null;
    }

    protected static MatchType match(
        final IMethod candidate, 
        final String[] paramTypes, 
        final String returnType
    ) throws JavaModelException {
        // We might spend a lot of time when we dig deeper, so we do an
        // early test for parameter count here, hoping to exit early.
        if (candidate.getParameterTypes().length != paramTypes.length)
            return MatchType.MISMATCH;
        
        final MatchType returnTypeMatch = match(candidate, returnType);
        if (returnTypeMatch == MatchType.MISMATCH)
            return MatchType.MISMATCH;

        return MatchType.mostPessimistic(returnTypeMatch, match(candidate, paramTypes));
    }

    protected static MatchType match(final IMethod candidate, final String[] paramTypes) 
    throws JavaModelException {
        final String[] actualParamTypes = candidate.getParameterTypes();

        assert actualParamTypes.length == paramTypes.length : "Should be checked in calling method";
        
        MatchType answer = MatchType.DEFINITE_MATCH;
        for (int i = 0; i < actualParamTypes.length; ++i) {
            answer = MatchType.mostPessimistic(
                answer, 
                match(candidate, actualParamTypes[i], paramTypes[i])
            );
            if (answer == MatchType.MISMATCH) 
                break;
        }

        return answer;
    }

    protected static MatchType match(final IMethod candidate, final String returnType) 
    throws JavaModelException {
        final String actualReturnType = candidate.getReturnType();
        return match(candidate, actualReturnType, returnType);
    }

    protected static MatchType match(
        final IMethod candidate, 
        final String eclipseSignature,
        final String lancelotSignature
    ) throws JavaModelException {
        if (isPrimitiveOrVoid(eclipseSignature))
            return MatchType.definitelyOrNo(
                Signature.toString(eclipseSignature).equals(lancelotSignature)
            );

        return matchNonBasic(candidate, eclipseSignature, lancelotSignature);
    }

    protected static boolean isPrimitiveOrVoid(final String eclipseSignature) {
        final int kind = Signature.getTypeSignatureKind(Signature.getElementType(eclipseSignature));
        return kind == Signature.BASE_TYPE_SIGNATURE;
    }

    protected static MatchType matchNonBasic(
        final IMethod candidate, 
        final String eclipseSignature,
        final String lancelotSignature
    ) throws JavaModelException {
        final String rawType = extractRawType(eclipseSignature);

        final IType declaringType = candidate.getDeclaringType();


        long t0 = System.currentTimeMillis();
        final String[][] resolvedMatches = declaringType.resolveType(rawType);
        long ts = System.currentTimeMillis() - t0;
        if (ts > 60)
            System.out.printf("es: %s  ls: %s   r:%d\n", eclipseSignature, lancelotSignature, ts);

        if (resolvedMatches == null)
            return MatchType.POSSIBLE_MATCH;

        for (final String[] resolvedMatch : resolvedMatches) {
            final String fullyQualifiedName = concatSplittedName(resolvedMatch);

            final int arrayCount = Signature.getArrayCount(eclipseSignature);
            final String translatedSignature = appendArrays(fullyQualifiedName, arrayCount);

            return MatchType.definitelyOrNo(translatedSignature.equals(lancelotSignature));
        }

        throw new RuntimeException("Should never reach here, check contract for resolveType!");
    }

    protected static String concatSplittedName(final String[] splittedName) {
        final String packageName = splittedName[0], typeQualifiedName = splittedName[1];
        return packageName + (packageName.isEmpty() ? "" : ".") + typeQualifiedName;
    }

    protected static String extractRawType(final String richSignature) {
        return Signature.toString(
                   Signature.getElementType(
                       Signature.getTypeErasure(
                           Signature.removeCapture(richSignature))));
    }

    protected static String appendArrays(final String signature, final int arrayCount) {
        final StringBuilder resBuilder = new StringBuilder(signature);
        for (int i = 0; i < arrayCount; ++i)
            resBuilder.append("[]");
        return resBuilder.toString();
    }

    protected enum MatchType {
        DEFINITE_MATCH, 
        POSSIBLE_MATCH, 
        MISMATCH;

        static MatchType mostPessimistic(final MatchType left, final MatchType right) {
            if (left == MISMATCH || right == MISMATCH)
                return MISMATCH;

            if (left == POSSIBLE_MATCH || right == POSSIBLE_MATCH)
                return POSSIBLE_MATCH;

            return DEFINITE_MATCH;
        }

        static MatchType definitelyOrNo(final boolean proposition) {
            return proposition ? DEFINITE_MATCH : MISMATCH;
        }
    };
}
