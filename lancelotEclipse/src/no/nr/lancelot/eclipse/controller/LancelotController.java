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
package no.nr.lancelot.eclipse.controller;

import java.io.IOException;
import java.util.List;

import no.nr.lancelot.analysis.ClassAnalysisReport;
import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.analysis.AnalysisController;
import no.nr.lancelot.eclipse.analysis.IAnalysisController;
import no.nr.lancelot.eclipse.gathering.AbstractGatherer;
import no.nr.lancelot.eclipse.view.ILancelotView;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;

public final class LancelotController {		
    /* Running the analysis often takes 5x the time of class 
     * file gathering and view code combined. We construct our
     * expected number of work units, which we pass to the task 
     * progress monitor, to reflect that. 
     */
    private static final int GATHERING_WORK_UNITS  = 1,
                             ANALYSIS_WORK_UNITS   = 8,
                             VIEW_WORK_UNITS = 1,
                             TOTAL_WORK_UNITS =   GATHERING_WORK_UNITS 
                                                + ANALYSIS_WORK_UNITS 
                                                + VIEW_WORK_UNITS;

	private final AbstractGatherer gatherer;
	private final IAnalysisController analysisController;
	private final ILancelotView view;
	private final IProgressMonitor mainMonitor;
	
	private List<IClassFile> filesForAnalysis = null;
	private List<ClassAnalysisReport> analysisReports = null;

	public LancelotController(
	    final AbstractGatherer gatherer,
	    final IAnalysisController analysisController,
	    final ILancelotView view,
		final IProgressMonitor mainMonitor
	) {
		if (gatherer == null || analysisController == null || view == null || mainMonitor == null)
			throw new IllegalArgumentException();
		
		this.gatherer = gatherer;
		this.analysisController = analysisController;
		this.view = view;
		this.mainMonitor = mainMonitor;

		LancelotPlugin.getDefault().ensureBackendIsConfigured();
	}
	
	public void run() throws CoreException {
	    final long startTimeMs = System.currentTimeMillis();
		final int totalWork = TOTAL_WORK_UNITS;

		try {
			logStart();			
			mainMonitor.beginTask("Lancelot analysis", totalWork);
			
			gather();
			analyze();
			passResultstoView();	
			
			final long totalTimeMs = System.currentTimeMillis() - startTimeMs;
			logCompletion(totalTimeMs);
		} catch (OperationCanceledException e) {
		    logCancellation();
		} catch (IOException e) {
			LancelotPlugin.throwWrappedException(e, "");
		} finally {
			mainMonitor.done();
		}
	}

	private void logStart() {
		LancelotPlugin.logInfo(getClass(), "Starting Lancelot analysis...");
	}

    private void logCancellation() {
        LancelotPlugin.logInfo(getClass(), "Analysis aborted by user.");
    }
    
	private void logCompletion(final long totalTimeMs) {
		LancelotPlugin.logInfo(
		    getClass(), 
		    "Lancelot analysis completed in " + totalTimeMs + " ms."
		);
	}
	
	protected void gather() throws CoreException {
	    mainMonitor.subTask("Gathering files for analysis...");
	    
		final List<IClassFile> gatherResult = gatherer.gatherFiles();
		if (gatherResult == null)
			throw new RuntimeException("Gatherer returned null. This should never happen.");
		
		this.filesForAnalysis = gatherResult;
		mainMonitor.worked(GATHERING_WORK_UNITS);
	}
	
	protected void analyze() throws CoreException, IOException {
		if (filesForAnalysis == null)
			throw new AssertionError("Files have not been gathered yet!");
		
		final IProgressMonitor analysisMonitor = new SubProgressMonitor(
		                                             mainMonitor, 
		                                             ANALYSIS_WORK_UNITS
		                                         );
		analysisController.run(filesForAnalysis, analysisMonitor);
		analysisReports = analysisController.getAnalysisReports();
	}
	
	protected void passResultstoView() throws CoreException {
		if (analysisReports == null)
			throw new AssertionError("Analysis has not been run!");
		final IProgressMonitor viewMonitor = new SubProgressMonitor(
		                                         mainMonitor, 
		                                         VIEW_WORK_UNITS
		                                     );
		view.run(analysisReports, viewMonitor);
	}
}
