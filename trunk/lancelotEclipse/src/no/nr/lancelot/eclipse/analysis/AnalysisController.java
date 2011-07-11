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
package no.nr.lancelot.eclipse.analysis;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.analysis.ClassAnalysisOperation;
import no.nr.lancelot.analysis.ClassAnalysisReport;
import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IClassFile;

public final class AnalysisController implements IAnalysisController {

	private List<ClassAnalysisReport> analysisReports = null;
	
    @Override
    public List<ClassAnalysisReport> getAnalysisReports() {
		if (analysisReports == null)
			throw new IllegalStateException("Analysis has not run!");
		return analysisReports;
	}
	
	@Override
    public void run(
        final List<IClassFile> filesForAnalysis, 
        final IProgressMonitor monitor
    ) throws IOException, CoreException {
	    if (filesForAnalysis == null || monitor == null)
            throw new IllegalArgumentException();

	    if (analysisReports != null)
	        throw new IllegalStateException("Analysis has already run!");
	    
	    final List<ClassAnalysisOperation> operations = createAnalysisOperations(filesForAnalysis);
    
		try {
			final int numClasses = operations.size(),
	                  totalWork = numClasses;

			logStart(numClasses);
			monitor.beginTask("Naming analysis", totalWork);

			final List<ClassAnalysisReport> tempAnalysisReports = 
				                                           new LinkedList<ClassAnalysisReport>();
			
			for (final ClassAnalysisOperation operation : operations) { 	
				if (monitor.isCanceled()) 
					throw new OperationCanceledException();
				
				monitor.subTask("Analyzing " + operation.getClassName() + "...");
				
				final ClassAnalysisReport analysisReport = operation.run();
				tempAnalysisReports.add(analysisReport);
				
				monitor.worked(1);
			}
			
			analysisReports = Collections.unmodifiableList(tempAnalysisReports);
			
			logCompletion();
		} finally {
			monitor.done();
		}
	}

	private void logStart(final int numClasses) {
		LancelotPlugin.logInfo(getClass(), "Starting analysis of " + numClasses + " classes...");
	}
	
    private void logCompletion() {
		LancelotPlugin.logInfo(getClass(), "Analysis completed.");
    }

	protected static List<ClassAnalysisOperation> createAnalysisOperations(
		final List<IClassFile> classFiles
	) throws CoreException {
		final List<ClassAnalysisOperation> result = new LinkedList<ClassAnalysisOperation>();
		
		for (final IClassFile cf : classFiles) {
			final String className = cf.getElementName();
			final byte[] byteCode = cf.getBytes();
			final Object key = cf;
			result.add(new ClassAnalysisOperation(className, byteCode, key));
		}

		return result;
	}
}
