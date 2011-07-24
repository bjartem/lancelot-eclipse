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

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.nature.LancelotNature;
import no.nr.lancelot.eclipse.view.MessageDialogHelper;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public final class ToggleNatureAction implements IObjectActionDelegate {
    private ISelection selection;

    @Override
    public void run(final IAction action) {
        if (! (selection instanceof IStructuredSelection))
            return;
        
        for (final Object element : ((IStructuredSelection) selection).toArray()) {
            IJavaProject javaProject = null;
            
            if (element instanceof IJavaProject) 
                javaProject = (IJavaProject) element;
            else if (element instanceof IAdaptable) 
                javaProject = (IJavaProject) ((IAdaptable) element).getAdapter(IJavaProject.class);
            
            if (javaProject != null) 
                toggleNature(javaProject.getProject());
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

    protected void toggleNature(final IProject project) {
        try {
            if (isLancelotNaturePresent(project))
                removeLancelotNature(project);
            else
                addLancelotNature(project);
        } catch (CoreException e) {
            LancelotPlugin.logException(e);
        }
    }

    protected boolean isLancelotNaturePresent(final IProject project) throws CoreException {
        final IProjectDescription description = project.getDescription();
        final String[] natures = description.getNatureIds();
        
        for (final String natureIds : natures) 
            if (LancelotNature.NATURE_ID.equals(natureIds)) 
                return true;
        
        return false;
    }
    
    protected void removeLancelotNature(final IProject project) throws CoreException {
        final IProjectDescription description = project.getDescription();
        final String[] natures = description.getNatureIds();
        final int natureId = findLancelotNatureId(project);

        final String[] newNatures = new String[natures.length - 1];
        System.arraycopy(natures, 0, newNatures, 0, natureId);
        System.arraycopy(natures, natureId + 1, newNatures, natureId, natures.length - natureId - 1);
        description.setNatureIds(newNatures);
        project.setDescription(description, null);

         MessageDialogHelper.openInformation(
             "Lancelot disabled", 
             "Automatical analysis disabled for '" + project.getName() + "'"
         );
    }
    
    protected int findLancelotNatureId(final IProject project) throws CoreException {
        final IProjectDescription description = project.getDescription();
        final String[] natures = description.getNatureIds();
      
        for (int natureId = 0; natureId < natures.length; ++natureId) 
            if (LancelotNature.NATURE_ID.equals(natures[natureId])) 
                return natureId;
        
        throw new IllegalStateException("project does not have the Lancelot nature!");
    }

    protected void addLancelotNature(final IProject project) throws CoreException {
        final IProjectDescription description = project.getDescription();
        final String[] natures = description.getNatureIds();

        // Add the nature
        final String[] newNatures = new String[natures.length + 1];
        System.arraycopy(natures, 0, newNatures, 0, natures.length);
        newNatures[natures.length] = LancelotNature.NATURE_ID;
        description.setNatureIds(newNatures);
        project.setDescription(description, null);
        
        MessageDialogHelper.openInformation(
            "Lancelot enabled", 
            "Automatical analysis enabled for '" + project.getName() + "'"
        );
    }
}
