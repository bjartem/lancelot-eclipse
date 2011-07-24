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

import static org.junit.Assert.assertEquals;
import no.nr.lancelot.eclipse.test.TestProject;
import no.nr.lancelot.eclipse.view.MethodMap.MatchType;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MethodMapTest {
    private TestProject testProject = null;
    private IType alphaType = null;
    private IMethod[] alphaMethods = null;
    
    @Before
    public void setup() throws Exception {
        testProject = TestProject.createTestProjectFromScenario("methodmaptest");    
        alphaType = testProject.getJavaProject().findType(
                                            "no.nr.lancelot.eclipse.testdata.methodmaptest.Alpha");
        alphaMethods = alphaType.getMethods();
    }
    
    @After
    public void tearDown() throws Exception {
        alphaMethods = null;
        alphaType = null;
        testProject.delete();
        testProject = null;
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testExceptionForNullArgumentToConstructor() throws JavaModelException {
        new MethodMap(null);
    }

    @Test
    public void testFindMethod() throws JavaModelException {
        assertEquals(
            alphaMethods[1],
            new MethodMap(alphaType).findMethod(
                "second",
                new String[]{ "java.lang.Object", "java.util.Set" },
                "java.util.List"
            )
        );
        
        assertEquals(
            alphaMethods[2],
            new MethodMap(alphaType).findMethod(
                "second",
                new String[]{ "java.util.Set", "java.lang.Object" },
                "java.util.List"
            )
        );
        
        assertEquals(
            alphaMethods[3],
            new MethodMap(alphaType).findMethod(
                "third",
                new String[]{
                    "int",
                    "no.nr.lancelot.eclipse.testdata.methodmaptest.Beta.BetaAlpha", 
                    "float"
                 },
                "void"
            )
        );
        
        assertEquals(
            alphaMethods[4],
            new MethodMap(alphaType).findMethod(
                "third",
                new String[]{
                    "int",
                    "no.nr.lancelot.eclipse.testdata.methodmaptest.Beta.BetaAlpha", 
                    "int"
                 },
                "void"
            )
        );
    }

    @Test
    public void testMatchTypeBasic() throws JavaModelException {
        assertEquals(
            MatchType.DEFINITE_MATCH, 
            MethodMap.match(alphaMethods[0], alphaMethods[0].getParameterTypes()[0], "int[][]")
        );
        
        assertEquals(
            MatchType.DEFINITE_MATCH, 
            MethodMap.match(alphaMethods[0], alphaMethods[0].getParameterTypes()[1], "double")
        );
    }
    
    @Test
    public void testMatchType1() throws JavaModelException {
        assertEquals(
            MatchType.MISMATCH, 
            MethodMap.match(alphaMethods[0], alphaMethods[0].getParameterTypes()[0], "float[]")
        );
        
        assertEquals(
            MatchType.MISMATCH, 
            MethodMap.match(alphaMethods[0], alphaMethods[0].getParameterTypes()[0], "T")
        );
        
        assertEquals(
            MatchType.MISMATCH, 
            MethodMap.match(alphaMethods[0], alphaMethods[0].getParameterTypes()[0], "String")
        );
    }
    
    @Test
    public void testMatchNonBasicType() throws JavaModelException {
        assertEquals(
            MatchType.DEFINITE_MATCH, 
            MethodMap.match(
                alphaMethods[0], 
                alphaMethods[0].getParameterTypes()[2], 
                "java.util.List"
            )
        );
        
        assertEquals(
            MatchType.MISMATCH, 
            MethodMap.match(
                alphaMethods[0], 
                alphaMethods[0].getParameterTypes()[2], 
                "java.lang.String"
            )
        );
        
        assertEquals(
            MatchType.POSSIBLE_MATCH, 
            MethodMap.match(
                alphaMethods[0], 
                alphaMethods[0].getParameterTypes()[3], 
                "java.lang.Object"
            )
        );
    }

    @Test
    public void testConcatSplittedName() {
        assertEquals("Name", MethodMap.concatSplittedName(new String[]{ "", "Name" }));
        assertEquals(
            "a.bb.ccc.Name.Core", 
            MethodMap.concatSplittedName(new String[]{ "a.bb.ccc", "Name.Core" })
        );
    }

    @Test
    public void testExtractRawType() {
        assertEquals("Map", MethodMap.extractRawType("[[[QMap<QString;*>;"));
    }

    @Test
    public void testAppendArrays() {
        assertEquals("int[][]", MethodMap.appendArrays("int", 2));
        assertEquals("java.lang.String", MethodMap.appendArrays("java.lang.String", 0));
    }
}
