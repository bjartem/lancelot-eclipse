/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package no.nr.lancelot.eclipse.view;

import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.analysis.ClassAnalysisReport;
import no.nr.lancelot.analysis.IClassAnalysisReport;
import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;

public class BugMarkingView implements ILancelotView {
    @Override
	public void run(
	    final List<ClassAnalysisReport> reports, 
	    final IProgressMonitor monitor
	) throws CoreException {
		try {
			logStart();
			final int totalWork = 3;
            monitor.beginTask("Marking bug in Eclipses Java Model", totalWork);
			
            monitor.subTask("Preparing annotation of buggy methods...");
			final List<ClassAnnotator> annotators = createAnnotators(reports); 
			monitor.worked(1);
			
            // This is the last point where cancel requests are honored.
			if (monitor.isCanceled())
			    return;
			
			long t0 = System.currentTimeMillis();
			monitor.subTask("Clearing old markers...");
			for (final ClassAnnotator annotator : annotators)
				annotator.removeExistingMarkers();
			monitor.worked(1);
			System.out.printf("a=%d\n", System.currentTimeMillis()-t0);
			

            long t1 = System.currentTimeMillis();
            monitor.subTask("Adding new markers...");
			for (final ClassAnnotator annotator : annotators) 
			    annotator.markBugs();
			monitor.worked(1);
            System.out.printf("b=%d\n", System.currentTimeMillis()-t1);
			
            logSuccessfulCompletion();
		} finally {
			monitor.done();
		}
	}
	
	private void logStart() {
		LancelotPlugin.logInfo(getClass(), "Starting annotation of buggy methods...");
	}
	
    private void logSuccessfulCompletion() {
        LancelotPlugin.logInfo(
        	getClass(), 
        	"Completed annotation."
        );
    }
	
	private List<ClassAnnotator> createAnnotators(final List<ClassAnalysisReport> reports) {
		final List<ClassAnnotator> annotators = new LinkedList<ClassAnnotator>();
		
		for (final IClassAnalysisReport report : reports) 
			try {   
				final IClassFile classFile = (IClassFile) report.getOperationKey();
				final IJavaProject javaProject = classFile.getJavaProject();
				annotators.add(new ClassAnnotator(javaProject, report));
			} catch (Exception e) {
				LancelotPlugin.logException(e, "Raised when preparing to process " + report);
				return annotators;
			}
				
		return annotators;
	}
}
