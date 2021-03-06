/*******************************************************************************
 * Copyright (c) 2011, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ua.tests.help.webapp.service;


import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.help.internal.server.WebappManager;
import org.eclipse.ua.tests.help.remote.SearchServletTest;
import org.junit.Test;
import org.w3c.dom.Node;

public class AdvancedSearchServiceTest extends SearchServletTest {

	@Override
	protected Node[] getSearchHitsFromServlet(String searchWord)
			throws Exception {
		int port = WebappManager.getPort();
		URL url = new URL("http", "localhost", port, "/help/vs/service/advancedsearch?searchWord=" + URLEncoder.encode(searchWord, "UTF-8"));
		return makeServletCall(url);
	}

	@Override
	protected Node[] getSearchHitsUsingLocale(String searchWord, String locale)
			throws Exception {
		int port = WebappManager.getPort();
		URL url = new URL("http", "localhost", port, "/help/vs/service/advancedsearch?searchWord="
				+ URLEncoder.encode(searchWord, "UTF-8") + "&lang=" + locale);
		return makeServletCall(url);
	}

	@Test
	public void testRemoteSearchXMLSchema()
			throws Exception {
		int port = WebappManager.getPort();
		URL url = new URL("http", "localhost", port,
				"/help/vs/service/advancedsearch?searchWord=" + URLEncoder.encode("jehcyqpfjs vkrhjewiwh", "UTF-8"));
		URL schemaUrl = new URL("http", "localhost", port, "/help/test/schema/xml/advancedsearch.xsd");
		String schema = schemaUrl.toString();
		String uri = url.toString();
		String result = SchemaValidator.testXMLSchema(uri, schema);

		assertEquals("URL: \"" + uri + "\" is ", "valid", result);
	}

	@Test
	public void testRemoteSearchXMLSchemaExactMatchFound()
			throws Exception {
		int port = WebappManager.getPort();
		URL url = new URL("http", "localhost", port,
				"/help/vs/service/advancedsearch?searchWord=" + URLEncoder.encode("\"jehcyqpfjs vkrhjewiwh\"", "UTF-8"));
		URL schemaUrl = new URL("http", "localhost", port, "/help/test/schema/xml/advancedsearch.xsd");
		String schema = schemaUrl.toString();
		String uri = url.toString();
		String result = SchemaValidator.testXMLSchema(uri, schema);

		assertEquals("URL: \"" + uri + "\" is ", "valid", result);
	}

	@Test
	public void testRemoteSearchJSONSchema()
			throws Exception {
//		fail("Not yet implemented.");
	}

}
