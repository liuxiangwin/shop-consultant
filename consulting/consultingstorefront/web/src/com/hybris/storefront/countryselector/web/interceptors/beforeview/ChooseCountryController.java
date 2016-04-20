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


import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


	@Autowired
	private CountrySelectorStrategy countrySelectorStrategy;


	@RequestMapping(method = RequestMethod.GET)
	public void setSiteWithCookie(final HttpServletRequest request, final Model model, final HttpServletResponse response)
			throws IOException
	{
		final ContentPageModel contentPageModel = cmsPageService.getPageByLabel("chooseCountryPage");
		//return cmsPageService.getPageForLabelOrId("homepage");
		storeCmsPageInModel(model, contentPageModel);

		final String country = request.getParameter("country");
		final String lang = request.getParameter("lang");
		final String cookieValue = country + "_" + lang;

		cookieGenerator.addCookie(response, cookieValue);

		cookieGenerator.getCookieName();
		response.sendRedirect("https://localhost:9002/consultantstorefront/?site=consultant" + country + "&lang=" + lang);

		//https://localhost:9002/consultingstorefront/zh-consultingsite/en/?clear=true&site=zh-consultingsite


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
			//updatePageTitle(model, getContentPageForLabelOrId(null));
			return "pages/choosecounty/chooseCountry";
		}
	}



	@RequestMapping(value = "/chooseCountry", method = RequestMethod.POST)
	public String chooseCountry(@RequestParam(value = "sessionCountry", required = true) final String sessionCountry,
			final Model model, final HttpServletRequest request) throws CMSItemNotFoundException
	{

		String cmsSite = "";

		if (sessionService.getCurrentSession().getAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR) != null)
		{

			cmsSite = sessionService.getCurrentSession().getAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR);

			if (cmsSite.equals(sessionCountry))
			{
				return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/" + cmsSite + "?clear=true&site=" + cmsSite;
			}
			else
			{
				return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/" + sessionCountry + "?clear=true&site="
						+ sessionCountry;
			}
		}
		else
		{
			//There is no country in session memory
			model.addAttribute("sites", countrySelectorStrategy.getAllCMMSite());
			storeCmsPageInModel(model, getContentPageForLabelOrId(null));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(null));
			//updatePageTitle(model, getContentPageForLabelOrId(null));
			return "pages/choosecounty/chooseCountry";
		}
	}



	//@RequestMapping(value = "/choosecountry", method = RequestMethod.GET)
	public String setCountryWithCookie(@RequestParam("country") final String country, final Model model,
			final HttpServletResponse response, final HttpServletRequest request) throws IOException
	{
		final String actualLanguage = storeSessionFacade.getCurrentLanguage().getIsocode();

		cookieGenerator.addCookie(response, country);

		CookieUtils.addCookie(response, 360000, "country", country);

		getSessionService().getCurrentSession().setAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR, country);

		return (country.isEmpty() ? getBaseReturnURL(request, country) : getNewReturnURL(request, country));
	}


	protected String getNewReturnURL(final HttpServletRequest request, final String country)
	{
		return REDIRECT_PREFIX + "https://localhost:9002/conshop/" + country + "/?site=conshop";
	}

	protected String getBaseReturnURL(final HttpServletRequest request, final String check)
	{
		if (check != null && check.trim().length() > 0)
		{
			return getNewReturnURL(request, check);
		}
		return REDIRECT_PREFIX + "https://localhost:9002/conshop/en/?site=conshop";
	}
}
