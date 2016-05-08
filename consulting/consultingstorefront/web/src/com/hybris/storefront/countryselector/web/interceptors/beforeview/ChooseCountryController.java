/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.hybris.storefront.countryselector.web.interceptors.beforeview;


import static com.hybris.storefront.util.CountryUtil.china_absoluteURL;
import static com.hybris.storefront.util.CountryUtil.china_categoryURL;
import static com.hybris.storefront.util.CountryUtil.uk_absoluteURL;
import static com.hybris.storefront.util.CountryUtil.uk_categoryURL;
import static com.hybris.storefront.util.CountryUtil.uk_local;
import static com.hybris.storefront.util.CountryUtil.zh_local;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.CookieGenerator;

import com.hybris.storefront.countryselector.strategies.CountrySelectorStrategy;
import com.hybris.storefront.util.CookieUtils;


/**
 * Controller for home page
 */
@Controller
@Scope("tenant")
@RequestMapping("/main")
public class ChooseCountryController extends AbstractPageController
{
	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "sessionCookieGenerator")
	private CookieGenerator cookieGenerator;

	@Resource
	private SessionService sessionService;

	@Resource(name = "cmsPageService")
	private CMSPageService cmsPageService;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Autowired
	private CountrySelectorStrategy countrySelectorStrategy;

	protected static final Logger LOG = Logger.getLogger(ChooseCountryController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String handleCountry(final HttpServletRequest request, final Model model, final HttpServletResponse response)
			throws IOException, CMSItemNotFoundException
	{
		final Cookie[] cookies = request.getCookies();
		Cookie cook;
		String selectCountry = "";
		if (cookies != null)
		{
			for (int i = 0; i < cookies.length; i++)
			{
				cook = cookies[i];
				if (cook.getName().equalsIgnoreCase("country-selected"))
				{
					selectCountry = cook.getValue();
				}

			}
		}
		if (selectCountry.equalsIgnoreCase(""))
		{
			final ContentPageModel contentPageModel = cmsPageService.getPageByLabel("chooseCountryPage");
			model.addAttribute("chooseUrl", "/main/chooseCountry");
			storeCmsPageInModel(model, contentPageModel);
			setUpMetaDataForContentPage(model, contentPageModel);
			updatePageTitle(model, contentPageModel);
			return getViewForPage(model);
		}
		else
		{
			return REDIRECT_PREFIX + "/main/handleRedirect?selectCountry=" + URLEncoder.encode(selectCountry, "UTF-8");
		}
	}

	public CMSSiteModel initializeSiteFromRequest(final String absoluteURL, final String categoryURL, final String locale)
	{
		try
		{
			final URL currentURL = new URL(absoluteURL);
			final CMSSiteModel cmsSiteModel = cmsSiteService.getSiteForURL(currentURL);
			if (cmsSiteModel != null)
			{
				cmsSiteModel.setLocale(locale);
				cmsSiteModel.setRedirectURL(categoryURL);
				baseSiteService.setCurrentBaseSite(cmsSiteModel, true);
				return cmsSiteModel;
			}
		}
		catch (final MalformedURLException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Cannot find CMSSite associated with current URL ( " + absoluteURL
						+ " - check whether this is correct URL) !");
			}
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.warn("Cannot find CMSSite associated with current URL (" + absoluteURL + ")!");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(e);
			}
		}
		return null;
	}

	@RequestMapping(value = "/handleRedirect", method = RequestMethod.GET)
	public void handleRedirect(@RequestParam(value = "selectCountry", required = true) final String selectCountry,
			final HttpServletRequest request, final Model model, final HttpServletResponse response) throws IOException,
			CMSItemNotFoundException
	{
		if (selectCountry.equalsIgnoreCase("zh"))
		{
			//return getViewForPage(china_absoluteURL, model, zh_local);
			getViewForPage(china_absoluteURL, model, zh_local, china_categoryURL, request, response);
		}
		else
		{
			//return getViewForPage(uk_absoluteURL, model, uk_local);
			getViewForPage(uk_absoluteURL, model, uk_local, uk_categoryURL, request, response);
		}
	}

	@RequestMapping(value = "/changeCountry", method = RequestMethod.GET)
	public String changeCountryFromLink(final HttpServletRequest request, final Model model, final HttpServletResponse response)
			throws IOException, CMSItemNotFoundException
	{
		final ContentPageModel contentPageModel = cmsPageService.getPageByLabel("chooseCountryPage");
		model.addAttribute("chooseUrl", "/main/chooseCountry");
		storeCmsPageInModel(model, contentPageModel);
		setUpMetaDataForContentPage(model, contentPageModel);
		updatePageTitle(model, contentPageModel);
		return getViewForPage(model);
	}


	@RequestMapping(value = "/chooseCountry", method = RequestMethod.POST)
	public void chooseCountry(@RequestParam(value = "country", required = true) final String country, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		final String cookiValue = country;
		cookieGenerator.addCookie(response, cookiValue);
		CookieUtils.addCookie(response, 3600, "country-selected", country);

		if (country.equalsIgnoreCase("zh"))
		{
			//return getViewForPage(china_absoluteURL, model, zh_local);
			getViewForPage(china_absoluteURL, model, zh_local, china_categoryURL, request, response);
		}
		else
		{
			//return getViewForPage(uk_absoluteURL, model, uk_local);
			getViewForPage(uk_absoluteURL, model, uk_local, uk_categoryURL, request, response);
		}
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
	}

	public void getViewForPage(final String absoluteURL, final Model model, final String locale, final String categoryURL,
			final HttpServletRequest httpRequest, final HttpServletResponse httpResponse)
	{
		initializeSiteFromRequest(absoluteURL, categoryURL, locale);
		//final ContentPageModel contentPageModel = cmsSiteModel.getStartingPage();
		//final ContentPageModel contentPageModel = null;
		CategoryPageModel catalogPageModel = null;
		try
		{
			//contentPageModel = cmsPageService.getPageByLabel("category");
			catalogPageModel = cmsPageService.getPageByCategoryCode("Development");
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.debug(e);
		}
		storeCmsPageInModel(model, catalogPageModel);
		//setUpMetaDataForContentPage(model, catalogPageModel);
		updatePageTitle(model, catalogPageModel);
		//Pattern like /{category-path}/c/{category-code}
		//return getViewForPage(model);

		//return REDIRECT_PREFIX + "/Development/c/Development";

		try
		{
			//final String redirectURL = "/Development/c/Development";
			//final String contextPath = httpRequest.getContextPath();
			//final String encodedRedirectUrl = httpResponse.encodeRedirectURL(contextPath + redirectURL);
			httpResponse.sendRedirect(categoryURL);
			//httpResponse.sendRedirect(redirectURL);
		}
		catch (final IOException e)
		{
			LOG.debug(e);
		}

	}

	public String beforeRender(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{
		String cmsSite = "";
		if (sessionService.getCurrentSession().getAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR) != null)
		{
			cmsSite = sessionService.getCurrentSession().getAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR);
			return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/" + cmsSite + "?clear=true&site=" + "cmsSite";
		}
		else
		{
			//There is no country in session memory
			model.addAttribute("sites", countrySelectorStrategy.getAllCMMSite());
			storeCmsPageInModel(model, getContentPageForLabelOrId(null));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
			return "pages/choosecounty/chooseCountry";
		}
	}

	private ContentPageModel getHomePageRender() throws CMSItemNotFoundException
	{
		//final ContentPageModel contentPageModel = cmsPageService.getPageByLabel("siteSelector");
		return cmsPageService.getPageForLabelOrId("homepage");
	}
}
//http://stackoverflow.com/questions/16189365/browser-not-showing-correct-url-even-after-using-response-sendredirect-method