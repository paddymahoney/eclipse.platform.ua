/*******************************************************************************
 * Copyright (c) 2010, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.help.internal.base.scope;

import org.eclipse.help.base.AbstractHelpScope;
import org.eclipse.help.base.IScopeHandle;

public class ScopeHandle implements IScopeHandle{

	private AbstractHelpScope scope;
	private String id;

	public ScopeHandle( String id, AbstractHelpScope scope) {
		this.id = id;
		this.scope = scope;
	}

	@Override
	public AbstractHelpScope getScope() {
		return scope;
	}

	@Override
	public String getId() {
		return id;
	}

}
