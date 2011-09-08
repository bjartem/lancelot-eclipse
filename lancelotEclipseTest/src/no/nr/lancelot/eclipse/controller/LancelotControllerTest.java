package no.nr.lancelot.eclipse.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.List;

import no.nr.lancelot.eclipse.analysis.IAnalyser;
import no.nr.lancelot.eclipse.gathering.IGatherer;
import no.nr.lancelot.eclipse.view.ILancelotView;
import no.nr.lancelot.frontend.IClassAnalysisReport;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public final class LancelotControllerTest {
    private final Mockery context = new JUnit4Mockery();

    private class StandardScene {
        final Sequence sequence = context.sequence(null);
        final IGatherer gatherer = context.mock(IGatherer.class);
        final IAnalyser analyzer = context.mock(IAnalyser.class);
        final ILancelotView view = context.mock(ILancelotView.class);
        final LancelotController controller = new LancelotController(
                                              gatherer, analyzer, view, new NullProgressMonitor());
    }
    
    @Test
    public void testCatchingOfBadGatherer() throws Exception {
        final StandardScene scene = new StandardScene();
        
        context.checking(new Expectations(){{
            oneOf (scene.gatherer).gatherFiles(); 
            will(returnValue(null)); // The simulated bad behavior.
        }});
        
        try {
            scene.controller.run();
            fail("No exception throwed!");
        } catch (RuntimeException e) {
            // The actual test!
            assertTrue(e.getMessage().toLowerCase().contains("gatherer")); 
            assertTrue(e.getMessage().toLowerCase().contains("null"));
        }
    }
    
    @Test
    public void testCatchingOfBadAnalyzer() throws Exception {
        final StandardScene scene = new StandardScene();
        
        final List<IClassFile> filesForAnalysis = Collections.emptyList();
        
        context.checking(new Expectations(){{
            oneOf (scene.gatherer).gatherFiles(); 
            will(returnValue(filesForAnalysis));
            
            oneOf (scene.analyzer).run(with(equal(filesForAnalysis)), with(aNonNull(SubProgressMonitor.class))); 
            will(returnValue(null)); // The simulated bad behavior.
        }});
        
        try {
            scene.controller.run();
            fail("No exception throwed!");
        // The actual test!
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().toLowerCase().contains("analyzer")); 
            assertTrue(e.getMessage().toLowerCase().contains("null"));
        }
    }
    
    @Test
    public void testSubSystemInvocationSequence() throws Exception {
        final StandardScene scene = new StandardScene();
        
        final List<IClassFile> filesForAnalysis = Collections.emptyList();
        final List<IClassAnalysisReport> analysisReports = Collections.emptyList();
        
        context.checking(new Expectations(){{
            oneOf (scene.gatherer).gatherFiles(); 
            inSequence(scene.sequence);
            will(returnValue(filesForAnalysis));
            
            oneOf (scene.analyzer).run(with(equal(filesForAnalysis)), with(aNonNull(SubProgressMonitor.class)));
            inSequence(scene.sequence);
            will(returnValue(analysisReports));
            
            oneOf (scene.view).run(with(equal(analysisReports)), with(aNonNull(SubProgressMonitor.class)));
            inSequence(scene.sequence);
        }});
        
        scene.controller.run();
    }
}
