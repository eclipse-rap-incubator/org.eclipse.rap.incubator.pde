/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Aniszczyk <zx@us.ibm.com> - initial API and implementation
 *     Willian Mitsuda <wmitsuda@gmail.com> - bug 209841
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - bug 209487
 *     Alexander Kurtakov <akurtako@redhat.com> - bug 415649
 *     Arnaud Mergey <a_mergey@yahoo.fr>              - RAP port
 *     Martin Karpisek <martin.karpisek@gmail.com> - Bug 507831
 *******************************************************************************/
package org.eclipse.pde.internal.runtime.spy;

import org.eclipse.help.IContext;
import org.eclipse.help.internal.context.Context;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.internal.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.widgets.*;
import org.osgi.framework.Bundle;

public class SpyFormToolkit extends FormToolkit {

	private static final String CLASS_PROTOCOL_PREFIX = "class://"; //$NON-NLS-1$

	private static final String BUNDLE_PROTOCOL_PREFIX = "bundle://"; //$NON-NLS-1$

	// private class SpyHyperlinkAdapter extends HyperlinkAdapter {
	//
	// private PopupDialog fDialog;
	//
	// public SpyHyperlinkAdapter(PopupDialog dialog) {
	// this.fDialog = dialog;
	// }
	//
	// @Override
	// public void linkActivated(HyperlinkEvent e) {
	// String href = (String) e.getHref();
	// if (href.startsWith(CLASS_PROTOCOL_PREFIX)) {
	// String clazz = href.substring(CLASS_PROTOCOL_PREFIX.length());
	// Bundle bundle = bundleClassByName.get(clazz);
	// SpyIDEUtil.openClass(bundle.getSymbolicName(), clazz);
	// fDialog.close();
	// } else if (href.startsWith(BUNDLE_PROTOCOL_PREFIX)) {
	// String bundle = href.substring(BUNDLE_PROTOCOL_PREFIX.length());
	// SpyIDEUtil.openBundleManifest(bundle);
	// fDialog.close();
	// }
	// }
	// }

	private class SaveImageAction extends Action {

		private Image image;

		public SaveImageAction(Image image) {
			this.image = image;
		}

		@Override
		public void run() {
			FileDialog fileChooser = new FileDialog(PDERuntimePlugin.getActiveWorkbenchShell(), SWT.SAVE);
			// fileChooser.setFileName("image"); //$NON-NLS-1$
			fileChooser.setFilterExtensions(new String[] {"*.png"}); //$NON-NLS-1$
			// fileChooser.setFilterNames(new String[] {"PNG (*.png)"}); //$NON-NLS-1$
			String filename = fileChooser.open();
			if (filename == null)
				return;

			int filetype = determineFileType(filename);
			if (filetype == SWT.IMAGE_UNDEFINED) {
				return;
			}
			ImageLoader loader = new ImageLoader();
			loader.data = new ImageData[] {image.getImageData()};
			loader.save(filename, filetype);
		}

