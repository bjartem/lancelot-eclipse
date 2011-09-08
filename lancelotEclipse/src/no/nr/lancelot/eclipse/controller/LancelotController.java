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

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.analysis.IAnalyser;
import no.nr.lancelot.eclipse.gathering.IGatherer;
import no.nr.lancelot.eclipse.view.ILancelotView;
import no.nr.lancelot.frontend.IClassAnalysisReport;
import no.nr.lancelot.frontend.IClassAnalysisReport.BugStatisticsData;

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

    private final IGatherer gatherer;
    private final IAnalyser analyzer;
    private final ILancelotView view;
    private final IProgressMonitor mainMonitor;
    
    private List<IClassFile> filesForAnalysis = null;
    private List<IClassAnalysisReport> analysisReports = null;

    public LancelotController(
        final IGatherer gatherer,
        final IAnalyser analyzer,
        final ILancelotView view,
        final IProgressMonitor mainMonitor
    ) {
        if (gatherer == null || analyzer == null || view == null || mainMonitor == null)
            throw new IllegalArgumentException();
        
        this.gatherer = gatherer;
        this.analyzer = analyzer;
        this.view = view;
        this.mainMonitor = mainMonitor;

        LancelotPlugin.getDefault().ensureBackendIsConfigured();
    }
    
    public void run() throws CoreException {
        final long startTimeMs = System.currentTimeMillis();

        try {
            logStart();            
            mainMonitor.beginTask("Lancelot analysis", TOTAL_WORK_UNITS);
            
            gather();
            analyze();
            passResultstoView();    
            
            final long stopTimeMs = System.currentTimeMillis();
            final long spentTimeMs = stopTimeMs - startTimeMs;
            logCompletion(spentTimeMs);
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
    
    private void logCompletion(final long spentTimeMs) {
        LancelotPlugin.logInfo(
            getClass(), 
            "Lancelot analysis ran in " + spentTimeMs + " ms." + makeCoverageReport()
        );
    }
    
    private String makeCoverageReport() {
        int totalMethodCount = 0, 
            totalCoveredMethodCount = 0,
            totalBuggyMethodCount = 0;
        
        for (IClassAnalysisReport report : analysisReports) {
            final BugStatisticsData statisticsData = report.getStatisticsData();
            totalMethodCount += statisticsData.numMethodsTotal;
            totalCoveredMethodCount += statisticsData.numMethodsCovered;
            totalBuggyMethodCount += statisticsData.numMethodsBuggy;
        }

        return String.format(
            "[Total method count: %d, covered method count: %d, buggy method count: %d, "
                                                + "coverage percent: %f  bug percent: %f]",
            totalMethodCount,
            totalCoveredMethodCount,
            totalBuggyMethodCount,
            100 * totalCoveredMethodCount / (double) Math.max(1.0, totalMethodCount),
            100 * totalBuggyMethodCount / (double) Math.max(1.0, totalCoveredMethodCount)
        );
    }

    private void gather() throws CoreException {
        mainMonitor.subTask("Gathering files for analysis...");
        
        final List<IClassFile> gatherResult = gatherer.gatherFiles();
        if (gatherResult == null)
            throw new RuntimeException("Gatherer returned null. This should never happen.");
        
        this.filesForAnalysis = gatherResult;
        mainMonitor.worked(GATHERING_WORK_UNITS);
    }
    
    private void analyze() throws CoreException, IOException {
        if (filesForAnalysis == null)
            throw new AssertionError("Files have not been gathered yet!");
        
        final IProgressMonitor analysisMonitor = new SubProgressMonitor(
            mainMonitor, 
            ANALYSIS_WORK_UNITS
        );

        final List<IClassAnalysisReport> analysisReports = analyzer.run(
            filesForAnalysis, 
            analysisMonitor
        );
        if (analysisReports == null)
            throw new RuntimeException("Analyzer returned null. This should never happen.");
        
        this.analysisReports = analysisReports;
            
    }
    
    private void passResultstoView() throws CoreException {
        if (analysisReports == null)
            throw new AssertionError("Analysis has not been run!");
       
        final IProgressMonitor viewMonitor = new SubProgressMonitor(mainMonitor, VIEW_WORK_UNITS);
        view.run(analysisReports, viewMonitor);
    }
}
