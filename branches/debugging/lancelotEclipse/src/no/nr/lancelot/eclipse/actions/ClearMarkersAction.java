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

import java.util.List;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.view.LancelotMarkerUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public final class ClearMarkersAction extends AbstractLancelotAction {
    @Override
    protected void run(final RichRegion selectedRegion) {
        runInForeground(new IRunnableWithProgress() {
            @Override
            public void run(final IProgressMonitor monitor) {
                final List<IResource> topLevelResources = selectedRegion.getTopLevelResources();
                
                final int totalWork = topLevelResources.size();
                monitor.beginTask("Removing Lancelot markers", totalWork);
                
                try {
                    for (final IResource resource : topLevelResources) {
                        monitor.subTask("Recursively removing markers on " + resource + "...");
                        LancelotMarkerUtil.getInstance().removeMarkersOn(resource);
                        monitor.worked(1);
                    }
                } catch (CoreException e) {
                    LancelotPlugin.logException(e, "Marker removal failed");
                } finally {
                    monitor.done();
                }
            }
        });
    }
}
