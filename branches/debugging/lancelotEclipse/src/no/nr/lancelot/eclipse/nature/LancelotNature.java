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
package no.nr.lancelot.eclipse.nature;

import no.nr.lancelot.eclipse.LancelotPlugin;
import no.nr.lancelot.eclipse.builder.LancelotBuilder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class LancelotNature implements IProjectNature {
    public static final String NATURE_ID = LancelotPlugin.PLUGIN_ID + ".nature";

    private IProject project = null;

    public void configure() throws CoreException {
        final IProjectDescription desc = project.getDescription();
        final ICommand[] commands = desc.getBuildSpec();

        for (final ICommand command : commands) {
            final boolean builderIsAlreadyLoaded = isLancelotBuildCommand(command);
            if (builderIsAlreadyLoaded) {
                LancelotPlugin.logError("Lancelot nature configuration failed: Already loaded!");
                return;
            }
        }

        final ICommand[] newCommands = new ICommand[commands.length + 1];
        System.arraycopy(commands, 0, newCommands, 0, commands.length);
        final ICommand command = desc.newCommand();
        command.setBuilderName(LancelotBuilder.BUILDER_ID);
        newCommands[newCommands.length - 1] = command;
        desc.setBuildSpec(newCommands);

        project.setDescription(desc, null);
    }
    
    public void deconfigure() throws CoreException {
        final IProjectDescription description = getProject().getDescription();
        final ICommand[] commands = description.getBuildSpec();

        for (int i = 0; i < commands.length; ++i)
            if (isLancelotBuildCommand(commands[i])) {
                final ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                project.setDescription(description, null);
                return;
            }

        LancelotPlugin.logError("Lancelot nature deconfiguration failed: Not loaded!");
    }

    private boolean isLancelotBuildCommand(final ICommand command) {
        return command.getBuilderName().equals(LancelotBuilder.BUILDER_ID);
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(final IProject project) {
        this.project = project;
    }
}
