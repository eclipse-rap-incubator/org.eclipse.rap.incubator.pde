/*******************************************************************************
 *  Copyright (c) 2007, 2017 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *     Chris Aniszczyk <zx@us.ibm.com> - initial API and implementation
 *     Arnaud Mergey - <a_mergey@yahoo.fr>
 *******************************************************************************/
package org.eclipse.pde.internal.runtime.spy.sections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.pde.internal.runtime.PDERuntimeMessages;
import org.eclipse.pde.internal.runtime.spy.SpyFormToolkit;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @since 3.4
 */
public class ActiveShellSection implements ISpySection {

	@Override
	public void build(ScrolledForm form, SpyFormToolkit toolkit, ExecutionEvent event) {
		final Shell shell = HandlerUtil.getActiveShell(event);
		Object object = shell.getData();
		if (object == null)
			return;
		Class<?> clazz = object.getClass();

		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR);
		section.clientVerticalSpacing = 9;

		section.setText(PDERuntimeMessages.get().SpyDialog_activeShell_title);

		FormText text = toolkit.createFormText(section, true);
		section.setClient(text);
		TableWrapData td = new TableWrapData();
		td.align = TableWrapData.FILL;
		td.grabHorizontal = true;
		section.setLayoutData(td);

		StringBuilder buffer = new StringBuilder();
		buffer.append("<form>"); //$NON-NLS-1$

		buffer.append(toolkit.createClassSection(text, PDERuntimeMessages.get().SpyDialog_activeShell_desc, clazz));

		buffer.append("</form>"); //$NON-NLS-1$
		text.setText(buffer.toString(), true, false);
	}

}
