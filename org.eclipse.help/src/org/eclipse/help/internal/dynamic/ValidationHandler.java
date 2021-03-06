/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.dynamic;

import java.util.Map;

import org.eclipse.help.ITocContribution;
import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.help.internal.UAElement;

/*
 * A handler that ensures that specified attributes are there for certain nodes,
 * or removes the node and logs an error. Also logs warnings for deprecated elements.
 */
public class ValidationHandler extends ProcessorHandler {

	private Map<String, String[]> requiredAttributes;
	private Map<String, String> deprecatedElements;

	/*
	 * Creates a new validator that looks for the given mapping of
	 * element names to required attribute names.
	 */
	public ValidationHandler(Map<String, String[]> requiredAttributes) {
		this(requiredAttributes, null);
	}

	/*
	 * Creates a new validator that looks for the given mapping of
	 * element names to required attribute names, as well as a mapping
	 * of deprecated element names to suggested new element names.
	 */
	public ValidationHandler(Map<String, String[]> requiredAttributes, Map<String, String> deprecatedElements) {
		this.requiredAttributes = requiredAttributes;
		this.deprecatedElements = deprecatedElements;
	}

	@Override
	public short handle(UAElement element, String id) {
		if (deprecatedElements != null) {
			String suggestion = deprecatedElements.get(element.getElementName());
			if (suggestion != null) {
				String msg = "The \"" + element.getElementName() + "\" element is deprecated in \"" + id + "\"; use \"" + suggestion + "\" instead."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				HelpPlugin.logWarning(msg);
			}
		}
		String[] attributes = requiredAttributes.get(element.getElementName());
		if (attributes != null) {
			for (int i=0;i<attributes.length;++i) {
				if (element.getAttribute(attributes[i]) == null) {
					String msg = "Required attribute \"" + attributes[i] + "\" missing from \"" + element.getElementName() + "\" element"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					if (id != null) {
						msg += " in \"" + id + '"'; //$NON-NLS-1$
					}
					UAElement parent = element.getParentElement();
					if (parent != null && !(parent instanceof ITocContribution)) {
						msg += " (skipping element)"; //$NON-NLS-1$
						parent.removeChild(element);
						HelpPlugin.logError(msg);
						return HANDLED_SKIP;
					}
					else {
						throw new IllegalArgumentException(msg);
					}
				}
			}
		}
		return UNHANDLED;
	}
}
