package org.eclipse.help.internal.navigation;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

import java.io.*;
import java.util.*;
import org.eclipse.help.internal.HelpSystem;
import org.eclipse.core.runtime.IPath;
import org.eclipse.help.internal.contributors.*;
import org.eclipse.help.internal.contributions.*;
import org.eclipse.help.internal.contributors.xml.*;
import org.eclipse.help.internal.util.*;

/**
 * Manages the navigation model. It generates it and it reas it back
 * and instantiates the model for future rendering.
 * There is a model (notifier) for each <views> node.
 */
public class HelpNavigationManager {
	private NavigationModel currentModel;
	private Map navigationModels = new HashMap(/* of NavigationModel */);

	public final static String INFOSETS_FILE = "infosets";

	private String currentInfosetId;

	private Collection validInfosetIds = new ArrayList();

	/**
	 * HelpNavigationManager constructor comment.
	 */
	public HelpNavigationManager() {
		super();

		try {
			// build collection of all valid (installed) infosets
			ContributionManager cmgr = HelpSystem.getContributionManager();
			Iterator infoSetContributors =
				cmgr.getContributionsOfType(ViewContributor.INFOSET_ELEM);
			while (infoSetContributors.hasNext()) {
				InfoSet infoset = (InfoSet) infoSetContributors.next();
				String infosetId = infoset.getID();
				if (infosetId == null)
					continue;
				//if (!infoset.isStandalone() || !isEmpty(infosetId))
				validInfosetIds.add(infosetId);
			}

			// build all the info sets: build the structure and generate the xml's
			// Note: it is cheaper to do all the info sets now, since we've taken the hit to
			//       to some extra processing in parsing actions, etc.
			//       Also, in most cases there is only one info set.
			if (cmgr.hasNewContributions()) {
				// suggest memory cleanup, as we're going to use a bit of it
				System.gc();

				createNavigationModels();
				cmgr.versionContributions();

				// attemp to cleanup all the memory no longer needed
				//System.gc();
			}
		} catch (Exception e) {
			Logger.logError(e.getMessage(), e);
		}
	}
	private void createNavigationModels() {
		try {
			// Keep track of all the infosets available
			PersistentMap infosetsMap = new PersistentMap(INFOSETS_FILE);

			ContributionManager cmgr = HelpSystem.getContributionManager();
			InfosetBuilder builder = new InfosetBuilder(cmgr);
			// merges all topics into views 
			Map infosets = builder.buildInformationSets();
			for (Iterator it = infosets.values().iterator(); it.hasNext();) {
				InfoSet infoset = (InfoSet) it.next();
				NavigationModel m = new NavigationModel(infoset);
				navigationModels.put(infoset.getID(), m);

				// generate navigation file for each infoset
				generateNavForInfoSet(infoset);

				infosetsMap.put(infoset.getID(), infoset.getLabel());
			}
			// Save a file with all the infosets ids and labels
			infosetsMap.save();
		} catch (Exception e) {
			Logger.logError(e.getMessage(), e);
		}
	}
	/**
	 * @param viewSet com.ibm.itp.ua.view.ViewSet
	 * @param outputDir java.io.File
	 */
	private void generateInfoSetNav(InfoSet infoSet, File outputDir) {
		XMLNavGenerator navGen = new XMLNavGenerator(infoSet, outputDir);
		navGen.generate();
	}
	/**
	 * @param viewSet com.ibm.itp.ua.view.ViewSet
	 */
	private void generateNavForInfoSet(InfoSet infoSet) {
		IPath path =
			HelpSystem
				.getPlugin()
				.getStateLocation()
				.addTrailingSeparator()
				.append(infoSet.getID());

		File outDir = path.toFile();
		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		generateInfoSetNav(infoSet, outDir);
	}
	/**
	 * Returns the current navigation model
	 */
	public InfoSet getCurrentInfoSet() {
		NavigationModel navModel = getCurrentNavigationModel();
		if (navModel == null)
			return null;
		else
			return (InfoSet) navModel.getRootElement();
	}
	/**
	 * Returns the current navigation model
	 */
	public NavigationModel getCurrentNavigationModel() {
		if (currentModel == null) {
			// no previous InfoSet loaded, find an InfoSet
			/*ContributionManager cmgr = HelpSystem.getContributionManager();
			Iterator infoSetContributors =
						cmgr.getContributionsOfType(ViewContributor.INFOSET_ELEM);
			if (infoSetContributors.hasNext()) 
				setCurrentInfoSet(((InfoSet) infoSetContributors.next()).getID());*/
			if (validInfosetIds.size() > 0)
				setCurrentInfoSet((String) validInfosetIds.iterator().next());
		}
		return currentModel;
	}
	/**
	 * Returns the navigation model for an infoset
	 */
	public InfoSet getInfoSet(String id) {
		NavigationModel navModel = getNavigationModel(id);
		if (navModel == null)
			return null;
		else
			return (InfoSet) navModel.getRootElement();
	}
	/**
	 * Returns the navigation model for an infoset
	 */
	public NavigationModel getNavigationModel(String id) {
		if (id == null || id.equals(""))
			return null;

		// First check the cache
		NavigationModel m = (NavigationModel) navigationModels.get(id);
		if (m == null && validInfosetIds.contains(id)) {
			m = new NavigationModel(id);
			navigationModels.put(id, m);
		}
		return m;
	}
	/**
	 * Sets the current model
	 */
	public void setCurrentInfoSet(String infosetId) {
		if (validInfosetIds.contains(infosetId)) {
			// Set global infoset and navigation model
			currentInfosetId = infosetId;
			// Set the current navigation model.
			// Side effect: the model may be loaded at this time.
			currentModel = getNavigationModel(infosetId);
		}
	}
	/**
	 * Sets the current model
	 */
	public void setCurrentNavigationModel(NavigationModel m) {
		currentModel = m;
	}
}
