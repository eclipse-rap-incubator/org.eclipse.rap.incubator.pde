/*******************************************************************************
 * Copyright (c) 2016 Google Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Xenos (Google) - initial API and implementation
 *     Patrik Suzzi <psuzzi@gmail.com> - Bug 499226
 *******************************************************************************/
package org.eclipse.pde.internal.runtime.spy.dialogs;

import org.eclipse.osgi.util.NLS;
import org.eclipse.rap.rwt.RWT;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.pde.internal.runtime.spy.dialogs.messages"; //$NON-NLS-1$

	public String LayoutSpyDialog_shell_text;
	public String LayoutSpyDialog_button_open_child;
	public String LayoutSpyDialog_button_open_parent;
	public String LayoutSpyDialog_button_select_control;
	public String LayoutSpyDialog_button_show_overlay;
	public String LayoutSpyDialog_label_children;
	public String LayoutSpyDialog_label_layout;
	public String LayoutSpyDialog_label_no_parent_control_selected;
	public String LayoutSpyDialog_warning_bounds_outside_parent;
	public String LayoutSpyDialog_warning_control_overlaps_siblings;
	public String LayoutSpyDialog_warning_control_partially_clipped;
	public String LayoutSpyDialog_warning_grab_horizontally_scrolling;
	public String LayoutSpyDialog_warning_grab_vertical_scrolling;
	public String LayoutSpyDialog_warning_hint_for_horizontally_scrollable;
	public String LayoutSpyDialog_warning_hint_for_vertically_scrollable;
	public String LayoutSpyDialog_warning_not_grabbing_horizontally;
	public String LayoutSpyDialog_warning_not_grabbing_vertically;
	public String LayoutSpyDialog_warning_prefix;
	public String LayoutSpyDialog_warning_shorter_than_preferred_size;
	public String LayoutSpyDialog_warning_unexpected_compute_size;
	public String LayoutSpyDialog_warning_zero_size;

	public static Messages get() {
		Class<Messages> clazz = Messages.class;
		Object result = RWT.NLS.getISO8859_1Encoded(BUNDLE_NAME, clazz);
		return (Messages) result;
	}
}
