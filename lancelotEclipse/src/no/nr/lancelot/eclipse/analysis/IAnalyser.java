package no.nr.lancelot.eclipse.analysis;

import java.io.IOException;
import java.util.List;

import no.nr.lancelot.frontend.IClassAnalysisReport;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;

public interface IAnalyser {
    List<IClassAnalysisReport> run(List<IClassFile> filesForAnalysis, IProgressMonitor monitor) 
    throws IOException, CoreException;
}