package no.nr.lancelot.eclipse.analysis;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;

import no.nr.lancelot.analysis.ClassAnalysisReport;

public interface IAnalysisController {
    List<ClassAnalysisReport> getAnalysisReports();
    void run(List<IClassFile> filesForAnalysis, IProgressMonitor monitor) 
    throws IOException, CoreException;
}