package org.eclipse.help.internal.server;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import java.io.*;
import java.util.*;

import java.net.URLDecoder;

/**
 * Manages a help URL.  Note that there is a limitation in handling
 * queries; only one instance of a name=value pair exists at at time.
 */
public class HelpURL {
	protected String url; // url string
	protected StringBuffer query;
	protected HashMap arguments = null;
	protected long contentSize; // length of input data
	/**
	 * HelpURL constructor comment.
	 */
	public HelpURL(String url) {
		this(url, "");
	}
	/**
	 * HelpURL constructor comment.
	 */
	public HelpURL(String url, String query) {
		this.url = url;
		this.query = new StringBuffer(query);

		parseQuery(query);
	}
	/**
	 * 
	 */
	public void addQuery(String newQuery) {
		if (newQuery != null && !"".equals(newQuery)) {
			query.append('&').append(newQuery);
			parseQuery(newQuery);
		}
	}
	/**
	 * Insert the method's description here.
	 * 
	 * @return long
	 */
	public long getContentSize() {
		return contentSize;
	}
	public String getContentType() {
		// NOTE: MAY NEED TO OVERRIDE FOR SearchURL or when there is a query string

		// Check if the file is hypertext or plain text 
		String ContentType;
		String file = url.toLowerCase();
		if (file.endsWith(".html") || file.endsWith(".htm"))
			return "text/html";
		else
			if (file.endsWith(".css"))
				return "text/css";
			else
				if (file.endsWith(".gif"))
					return "image/gif";
				else
					if (file.endsWith(".jpg"))
						return "image/jpeg";
					else
						if (file.endsWith(".pdf"))
							return "application/pdf";
						else
							if (file.endsWith(".xml"))
								return "application/xml";
							else
								if (file.endsWith(".xsl"))
									return "application/xsl";
		return "text/plain";
	}
	/**
	 * 
	 */
	public Vector getMultiValue(String name) {
		if (arguments != null) {
			Object value = arguments.get(name);
			if (value instanceof Vector)
				return (Vector) value;
			else
				return null;
		}
		return null;
	}
	public String getPath() {
		// The stored url starts with plugin id.
		// We need to return /pluginid/filename
		return "/" + url;
	}
	/**
	 * 
	 */
	public String getValue(String name) {
		if (arguments == null)
			return null;
		Object value = arguments.get(name);
		String stringValue = null;
		if (value instanceof String)
			stringValue = (String) value;
		else
			if (value instanceof Vector)
				stringValue = (String) ((Vector) value).firstElement();
			else
				return null;
		try {
			return URLDecoder.decode(stringValue);
		} catch (Exception e) {
			return null;
		}

	}
	// this returns whether or not a response created for a request
	// to this URL is cached by the browser client.
	public boolean isCacheable() {
		return false;
	}
	/**
	 * Opens a stream for reading.
	 * 
	 * @return java.io.InputStream
	 */
	public InputStream openStream() {
		return null;
	}
	/**
	 * NOTE: need to add support for multi-valued parameters (like filtering)
	 * Multiple values are added as vectors
	 */
	protected void parseQuery(String theQuery) {
		if (theQuery != null && !"".equals(theQuery)) {
			if (arguments == null) {
				arguments = new HashMap(5);
			}

			StringTokenizer stok = new StringTokenizer(theQuery, "&");
			while (stok.hasMoreTokens()) {
				String aQuery = stok.nextToken();
				int equalsPosition = aQuery.indexOf("=");
				if (equalsPosition > -1) { // well formed name/value pair
					String arg = aQuery.substring(0, equalsPosition);
					String val = aQuery.substring(equalsPosition + 1);
					Object existing = arguments.get(arg);
					if (existing == null)
						arguments.put(arg, val);
					else
						if (existing instanceof Vector) {
							((Vector) existing).add(val);
							arguments.put(arg, existing);
						} else {
							Vector v = new Vector(2);
							v.add(existing);
							v.add(val);
							arguments.put(arg, v);
						}
				}
			}
		}
	}
	public String toString() {
		return url;
	}
}
