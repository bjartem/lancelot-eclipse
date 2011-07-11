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

import static no.nr.lancelot.eclipse.test.TestUtils.join;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import no.nr.lancelot.eclipse.test.TestProject;
import no.nr.lancelot.eclipse.view.SourceTypeFinder.TypeNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * We must be able to lookup all kind of types.
 * Class, Interfaces.
 * Anonymous, Inner.
 * In-package, not-in-package.
 * Contained in another type, not contained.
 * +++
 */
public class SourceTypeFinderTest {
	private int testProjectCounter = 0;
	private TestProject testProject = null;
	private IJavaProject javaProject;
	
	private IType findSourceType(final String packageName, final String typeQualifiedName) 
	throws TypeNotFoundException {
		return SourceTypeFinder.getInstance().findSourceType(
			javaProject, 
			packageName, 
			typeQualifiedName
		);
	}

	@Before 
	public void setupTestProject() throws CoreException {
		testProject = new TestProject("SourceTypeFinderTest Test Project " + testProjectCounter);
		javaProject = testProject.getJavaProject();
		testProjectCounter++;
	}
	
	@After
	public void tearDownTestProject() throws CoreException {
		testProject.delete();
		javaProject = null;
		testProject = null;
	}
	
	@Test
	public void testFindTypeForNormalClass() throws Exception {
		final IPackageFragment bazPackage = testProject.createPackage("baz");
		
		final IType expected = testProject.createType(
		    bazPackage, 
		    "Foo.java", 
		    "public class Foo { long bar(){ return 0x2A; } }"
		);
		
		testProject.fullBuild();
		
		final IType actual = findSourceType("baz", "Foo");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testFindTypeForInnerClass() throws Exception {
		final IPackageFragment bazPackage = testProject.createPackage("baz.deeper.deep");
		
		final IType outerClassType = testProject.createType(
		    bazPackage, 
		    "Bar.java", join(
		    "public class Bar { ",
		    "    class InnerFoo {",
		    "        class ReallyInnerFoo { " +
		    "		     long bar(){",
		    "                return 0x2A; ",
	        "            }",
	        "        }",
	        "    }", 
	        "}"
		));
		final IType expected = outerClassType.getType("InnerFoo").getType("ReallyInnerFoo");
		if (!expected.exists())
			throw new RuntimeException("Test error. Could not locate inner type.");

		testProject.fullBuild();
		
		final IType actual = findSourceType("baz.deeper.deep", "Bar$InnerFoo$ReallyInnerFoo");
		assertEquals(expected, actual);
	}
	
	/* It's hard to look up anonymous types through IType handles!
	 * Thus, for this test we partially rely on exceptions not being thrown,
	 * as they would be in case of failure. 
	 */
	@Test
	public void testFindTypeForAnonymousClass() throws CoreException {
		final IPackageFragment package_ = testProject.createPackage("test.deeper");
		
		testProject.createType(
		    package_, 
		    "Bazus.java", join(
		    "public class Bazus { ",
		    "    private Object k = new Object(){", 
		    "        int foo(){ ",
		    "			return 4; ",
		    "        }",
		    "	}; ",
		    "}"
		));
		
		final IType bazus$1Type;
		try {
			bazus$1Type = findSourceType("test.deeper", "Bazus$1");
			assertTrue(bazus$1Type.exists());
			assertEquals("test.deeper.Bazus$1", bazus$1Type.getFullyQualifiedName());
			assertEquals("foo", bazus$1Type.getMethods()[0].getElementName());
		} catch (TypeNotFoundException e) {
			fail("Lookup of anonymous class failed. Exception: " + e);
			return;
		}
	}
	
	@Test
	public void testFindTypeForAnonymousClassWithinInnerClass() throws CoreException {
		final IPackageFragment package_ = testProject.createPackage("test.deeper");
	
		testProject.createType(
		    package_, 
		    "Barur.java", join(
		    "public class Barur { ",
		    "    class Inner {", 
		    "        private Object k = new Object(){", 
		    "            int foo(){ ",
		    "			    return 4; ",
		    "            }",
		    "	    }; ",
		    "    }",
		    "}"
		));
		
		final IType barur$Inner$1Type;
		try {
			barur$Inner$1Type = findSourceType("test.deeper", "Barur$Inner$1");
			assertTrue(barur$Inner$1Type.exists());
			assertEquals("test.deeper.Barur$Inner$1", barur$Inner$1Type.getFullyQualifiedName());
			assertEquals("foo", barur$Inner$1Type.getMethods()[0].getElementName());
		} catch (TypeNotFoundException e) {
			fail("Lookup of anonymous class failed. Exception: " + e);
			return;
		}
	}
	
	@Test
	public void testFindTypeForDeeplyNestedAnonAndInner() throws CoreException {
		final IPackageFragment package_ = testProject.createPackage("alpha");
	
		testProject.createType(
		    package_, 
		    "A.java", join(
    		"class A { ",
    		"    class B { ",
    		"        Object notMe = new Object(){};",
    		"        Object notMeNeither = new Object(){",
            "            class C {",
            "                Object andMe = new Object(){",
            "                    int EHT(){",
            "                        return 21 << 1;",
            "                    }",
            "                };",
            "            }",
            "        };",
    		"        Object me = new Object(){ ",
    		"            class C { ",
    		"                Object andMe = new Object(){ ",
    		"                    class D { ",
    		"                        Object notMeTho = new Object(){};",
    		"                        Object andMeMe = new Object(){ ",
    		"                            int THE(){",
    		"                                return 21 << 1;",
    		"                            };",
    		"                        };",
    		"                    }",
    		"                };",
    		"            }",
    		"        };",
    		"        Object andMeNeither = new Object(){};",
    		"    }",
    		"}"
		));
		
		final IType a$B$3$C$1$D$2Type;
		try {
			a$B$3$C$1$D$2Type = findSourceType("alpha", "A$B$3$C$1$D$2");
			assertTrue(a$B$3$C$1$D$2Type.exists());
			assertEquals("THE", a$B$3$C$1$D$2Type.getMethods()[0].getElementName());
		} catch (TypeNotFoundException e) {
			fail("Lookup of anonymous class failed. Exception: " + e);
			return;
		}
	}
		
	@Test(expected = SourceTypeFinder.TypeNotFoundException.class)
	public void testFindTypeFailureForNonExistingType() throws Exception {
		findSourceType("non.existing", "No");
	}
	
	@Test(expected = SourceTypeFinder.TypeNotFoundException.class)
	public void testFindTypeFailureForDeletedProject() throws Exception {
		/* It seems to do no harm that we call testProject.delete here
		 * even though @After does so as well.
		 */
		testProject.delete();
		findSourceType("nada", "nada");
	}
}
