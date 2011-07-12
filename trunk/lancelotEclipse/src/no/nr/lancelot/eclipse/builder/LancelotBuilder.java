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
package no.nr.lancelot.eclipse.builder;

import java.util.Map;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.analysis.AnalysisController;
import no.nr.lancelot.eclipse.analysis.IAnalysisController;
import no.nr.lancelot.eclipse.controller.LancelotController;
import no.nr.lancelot.eclipse.gathering.GatheringHelper;
import no.nr.lancelot.eclipse.gathering.AbstractGatherer;
import no.nr.lancelot.eclipse.view.BugMarkingView;
import no.nr.lancelot.eclipse.view.ILancelotView;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import edu.umd.cs.findbugs.annotations.Nullable;

public final class LancelotBuilder extends IncrementalProjectBuilder {
    public static final String BUILDER_ID = LancelotPlugin.PLUGIN_ID + ".builder";
    
    @Override
    protected void clean(final IProgressMonitor monitor) {    
        // TODO! Should remove all markers.
    }

    // TODO! When refactoring this. We should catch all exceptions (including runtime exceptions)
    //       and handle them gracefully. Maybe some report to the user, etc?
    @Override
    @SuppressWarnings("rawtypes") 
    protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) 
    throws CoreException {
        if (GatheringHelper.hasJavaBuildErrors(getProject())) {
            LancelotPlugin.logTrace("Project has compilation errors, aborting analysis.");
            return null;
        }

        try {
            final AbstractGatherer gatherer = createGatherer();
            final IAnalysisController analyser = new AnalysisController();
            final ILancelotView annotator = new BugMarkingView();
            new LancelotController(gatherer, analyser, annotator, monitor).run();
        } catch (Exception e) {
            LancelotPlugin.logException(e, "LanceLotBuilder.build() failed");
        } 
        
        return null;
    }
    
    private AbstractGatherer createGatherer() {
        @Nullable 
        final IResourceDelta possibleDelta = getDelta(getProject());
        return new BuilderGatherer(getProject(), possibleDelta);
    }
}
