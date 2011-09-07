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
/* This file is directly derived from the Bytecode Outline plugin developed 
 * by Andrei Loskutov, and released under the BSD license. 
 * (Thank you for your excellent work:-).
 * 
 * Bytecode Outline has the following original license, copied from 
 * http://andrei.gmxhome.de/bytecode/copyright.html:
 * 
 * ******************************* Copyright notice *******************************
 * Copyright (C)2004 - 2005 by Andrey Loskutov <>. 
 * All rights reserved. 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ********************************************************************************
 */
package no.nr.lancelot.eclipse.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

final class AnonymousTypeFinder {
    private AnonymousTypeFinder() {}
    
     /**
     * Get the anonymous inner class with given parent type and class number
     * (like Hello$5.class)
     * 
     * @param parentType
     *            the parent of anon. type
     * @return may return null, if we cannot find such anonymous class
     */
    public static IType findAnonymous(final IType parentType, final String name) {
        // -1 because compiler starts generated classes always with 1
        final int anonIndex = Integer.parseInt(name) - 1;

        final List<IType> anonClasses = new ArrayList<IType>();
        final IParent declaringType = parentType;

        try {
            collectAllAnonymous(anonClasses, declaringType, false);
        } catch (JavaModelException e) {
            LancelotPlugin.logException(e, "collectAllAnonymous() failed");
            return null;
        }
        
        if (anonClasses.size() <= anonIndex) 
            return null;
             
        sortAnonymous(anonClasses, parentType);
        return anonClasses.get(anonIndex);
    }
    
    /**
     * Traverses down the children tree of this parent and collect all child
     * anon. classes
     * 
     * @param result
     * @param type
     * @param isNested
     *            true to search in IType child elements too
     * @throws JavaModelException
     */
    private static void collectAllAnonymous(
        final List<IType> result, 
        final IParent parent, 
        final boolean isNested
    ) throws JavaModelException {
        for (final IJavaElement child : parent.getChildren()) {
            final boolean isAnon = isAnonymousType(child);
            if (isAnon && !isNested)
                result.add((IType) child);

            if (child instanceof IParent)
                collectAllAnonymous(result, (IParent) child, isNested || isAnon);
        }
    }
    
    private static final class AnonymClassComparator implements Comparator<IType> {

        private final IType topAncestorType;

        private final SourceOffsetComparator sourceComparator;

        private final boolean is50OrHigher;

        private final Map/* <IJavaElement,Integer> */<IType, Integer> map;

        /**
         * @param javaElement
         * @param sourceComparator
         */
        public AnonymClassComparator(IType javaElement, SourceOffsetComparator sourceComparator) {
            this.sourceComparator = sourceComparator;
            this.is50OrHigher = is50OrHigher(javaElement);
            this.topAncestorType = (IType) getLastAncestor(javaElement, IJavaElement.TYPE);
            this.map = new IdentityHashMap<IType, Integer>();
        }

        /**
         * Very simple comparison based on init/not init block decision and then
         * on the source code position
         */
        private int compare50(IType m1, IType m2) {

            IJavaElement firstAncestor1 = getFirstAncestor(m1);
            IJavaElement firstAncestor2 = getFirstAncestor(m2);

            int compilePrio1 = getCompilePrio(m1, firstAncestor1);
            int compilePrio2 = getCompilePrio(m2, firstAncestor2);

            if (compilePrio1 > compilePrio2) {
                return -1;
            } else if (compilePrio1 < compilePrio2) {
                return 1;
            } else {
                return sourceComparator.compare(m1, m2);
            }
        }

