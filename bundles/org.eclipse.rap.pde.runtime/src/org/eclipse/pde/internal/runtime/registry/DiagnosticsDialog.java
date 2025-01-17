/*******************************************************************************
 *  Copyright (c) 2007, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla <b.muskalla@gmx.net> - bug 207831
 *     Arnaud Mergey <a_mergey@yahoo.fr>
 *******************************************************************************/
package org.eclipse.pde.internal.runtime.registry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.*;


public class DiagnosticsDialog extends ErrorDialog {

  public DiagnosticsDialog( Shell parentShell,
                            String dialogTitle,
                            String message,
                            IStatus status,
                            int displayMask )
  {
    super( parentShell, dialogTitle, message, status, displayMask );
  }

  @Override
  protected Control createDialogArea( Composite parent ) {
    Control area = super.createDialogArea( parent );
    createDropDownList( ( Composite )area );
    return area;
  }

  @Override
  protected void createButtonsForButtonBar( Composite parent ) {
    createButton( parent, IDialogConstants.OK_ID, IDialogConstants.get().OK_LABEL, true );
  }
}
