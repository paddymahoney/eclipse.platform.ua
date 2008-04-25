/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.help.internal.webapp.servlet;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.ITopic;
import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.help.internal.base.HelpBasePlugin;
import org.eclipse.help.internal.base.IHelpBaseConstants;
import org.eclipse.help.internal.webapp.HelpWebappPlugin;
import org.eclipse.help.internal.webapp.data.UrlUtil;

/**
 * Injects breadcrumbs at the top of help documents, e.g.
 * "Workbench User Guide > Concepts > Workbench".
 */
public class BreadcrumbsFilter implements IFilter {

	private static final String HEAD_CONTENT =
			"\n<link rel=\"stylesheet\" href=\"../content/PLUGINS_ROOT/" + HelpWebappPlugin.PLUGIN_ID + "/advanced/breadcrumbs.css\" charset=\"ISO-8859-1\" type=\"text/css\"></link>"  //$NON-NLS-1$//$NON-NLS-2$
			+ "\n<script type=\"text/javascript\" src=\"../content/PLUGINS_ROOT/" + HelpPlugin.PLUGIN_ID + "/livehelp.js\"> </script>"; //$NON-NLS-1$ //$NON-NLS-2$

	/* (non-Javadoc)
	 * @see org.eclipse.help.internal.webapp.servlet.IFilter#filter(javax.servlet.http.HttpServletRequest, java.io.OutputStream)
	 */
	public OutputStream filter(HttpServletRequest req, OutputStream out) {
		String uri = req.getRequestURI();
		if (uri == null || !uri.endsWith("html") && !uri.endsWith("htm") && !UrlUtil.isNavPath(uri)) { //$NON-NLS-1$ //$NON-NLS-2$
			return out;
		}
		if ("/rtopic".equals(req.getServletPath())) { //$NON-NLS-1$
			return out;
		}
		if (UrlUtil.isBot(req)) {
			return out;
		}
		String pathInfo = req.getPathInfo();
		String servletPath = req.getServletPath();
		if (pathInfo == null || servletPath == null) {
			return out;
		}
		Preferences prefs = HelpBasePlugin.getDefault().getPluginPreferences();
		String showBreadcrumbs = prefs.getString(IHelpBaseConstants.P_SHOW_BREADCRUMBS);
		if ("false".equalsIgnoreCase(showBreadcrumbs)) { //$NON-NLS-1$
			try {
				return new FilterHTMLHeadOutputStream(out, HEAD_CONTENT.getBytes("ASCII")); //$NON-NLS-1$
			}
			catch (UnsupportedEncodingException e) {
			}
		}
		// Use pathInfo to get the topic path because the uri could have escaped spaces
		// or other characters, Bug 75360
		int[] path = UrlUtil.getTopicPath(servletPath + pathInfo);
		if (path != null && path.length > 1) {
			boolean isNarrow = "/ntopic".equals(req.getServletPath()); //$NON-NLS-1$
			String locale = UrlUtil.getLocale(req, null);
			String bodyContent = getBodyContent(path, getBackpath(pathInfo), isNarrow, locale);
			try {
				return new FilterHTMLHeadAndBodyOutputStream(out, HEAD_CONTENT.getBytes("ASCII"), bodyContent); //$NON-NLS-1$
			}
			catch (UnsupportedEncodingException e) {
			}
		}
		return out;
	}
	
	private String getBackpath(String path) {
		int num = new Path(path).segmentCount();
		StringBuffer buf = new StringBuffer();
		for (int i=0;i<num;++i) {
			if (i > 0) {
				buf.append('/');
			}
			buf.append(".."); //$NON-NLS-1$
		}
		return buf.toString();
	}
	
	private String getBodyContent(int[] path, String backPath, boolean isNarrow, String locale) {
		StringBuffer buf = new StringBuffer();
		StringBuffer pathBuf = new StringBuffer();
		ITopic topic = HelpPlugin.getTocManager().getTocs(locale)[path[0]].getTopic(null);
		pathBuf.append(path[0]);
		buf.append("<div class=\"help_breadcrumbs\">"); //$NON-NLS-1$
		
		for (int i=0;i<path.length-1;++i) {
			
			// add the link
			buf.append("<a href=\""); //$NON-NLS-1$
			String href = topic.getHref();
			if (href != null) {
				href = backPath + (isNarrow ? "/ntopic" : "/topic") + href; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				if (isNarrow) {
					href = "javascript:liveAction('org.eclipse.help.ui', 'org.eclipse.help.ui.internal.ShowInTocAction', '" + pathBuf.toString() + "')"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					href = backPath + "/nav/" + pathBuf.toString(); //$NON-NLS-1$
				}
			}
			buf.append(href);
			buf.append("\">"); //$NON-NLS-1$
			buf.append(topic.getLabel());
			buf.append("</a>"); //$NON-NLS-1$
			
			// add separator
			if (i < path.length - 2 || path.length == 2) {
				// always add if there's only one link
				buf.append(" > "); //$NON-NLS-1$
			}
			
			// move to the next topic in the path
			topic = topic.getSubtopics()[path[i + 1]];
			pathBuf.append('_');
			pathBuf.append(path[i + 1]);
		}
		buf.append("</div>"); //$NON-NLS-1$
		return buf.toString();
	}
}