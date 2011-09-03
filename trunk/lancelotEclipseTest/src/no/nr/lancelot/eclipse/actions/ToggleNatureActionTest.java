package no.nr.lancelot.eclipse.actions;

import static org.junit.Assert.fail;
import no.nr.lancelot.eclipse.nature.LancelotNature;
import no.nr.lancelot.eclipse.test.TestProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.Before;
import org.junit.Test;


public class ToggleNatureActionTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testRun() throws Exception {
        final TestProject testProjectA = new TestProject("ToggleNatureActionTestProjectA"),
                          testProjectB = new TestProject("ToggleNatureActionTestProjectB");

        try {
            if (hasLancelotNature(testProjectA) || hasLancelotNature(testProjectB)) 
                throw new Exception("test error");
            
            final Object[] selectedObjects = { testProjectA.getJavaProject(), testProjectB.getJavaProject() };
            
            final ToggleNatureAction action = new ToggleNatureAction();
            action.selectionChanged(null, new StructuredSelection(selectedObjects));
           
            action.run(null);
        } finally {
            testProjectA.delete();
            testProjectB.delete();
        }
    }

    private boolean hasLancelotNature(final TestProject testProjectA) throws CoreException {
        for (final String natureId : testProjectA.getProject().getDescription().getNatureIds())
            if (natureId.equals(LancelotNature.NATURE_ID))
                return true;
        return false;
    }

    @Test
    public void testToggleNature() {
        fail("Not yet implemented");
    }

    @Test
    public void testIsLancelotNaturePresent() {
        fail("Not yet implemented");
    }

    @Test
    public void testRemoveLancelotNature() {
        fail("Not yet implemented");
    }

    @Test
    public void testFindLancelotNatureId() {
        fail("Not yet implemented");
    }

    @Test
    public void testReportDisabling() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddLancelotNature() {
        fail("Not yet implemented");
    }

    @Test
    public void testReportEnabling() {
        fail("Not yet implemented");
    }
}
