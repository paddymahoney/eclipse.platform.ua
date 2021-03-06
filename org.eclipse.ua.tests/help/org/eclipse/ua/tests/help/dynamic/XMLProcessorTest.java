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
package org.eclipse.ua.tests.help.dynamic;

import java.io.InputStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.help.internal.base.HelpEvaluationContext;
import org.eclipse.help.internal.dynamic.DocumentReader;
import org.eclipse.help.internal.dynamic.ExtensionHandler;
import org.eclipse.help.internal.dynamic.FilterHandler;
import org.eclipse.help.internal.dynamic.IncludeHandler;
import org.eclipse.help.internal.dynamic.ProcessorHandler;
import org.eclipse.help.internal.dynamic.XMLProcessor;
import org.eclipse.help.ui.internal.HelpUIPlugin;
import org.eclipse.ua.tests.plugin.UserAssistanceTestPlugin;
import org.eclipse.ua.tests.util.FileUtil;
import org.eclipse.ua.tests.util.XMLUtil;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class XMLProcessorTest {

	@Before
	public void setUp() throws Exception {
		// activate the UI plug-in for UI filtering ability
		HelpUIPlugin.getDefault();
	}

	private void xmlProcess(String path) throws Exception {
		DocumentReader reader = new DocumentReader();
		ProcessorHandler[] handlers = new ProcessorHandler[] {
				new IncludeHandler(reader, Platform.getNL()),
				new ExtensionHandler(reader, Platform.getNL()),
				new FilterHandler(HelpEvaluationContext.getContext())
		};
		XMLProcessor processor = new XMLProcessor(handlers);
		Bundle bundle = UserAssistanceTestPlugin.getDefault().getBundle();
		try (InputStream in = bundle.getEntry(FileUtil.getResultFile(path)).openStream();
				InputStream in2 = processor.process(bundle.getEntry(path).openStream(),
						'/' + bundle.getSymbolicName() + '/' + path, "UTF-8")) {
			XMLUtil.assertXMLEquals("XML content was not processed correctly: " + path, in, in2);
		}
	}

	@Test
	public void testExtension() throws Exception {
		xmlProcess("data/help/dynamic/extension.xml");
	}

	@Test
	public void testFilter() throws Exception {
		xmlProcess("data/help/dynamic/filter.xml");
	}

	@Test
	public void testInclude() throws Exception {
		xmlProcess("data/help/dynamic/include.xml");
	}

	@Test
	public void testIndex() throws Exception {
		xmlProcess("data/help/dynamic/index.xml");
	}

	@Test
	public void testSimple() throws Exception {
		xmlProcess("data/help/dynamic/simple.xml");
	}

	@Test
	public void testToc() throws Exception {
		xmlProcess("data/help/dynamic/toc.xml");
	}

	@Test
	public void testXhtml() throws Exception {
		xmlProcess("data/help/dynamic/xhtml.xml");
	}

	@Test
	public void testEntities() throws Exception {
		xmlProcess("data/help/dynamic/entities.xml");
	}
}
