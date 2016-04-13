package com.hybris.storefront.countryselector.strategies;


import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


public class CountrySelectorStrategy
{

	private static final Logger LOG = Logger.getLogger(CountrySelectorStrategy.class);

	@Resource
	private CMSSiteService cmsSiteService;
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private SessionService sessionService;


	public static final String EXTENSIONNAME = "posselector";
	public static final String SESSION_MY_LOCAL_STORE = "myLocalStore";

	// implement here constants used by this extension
	public static final String SESSION_SELECT_COUNTYR = "SELECTED_COUNTRY";

	public List<CMSSiteModel> getAllCMMSite()
	{
		final List<CMSSiteModel> sites = new ArrayList<>(cmsSiteService.getSites());

		return sites;

	}

	public CMSSiteModel getCurrentCMMSite()
	{
		final CustomerModel currentUser = (CustomerModel) userService.getCurrentUser();

		String siteUid = null;
		if (userService.isAnonymousUser(currentUser))
		{
			siteUid = getSiteUidForAnonymousUser(currentUser);
		}
		else
		{
			siteUid = getSiteUidForLoggedUser(currentUser);
		}

		return getSiteForUid(siteUid);
	}

	private String getSiteUidForLoggedUser(final CustomerModel currentUser)
	{
		return (currentUser.getSessionLanguage() != null) ? currentUser.getSite().getUid() : null;
	}

	private String getSiteUidForAnonymousUser(final CustomerModel anonymousUser)
	{
		return sessionService.getAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR);
	}


	public CMSSiteModel getSiteForUid(final String siteUid)
	{
		//fix for cms cockpit
		if (cmsSiteService.getCurrentSite() == null)
		{
			return null;
		}

		final List<CMSSiteModel> sites = new ArrayList<>(cmsSiteService.getSites());

		if (null == siteUid)
		{
			sessionService.setAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR, null);
			return sites.get(0);
		}

		for (final CMSSiteModel siteModel : sites)
		{
			if (siteModel.getUid().equals(siteUid))
			{
				sessionService.setAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR, siteUid);
				return siteModel;
			}
		}
		throw new IllegalStateException("Cannot find site with uid: " + siteUid + " on site:"
				+ cmsSiteService.getCurrentSite().getUid());
	}


	private BaseStoreModel getDefaultBaseStore()
	{
		//LOG.info("Using default base store for anonymous user");
		return cmsSiteService.getCurrentSite().getStores().get(0);
	}

}
