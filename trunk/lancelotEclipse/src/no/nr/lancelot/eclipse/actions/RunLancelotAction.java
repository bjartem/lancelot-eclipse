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
package no.nr.lancelot.eclipse.actions;

import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.analysis.Analyser;
import no.nr.lancelot.eclipse.analysis.IAnalyser;
import no.nr.lancelot.eclipse.controller.LancelotController;
import no.nr.lancelot.eclipse.gathering.GatheringHelper;
import no.nr.lancelot.eclipse.gathering.IGatherer;
import no.nr.lancelot.eclipse.view.BugAnnotationLancelotView;
import no.nr.lancelot.eclipse.view.ILancelotView;
import no.nr.lancelot.eclipse.view.MessageDialogHelper;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class RunLancelotAction extends AbstractLancelotAction {
    @Override
    protected void run(final RichRegion region) {
        try {
            if (!workspaceIsAutoBuilding()) {
                signalAutoBuildingProblem();
                return;
            }
            
            if (containsJavaBuildErrors(region)) {
                signalCompilationProblem();
                return;
            }
    
            runController(region);
        } catch (CoreException e) {
            LancelotPlugin.logException(e);
        }
    }

    private boolean workspaceIsAutoBuilding() {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IWorkspaceDescription description = workspace.getDescription();
        return description.isAutoBuilding();
    }
    
    private void signalAutoBuildingProblem() {
        MessageDialogHelper.openInformation(
            "Auto-building disabled", 
            "Lancelot can not run when auto-building is disabled."
        );
    }
    
    private boolean containsJavaBuildErrors(final RichRegion region) throws CoreException {
        for (final IResource resource : region.getTopLevelResources())
            if (GatheringHelper.hasJavaBuildErrors(resource)) 
                return true;
        return false;
    }
    
    private void signalCompilationProblem() {
        MessageDialogHelper.openInformation(
            "Lancelot cannot run",
            "Some of the selected resources has Java compilation errors. " +
            "Lancelot cannot run on code that does not compile."
        );
    }

    private void runController(final RichRegion region) {
        final IGatherer gatherer = createGatherer(region);
        final IAnalyser analyser = createAnalyser();
        final ILancelotView view = createView();

        runInForeground(new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor) {
                try {
                    new LancelotController(gatherer, analyser, view, monitor).run();
                } catch (CoreException e) {
                    LancelotPlugin.logException(e);
                }
            }
        });
    }

    private IGatherer createGatherer(final RichRegion region) {
        final IResource[] generatedResources = JavaCore.getGeneratedResources(region, false);
        final IGatherer gatherer = new Gatherer(generatedResources);
        return gatherer;
    }
    
    private IAnalyser createAnalyser() {
        return new Analyser();
    }

    private ILancelotView createView() {
        return new BugAnnotationLancelotView();
    }
    
    @SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Data flows in private scope.")
    protected static final class Gatherer implements IGatherer {
        private final IResource[] resources;

        public Gatherer(final IResource[] generatedResources) {
            this.resources = generatedResources;
        }

        @Override
        public List<IClassFile> gatherFiles() throws CoreException {
            final List<IClassFile> result = new LinkedList<IClassFile>();
            
            for (final IResource resource : resources) {
                final IClassFile maybeClassFile = GatheringHelper.findClassFileOrNull(resource);
                if (maybeClassFile != null)
                    result.add(maybeClassFile);
            }
            
            return result;
        }
    }
}
