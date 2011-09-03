package no.nr.lancelot.eclipse.gathering;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import no.nr.lancelot.eclipse.test.TestProject;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GatheringHelperTest {
    private TestProject testProject = null;
    private IPackageFragment fooPackage = null;

    @Before
    public void setup() throws Exception {
        testProject = new TestProject("gatheringHelperTestProject");
        fooPackage  = testProject.createPackage("foo");
    }
    
    @After
    public void tearDown() throws Exception {
        testProject.delete();
        fooPackage = null;
        testProject = null; 
    }
    
    @Test
    public void testHasJavaBuildErrorsTrue() throws Exception {
        final IType[] faultyTypes = {
            testProject.createType(
                fooPackage,
                "Faulty.java",
                "public class Faulty { int k({ return } }"
            ),
            testProject.createType(
                fooPackage, 
                "WishIWasCeeLang.java", 
                "public class WishIWasCeeLang { static class A { boolean k() { return 1; } } }"
            )
        };
        
        testProject.fullBuild();
        
        for (final IType ft : faultyTypes) 
            assertTrue(GatheringHelper.hasJavaBuildErrors(ft.getResource()));
    }
    
    @Test
    public void testHasJavaBuildErrorsFalse() throws Exception {
        final IType okType = testProject.createType(
            fooPackage,
            "Nice.java",
            "public class Nice { { int i = 1; } }"
        );
        
        testProject.fullBuild();
        
        assertFalse(GatheringHelper.hasJavaBuildErrors(okType.getResource()));
    }
}