        /**
         * If "deep" is the same, then source order win. 1) from instance init
         * 2) from deepest inner from instance init (deepest first) 3) from
         * static init 4) from deepest inner from static init (deepest first) 5)
         * from deepest inner (deepest first) 7) regular anon classes from main
         * class
         * 
         * <br>
         * Note, that nested inner anon. classes which do not have different
         * non-anon. inner class ancestors, are compiled in they nesting order,
         * opposite to rule 2)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(IType m1, IType m2) {
            if (m1 == m2) {
                return 0;
            }
            if (is50OrHigher) {
                return compare50(m1, m2);
            }

            IJavaElement firstAncestor1 = getFirstAncestor(m1);
            IJavaElement firstAncestor2 = getFirstAncestor(m2);

            int compilePrio1 = getCompilePrio(m1, firstAncestor1);
            int compilePrio2 = getCompilePrio(m2, firstAncestor2);

            if (compilePrio1 > compilePrio2) {
                return -1;
            } else if (compilePrio1 < compilePrio2) {
                return 1;
            } else {
                firstAncestor1 = getFirstNonAnonymous(m1, topAncestorType);
                firstAncestor2 = getFirstNonAnonymous(m2, topAncestorType);

                if (firstAncestor1 == firstAncestor2) {
                    if (isLocal(firstAncestor1)) {
                        // we have to sort init blocks in local classes before
                        // other local class methods
                        // search for initializer block
                        boolean fromInitBlock1 = isFromInitBlock(m1);
                        boolean fromInitBlock2 = isFromInitBlock(m2);
                        if (fromInitBlock1 ^ fromInitBlock2) {
                            return fromInitBlock1 ? -1 : 1;
                        }
                    }
                    return sourceComparator.compare(m1, m2);
                }

                boolean isLocal = isLocal(firstAncestor1) || isLocal(firstAncestor2);
                if (isLocal) {
                    return sourceComparator.compare(m1, m2);
                }

                /*
                 * for anonymous classes which have first non-common
                 * non-anonymous ancestor, the order is the reversed definition
                 * order
                 */
                int topAncestorDistance1 = getTopAncestorDistance(firstAncestor1, topAncestorType);
                int topAncestorDistance2 = getTopAncestorDistance(firstAncestor2, topAncestorType);
                if (topAncestorDistance1 > topAncestorDistance2) {
                    return -1;
                } else if (topAncestorDistance1 < topAncestorDistance2) {
                    return 1;
                } else {
                    return sourceComparator.compare(m1, m2);
                }
            }
        }

        private int getCompilePrio(IType anonType, IJavaElement firstAncestor) {
            int compilePrio;
            Integer prio;
            if ((prio = map.get(anonType)) != null) {
                compilePrio = prio.intValue();
            } else {
                compilePrio = getAnonCompilePriority(anonType, firstAncestor, topAncestorType, is50OrHigher);
                map.put(anonType, Integer.valueOf(compilePrio));
            }
            return compilePrio;
        }
    }

    @SuppressWarnings(value="SE_COMPARATOR_SHOULD_BE_SERIALIZABLE", justification="Never serialized")
    private static final class SourceOffsetComparator implements Comparator<IType> {
        /**
         * First source occurrence wins.
         * 
         * @param o1
         *            should be IType
         * @param o2
         *            should be IType
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(IType o1, IType o2) {
            IType m1 = o1;
            IType m2 = o2;
            int idx1, idx2;
            try {
                ISourceRange sr1 = m1.getSourceRange();
                ISourceRange sr2 = m2.getSourceRange();
                if (sr1 == null || sr2 == null) {
                    return 0;
                }
                idx1 = sr1.getOffset();
                idx2 = sr2.getOffset();
            } catch (JavaModelException e) {
                LancelotPlugin.logException(e, "SourceOffsetComparator failed");
                return 0;
            }
            return idx1 - idx2;
        }
    }

    /**
     * @param javaElt
     * @return true, if corresponding java project has compiler setting to
     *         generate bytecode for jdk 1.5 and above
     */
    private static boolean is50OrHigher(IJavaElement javaElt) {
        IJavaProject project = javaElt.getJavaProject();
        String option = project.getOption(JavaCore.COMPILER_COMPLIANCE, true);
        boolean result = JavaCore.VERSION_1_5.equals(option);
        if (result) {
            return result;
        }
        // probably > 1.5?
        result = JavaCore.VERSION_1_4.equals(option);
        if (result) {
            return false;
        }
        result = JavaCore.VERSION_1_3.equals(option);
        if (result) {
            return false;
        }
        result = JavaCore.VERSION_1_2.equals(option);
        if (result) {
            return false;
        }
        result = JavaCore.VERSION_1_1.equals(option);
        if (result) {
            return false;
        }
        // unknown = > 1.5
        return true;
    }

    /**
     * Sort given anonymous classes in order like java compiler would generate
     * output classes, in context of given anonymous type
     * 
     * @param anonymous
     */
    private static void sortAnonymous(final List<IType> anonymous, final IType anonType) {
        SourceOffsetComparator sourceComparator = new SourceOffsetComparator();

        final AnonymClassComparator classComparator = new AnonymClassComparator(anonType, sourceComparator);
        Collections.sort(anonymous, classComparator);
    }

    private static int getAnonCompilePriority(IJavaElement elt, IJavaElement firstAncestor, IJavaElement topAncestor,
            boolean is50OrHigher) {
        if (is50OrHigher) {
            return getAnonCompilePriority50(elt, firstAncestor, topAncestor);
        }

        IJavaElement firstNonAnon = getFirstNonAnonymous(elt, topAncestor);

        // get rid of children from local types
        if (topAncestor != firstNonAnon && isLocal(firstNonAnon)) {
            return 5; // local anon. types have same prio as anon. from regular
                      // code
        }

        IJavaElement initBlock = getLastAncestor(elt, IJavaElement.INITIALIZER);
        // test is for anon. classes from initializer blocks
        if (initBlock != null) {
            if (isAnyParentLocal(firstAncestor, topAncestor)) {
                return 5; // init blocks from local types have same prio as
                          // regular
            }
            if (firstAncestor == topAncestor) {
                return 10; // instance init from top level type has top prio
            }
            if ( /* firstNonAnon != topAncestor && */!isStatic((IMember) firstNonAnon)) {
                return 8; // init blocks from non static types have top 2 prio
            }
            return 7; // init blocks from static classes
        }

        if (firstNonAnon != topAncestor) {
            if (!isStatic((IMember) firstNonAnon)) {
                return 7; // children of member types first
            }
            return 6; // children of static types
        }

        // anon. types from "regular" code
        return 5;
    }

    /**
     * 1) from instance init 2) from deepest inner from instance init (deepest
     * first) 3) from static init 4) from deepest inner from static init
     * (deepest first) 5) from deepest inner (deepest first) 6) regular anon
     * classes from main class
     * 
     * <br>
     * Note, that nested inner anon. classes which do not have different
     * non-anon. inner class ancestors, are compiled in they nesting order,
     * opposite to rule 2)
     * 
     * @param javaElement
     * @return priority - lesser mean wil be compiled later, a value > 0
     */
    private static int getAnonCompilePriority50(IJavaElement javaElement, IJavaElement firstAncestor, IJavaElement topAncestor) {

        // search for initializer block
        IJavaElement initBlock = getLastAncestor(javaElement, IJavaElement.INITIALIZER);
        // test is for anon. classes from initializer blocks
        if (initBlock != null) {
            return 10; // from inner from class init
        }

        // test for anon. classes from "regular" code
        return 5;
    }

    /**
     * @param javaElement
     * @return null, if javaElement is top level class
     */
    private static IType getFirstAncestor(IJavaElement javaElement) {
        IJavaElement parent = javaElement;
        if (javaElement.getElementType() == IJavaElement.TYPE) {
            parent = javaElement.getParent();
        }
        if (parent != null) {
            return (IType) parent.getAncestor(IJavaElement.TYPE);
        }
        return null;
    }

    /**
     * @param javaElement
     * @return first non-anonymous ancestor
     */
    private static IJavaElement getFirstNonAnonymous(IJavaElement javaElement, IJavaElement topAncestor) {
        if (javaElement.getElementType() == IJavaElement.TYPE && !isAnonymousType(javaElement)) {
            return javaElement;
        }
        IJavaElement parent = javaElement.getParent();
        if (parent == null) {
            return topAncestor;
        }
        IJavaElement ancestor = parent.getAncestor(IJavaElement.TYPE);
        if (ancestor != null) {
            return getFirstNonAnonymous(ancestor, topAncestor);
        }
        return topAncestor;
    }

    private static IJavaElement getLastAncestor(IJavaElement javaElement, int elementType) {
        IJavaElement lastFound = null;
        if (elementType == javaElement.getElementType()) {
            lastFound = javaElement;
        }
        IJavaElement parent = javaElement.getParent();
        if (parent == null) {
            return lastFound;
        }
        IJavaElement ancestor = parent.getAncestor(elementType);
        if (ancestor != null) {
            return getLastAncestor(ancestor, elementType);
        }
        return lastFound;
    }

    /**
     * @param javaElement
     * @return distance to given ancestor, 0 if it is the same, -1 if ancestor
     *         with type IJavaElement.TYPE does not exist
     */
    private static int getTopAncestorDistance(IJavaElement javaElement, IJavaElement topAncestor) {
        if (topAncestor == javaElement) {
            return 0;
        }
        IJavaElement ancestor = getFirstAncestor(javaElement);
        if (ancestor != null) {
            return 1 + getTopAncestorDistance(ancestor, topAncestor);
        }
        // this is not possible, if ancestor exists - which return value should
        // we use?
        return -1;
    }

    /**
     * @param javaElement
     * @return true, if given element is anonymous inner class
     */
    private static boolean isAnonymousType(IJavaElement javaElement) {
        try {
            return javaElement instanceof IType && ((IType) javaElement).isAnonymous();
        } catch (JavaModelException e) {
            LancelotPlugin.logException(e, "isAnonymousType() failed");
        }
        return false;
    }

    /**
     * @param type
     *            should be inner type.
     * @return true, if given element is a type defined in the initializer block
     */
    private static boolean isFromInitBlock(IType type) {
        IJavaElement ancestor = type.getAncestor(IJavaElement.INITIALIZER);
        return ancestor != null;
    }

    /**
     * @param innerType
     *            should be inner type.
     * @return true, if given element is inner class from initializer block or
     *         method body
     */
    private static boolean isLocal(IJavaElement innerType) {
        try {
            return innerType instanceof IType && ((IType) innerType).isLocal();
        } catch (JavaModelException e) {
            LancelotPlugin.logException(e, "isLocal() failed");
        }
        return false;
    }

    private static boolean isStatic(IMember firstNonAnon) {
        int topFlags = 0;
        try {
            topFlags = firstNonAnon.getFlags();
        } catch (JavaModelException e) {
            LancelotPlugin.logException(e, "isStatic() failed");
        }
        return Flags.isStatic(topFlags);
    }

    /**
     * @param elt
     * @return true, if given element is inner class from initializer block or
     *         method body
     */
    private static boolean isAnyParentLocal(IJavaElement elt, IJavaElement topParent) {
        if (isLocal(elt)) {
            return true;
        }
        IJavaElement parent = elt.getParent();
        while (parent != null && parent != topParent) {
            if (isLocal(parent)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}

