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

import java.util.LinkedList;
import java.util.List;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.gathering.IGatherer;
import no.nr.lancelot.eclipse.gathering.GatheringHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;

import edu.umd.cs.findbugs.annotations.Nullable;

final class BuilderGatherer implements IGatherer {
    private final IProject project;
    private final IResourceDelta rootDelta;
    private final boolean needsFullSearch;
    
    private final List<IClassFile> result = new LinkedList<IClassFile>();
    
    public BuilderGatherer(final IProject project, @Nullable final IResourceDelta rootDelta) {
        if (project == null)
            throw new IllegalArgumentException();
        
        this.project = project;
        this.rootDelta = rootDelta;

        this.needsFullSearch = rootDelta == null;
    }
    
    @Override
    public List<IClassFile> gatherFiles() throws CoreException {
        logStart();
        
        if (needsFullSearch)
            runFullSearch();
        else
            runIncrementalSearch();

        logSuccessfulCompletion();
        return result;
    }
    
    private void logStart() {
        LancelotPlugin.logInfo(
            getClass(), 
            "Starting gathering of class files... [Full search: " + needsFullSearch + "]"
        );
    }
    
    private void logSuccessfulCompletion() {
        final int numFound = result.size();
        LancelotPlugin.logInfo(
            getClass(), 
            "Gathering completed successfully. Found " + numFound + " class files."
        );
    }

    private void runFullSearch() throws CoreException {
        project.accept(new FullGatherVisitor());
    }
    
    private void runIncrementalSearch() throws CoreException {
        rootDelta.accept(new IncrementalGatherVisitor());       
    }
    
    private void consider(final IResource resource) {
        final IClassFile maybeClassFile = GatheringHelper.findClassFileOrNull(resource);
        if (maybeClassFile != null)
            result.add(maybeClassFile);
    }

    private final class FullGatherVisitor implements IResourceVisitor {
        @Override
        public boolean visit(final IResource resource) {
            consider(resource);
            return true; // Signal wish to continue visiting children.
        }
    }
    
    private final class IncrementalGatherVisitor implements IResourceDeltaVisitor {
        @Override
        public boolean visit(final IResourceDelta delta) throws CoreException {
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:    // Fall through.
                case IResourceDelta.CHANGED:
                    consider(delta.getResource());
                    break;
                case IResourceDelta.REMOVED:
                    break;
            }
            
            return true; // Signal wish to continue visiting children.
        }
    }
}
