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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
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
			IProject project = null;
			
			if (element instanceof IProject) 
				project = (IProject) element;
			else if (element instanceof IAdaptable) 
				project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
			
			if (project != null) 
				toggleNature(project);
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

	private void toggleNature(final IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (LancelotNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i,
							natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);

					LancelotPlugin.logInfo("Removed Lancelot nature.");
					return;
				}
			}

			// Add the nature
			final String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = LancelotNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			
			LancelotPlugin.logInfo("Added Lancelot nature.");
		} catch (CoreException e) {
			e.printStackTrace();
			// @TODO!! FIXME
		}
	}
}
