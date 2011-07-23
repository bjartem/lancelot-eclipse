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
package no.nr.lancelot.eclipse.view;

import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public final class LancelotProblemViewPart extends ViewPart {
    private final static String[] TABLE_HEADER_TEXTS = {
        "Message", 
        "Resource", 
        "Location"
    };

    private Table table;

    @Override
    public void createPartControl(Composite parent) {
        table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible (true);
        
        final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.heightHint = 200;
        table.setLayoutData(data);

        new TableViewer(table);
        
        populate();
    }
     
    void populate() {
        for (final String headerText : TABLE_HEADER_TEXTS) {
            final TableColumn column = new TableColumn (table, SWT.NONE);
            column.setText (headerText);
        }
        
        try {
            final IMarker[] allMarkers = LancelotMarkerUtil.getInstance().getMarkersInWorkspace();
            for (final IMarker marker : allMarkers) {
                final TableItem item = new TableItem (table, SWT.NONE);
                item.setText(0, (String) marker.getAttribute(IMarker.MESSAGE));
                item.setText(1, marker.getResource().getName());
                item.setText(2, (String) marker.getAttribute(IMarker.LOCATION));
            }
        } catch (ClassCastException e) {
            LancelotPlugin.logException(e, "After cast of marker attribute.");
        } catch (CoreException e) {
            LancelotPlugin.logException(e);
        }
          
        for (int i = 0; i < TABLE_HEADER_TEXTS.length; ++i) 
            table.getColumn(i).pack();
    }

    @Override
    public void setFocus() {
        table.forceFocus();
    }
}
