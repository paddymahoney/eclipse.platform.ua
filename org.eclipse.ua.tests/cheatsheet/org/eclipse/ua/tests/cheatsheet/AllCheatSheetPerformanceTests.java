/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ua.tests.cheatsheet;

import org.eclipse.ua.tests.cheatsheet.performance.OpenCheatSheetTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/*
 * Tests help performance (automated).
 */
public class AllCheatSheetPerformanceTests extends TestSuite {

	/*
	 * Returns the entire test suite.
	 */
	public static Test suite() {
		return new AllCheatSheetPerformanceTests();
	}

	/*
	 * Constructs a new performance test suite.
	 */
	public AllCheatSheetPerformanceTests() {
		addTest(OpenCheatSheetTest.suite());
	}
}
