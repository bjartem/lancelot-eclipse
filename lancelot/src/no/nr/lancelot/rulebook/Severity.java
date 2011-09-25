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
package no.nr.lancelot.rulebook;

public enum Severity {
    NOTIFY(0x1),
    INAPPROPRIATE(0x2),
    FORBIDDEN(0x4);

    private final int flag;

    private Severity(final int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