		private int determineFileType(String filename) {
			String ext = filename.substring(filename.lastIndexOf('.') + 1);
			if (ext.equalsIgnoreCase("gif")) //$NON-NLS-1$
				return SWT.IMAGE_GIF;
			if (ext.equalsIgnoreCase("ico")) //$NON-NLS-1$
				return SWT.IMAGE_ICO;
			if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) //$NON-NLS-1$//$NON-NLS-2$
				return SWT.IMAGE_JPEG;
			if (ext.equalsIgnoreCase("png")) //$NON-NLS-1$
				return SWT.IMAGE_PNG;
			return SWT.IMAGE_UNDEFINED;
		}
	}

	// private Map<String, Bundle> bundleClassByName = new HashMap<>();
	// private PopupDialog dialog;
	private static String HELP_KEY = "org.eclipse.ui.help"; //$NON-NLS-1$

	public SpyFormToolkit(PopupDialog dialog) {
		super(Display.getDefault());
		// this.dialog = dialog;
	}

	@Override
	public FormText createFormText(Composite parent, boolean trackFocus) {
		FormText text = super.createFormText(parent, trackFocus);
		if (PDERuntimePlugin.HAS_IDE_BUNDLES) {
			// text.addHyperlinkListener(new SpyHyperlinkAdapter(dialog));
			addCopyQNameMenuItem(text);
		}
		return text;
	}

	private void addCopyQNameMenuItem(final FormText formText) {
		Menu menu = formText.getMenu();
		final MenuItem copyQNameItem = new MenuItem(menu, SWT.PUSH);
		copyQNameItem.setImage(PDERuntimePluginImages.get(PDERuntimePluginImages.IMG_COPY_QNAME));
		copyQNameItem.setText(PDERuntimeMessages.get().SpyFormToolkit_copyQualifiedName);

		// SelectionListener listener = new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// if (e.widget == copyQNameItem) {
		// Clipboard clipboard = null;
		// try {
		// clipboard = new Clipboard(formText.getDisplay());
		// clipboard.setContents(new Object[] {((String)
		// formText.getSelectedLinkHref()).substring(CLASS_PROTOCOL_PREFIX.length())},
		// new Transfer[] {TextTransfer.getInstance()});
		// } finally {
		// if (clipboard != null)
		// clipboard.dispose();
		// }
		// }
		// }
		// };
		// copyQNameItem.addSelectionListener(listener);
		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				String href = (String) formText.getSelectedLinkHref();
				copyQNameItem.setEnabled(href != null && href.startsWith(CLASS_PROTOCOL_PREFIX));
			}
		});
	}

	public String createInterfaceSection(FormText text, String title, Class<?>... clazzes) {
		StringBuilder buffer = new StringBuilder();
		if (clazzes.length > 0) {
			buffer.append("<p>"); //$NON-NLS-1$
			buffer.append(title);
			buffer.append("</p>"); //$NON-NLS-1$
			for (Class<?> clazz : clazzes) {
				buffer.append("<li bindent=\"20\" style=\"image\" value=\"interface\">"); //$NON-NLS-1$
				createClassReference(buffer, clazz);
				buffer.append("</li>"); //$NON-NLS-1$
			}
			Image image = PDERuntimePluginImages.get(PDERuntimePluginImages.IMG_INTERFACE_OBJ);
			text.setImage("interface", image); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	public String createClassSection(FormText text, String title, Class<?>... clazzes) {
		StringBuilder buffer = new StringBuilder();
		if (clazzes.length > 0) {
			buffer.append("<p>"); //$NON-NLS-1$
			buffer.append(title);
			buffer.append("</p>"); //$NON-NLS-1$
			for (Class<?> clazz : clazzes) {
				buffer.append("<li bindent=\"20\" style=\"image\" value=\"class\">"); //$NON-NLS-1$
				createClassReference(buffer, clazz);
				buffer.append("</li>"); //$NON-NLS-1$
			}
			Image image = PDERuntimePluginImages.get(PDERuntimePluginImages.IMG_CLASS_OBJ);
			text.setImage("class", image); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	public String createIdentifierSection(FormText text, String title, String[] ids) {
		StringBuilder buffer = new StringBuilder();
		if (ids.length > 0) {
			buffer.append("<p>"); //$NON-NLS-1$
			buffer.append(title);
			buffer.append("</p>"); //$NON-NLS-1$
			for (String id : ids) {
				buffer.append("<li bindent=\"20\" style=\"image\" value=\"id\">"); //$NON-NLS-1$
				buffer.append(id);
				buffer.append("</li>"); //$NON-NLS-1$
			}
			Image image = PDERuntimePluginImages.get(PDERuntimePluginImages.IMG_ID_OBJ);
			text.setImage("id", image); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	public String createHelpIdentifierSection(Widget widget) {
		return createHelpIdentifierSection(widget.getData(HELP_KEY));
	}

	public String createHelpIdentifierSection(IContext context) {
		if (context instanceof Context)
			return createHelpIdentifierSection(((Context) context).getId());
		return ""; //$NON-NLS-1$
	}

	private String createHelpIdentifierSection(Object help) {
		StringBuilder buffer = new StringBuilder();
		if (help != null) {
			buffer.append("<li bindent=\"20\" style=\"image\" value=\"contextid\">"); //$NON-NLS-1$
			buffer.append(help);
			buffer.append("</li>"); //$NON-NLS-1$
		}
		return buffer.toString();
	}

	private void createClassReference(StringBuilder buffer, Class<?> clazz) {
		// Bundle bundle = PDERuntimePlugin.HAS_IDE_BUNDLES ?
		// FrameworkUtil.getBundle(clazz) : null;
		// if (bundle != null) {
		// bundleClassByName.put(clazz.getName(), bundle);
		// buffer.append("<a href=\"").append(CLASS_PROTOCOL_PREFIX).append(
		// //$NON-NLS-1$
		// clazz.getName()).append("\">") //$NON-NLS-1$
		// .append(clazz.getSimpleName()).append("</a>"); //$NON-NLS-1$
		// } else {
			buffer.append(clazz.getName());
		// }
	}

	// TODO refactor me, I'm ugly
	public void generatePluginDetailsText(Bundle bundle, String objectId, String objectType, StringBuilder buffer, FormText text) {
		if (bundle != null) {
			String version = (bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION));

			buffer.append("<p>"); //$NON-NLS-1$
			buffer.append(PDERuntimeMessages.get().SpyDialog_contributingPluginId_title);
			buffer.append("</p>"); //$NON-NLS-1$
			buffer.append("<li bindent=\"20\" style=\"image\" value=\"plugin\">"); //$NON-NLS-1$
			if (PDERuntimePlugin.HAS_IDE_BUNDLES) {
				buffer.append("<a href=\""); //$NON-NLS-1$
				buffer.append(BUNDLE_PROTOCOL_PREFIX);
				buffer.append(bundle.getSymbolicName());
				buffer.append("\">"); //$NON-NLS-1$
			}
			buffer.append(bundle.getSymbolicName());
			buffer.append(" ("); //$NON-NLS-1$
			buffer.append(version);
			buffer.append(")"); //$NON-NLS-1$
			if (PDERuntimePlugin.HAS_IDE_BUNDLES) {
				buffer.append("</a>"); //$NON-NLS-1$
			}
			buffer.append("</li>"); //$NON-NLS-1$

			Image pluginImage = PDERuntimePluginImages.get(PDERuntimePluginImages.IMG_PLUGIN_OBJ);
			text.setImage("plugin", pluginImage); //$NON-NLS-1$

			if (objectId != null) {
				buffer.append("<p>"); //$NON-NLS-1$
				buffer.append(NLS.bind(PDERuntimeMessages.get().SpyDialog_contributingPluginId_desc, objectType));
				buffer.append("</p>"); //$NON-NLS-1$
				buffer.append("<li bindent=\"20\" style=\"image\" value=\"id\">"); //$NON-NLS-1$
				buffer.append(objectId);
				buffer.append("</li>"); //$NON-NLS-1$
			}
		}
	}

	private ToolBarManager createSectionToolbar(Section section) {
		Object object = section.getData("toolbarmanager"); //$NON-NLS-1$
		if (object instanceof ToolBarManager) {
			return (ToolBarManager) object;
		}
		ToolBarManager manager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = manager.createControl(section);
		final Cursor handCursor = Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		section.setTextClient(toolbar);
		section.setData("toolbarmanager", manager); //$NON-NLS-1$
		return manager;
	}

	public void createImageAction(Section section, Image image) {
		if (image == null)
			return;
		ToolBarManager manager = createSectionToolbar(section);
		SaveImageAction action = new SaveImageAction(image);
		action.setText(PDERuntimeMessages.get().SpyFormToolkit_saveImageAs_title);
		action.setImageDescriptor(PDERuntimePluginImages.SAVE_IMAGE_AS_OBJ);
		manager.add(action);
		manager.update(true);
	}

}
