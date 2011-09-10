package no.nr.lancelot.eclipse.view;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.eclipse.test.TestProject;
import no.nr.lancelot.frontend.IClassAnalysisReport;
import no.nr.lancelot.frontend.IMethodBugReport;
import no.nr.lancelot.model.JavaMethod;
import no.nr.lancelot.rulebook.Rule;
import no.nr.lancelot.rulebook.Severity;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Test;

public class SuppressingAnnotationsTest {
    private static final String TEST_PROJECT_NAME = "suppressing_annotations_test";
    
    private TestProject testProject = null;
    
    @Test
    public void testSuppressingAnnotations() throws Exception {
        testProject = TestProject.createTestProjectFromScenario(TEST_PROJECT_NAME);
        
        testProject.checkFullBuild();
        
        final List<IClassFile> classFilesToCheck = testProject.getAllClassFiles();
        
        final List<Checker> checkers = new LinkedList<Checker>();
        for (final IClassFile classFile : classFilesToCheck)
            checkers.add(new Checker(classFile));
        
        final List<IClassAnalysisReport> mockReports = new LinkedList<IClassAnalysisReport>();
        for (final Checker checker : checkers)
            mockReports.add(checker.createMockReport());
        
        new BugAnnotationLancelotView().run(mockReports, new NullProgressMonitor());
        
        for (final Checker checker : checkers)
            checker.checkMarkers();
        		
        assertEquals(2, Checker.totalNumMethodsMarked);
        assertEquals(5, Checker.totalNumMethodsIgnored);
    }
    
    private static final class Checker {
        private final IClassFile classFile;
        private final IType sourceType;
        private final IMethod[] methods;
        
        private static int totalNumMethodsMarked = 0,
        		           totalNumMethodsIgnored = 0;
        
        public Checker(final IClassFile classFile) throws JavaModelException {
            this.classFile = classFile;
            this.sourceType = findSourceType(classFile);
            this.methods = sourceType.getMethods();
        }
        
        public void checkMarkers() throws Exception {
            final IMarker[] markers = sourceType.getResource().findMarkers(
                LancelotMarkerUtil.BUG_MARKER_TYPE, 
                true, 
                IResource.DEPTH_ZERO
            );
            
            int numMarked = 0;
            for (final IMethod method : methods) {
                final String methodName = method.getElementName();
                final int nameStart = method.getNameRange().getOffset();
                
                boolean hasMarker = false;
                for (final IMarker marker : markers)
                    if (marker.getAttribute(IMarker.CHAR_START, -1) == nameStart)
                        if (hasMarker)
                            throw new Exception("Double marker on resource!");
                        else
                            hasMarker = true;
                
                if (methodName.startsWith("shouldMark")) {
                    assertTrue(methodName + " should be marked", hasMarker);
                    numMarked++;
                    totalNumMethodsMarked++;
                } else if (methodName.startsWith("shouldIgnore")) {
                    assertFalse(methodName + " should be ignored", hasMarker);
                    totalNumMethodsIgnored++;
                }
            }
            
            assertEquals(numMarked, markers.length);
        }
        
        private IType findSourceType(final IClassFile classFile) throws JavaModelException {
            return classFile.getJavaProject().findType(
                TestProject.TEST_PROJECTS_PKG_PREFIX + "." + TEST_PROJECT_NAME, 
                classFile.getElementName().replace(".class", ""), 
                new NullProgressMonitor()
            );
        }
        
        public IClassAnalysisReport createMockReport() {
            return new IClassAnalysisReport() {
                @Override public boolean hasBugs() { return true; }
                @Override public BugStatisticsData getStatisticsData() { return null; }
                
                @Override
                public String getPackageName() {
                    return sourceType.getPackageFragment().getElementName();
                }
                
                @Override
                public String getClassName() {
                    return sourceType.getElementName();
                }
                
                @Override
                public Object getOperationKey() {
                    return classFile;
                }
                
                @Override
                public List<IMethodBugReport> getMethodBugReports() {
                    final List<IMethodBugReport> res = new LinkedList<IMethodBugReport>();
                    
                    for (final IMethod method : methods) {
                        res.add(new IMethodBugReport() {
                            @Override
                            public List<Rule> getViolations() {
                                return null;
                            }
                            
                            @Override
                            public String getTextualDescription() {
                                return "bla";
                            }
                            
                            @Override
                            public JavaMethod getMethod() {
                                return new JavaMethod(
                                    method.getElementName(), "", "",
                                    new String[]{}, 0L, null, 0, false
                                );
                            }
                            
                            @Override
                            public Severity getMaximumSeverity() {
                                return Severity.INAPPROPRIATE;
                            }
                            
                            @Override
                            public List<String> getAlternativeNameSuggestions() {
                                return Collections.<String>emptyList();
                            }
                        });
                    }
                    
                    return res;
                }
            };
        }
    }
}
