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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public abstract class AbstractLancelotAction implements IObjectActionDelegate {
    private ISelection selection = null;

    @Override
    public void run(final IAction action) {
        if (!(selection instanceof IStructuredSelection))
            return;
    
        final List<?> selectedObjects = ((IStructuredSelection) selection).toList();
        final RichRegion selectedRegion = new RichRegion(selectedObjects);
        
        run(selectedRegion);
    }
    
    protected abstract void run(final RichRegion selectedRegion);
    
    protected void runInForeground(final IRunnableWithProgress action) {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IProgressService progressService = workbench.getProgressService();
        
        try {
            progressService.busyCursorWhile(action);
        } catch (Exception e) {
            LancelotPlugin.logException(e, "Raised while running " + action);
        }
    }
    
    @Override
    public void selectionChanged(final IAction action, final ISelection newSelection) {
        this.selection = newSelection;
    }

    @Override
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
        // Do nothing.
    }
}
