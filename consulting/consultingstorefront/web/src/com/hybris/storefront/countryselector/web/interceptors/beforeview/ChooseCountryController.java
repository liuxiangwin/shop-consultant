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
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
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
			//final ContentPageModel contentPageModel = cmsPageService.getPageByLabel("siteSelector");
			//return cmsPageService.getPageForLabelOrId("homepage");
			model.addAttribute("chooseUrl", "/main/chooseCountry");
			storeCmsPageInModel(model, contentPageModel);
			setUpMetaDataForContentPage(model, contentPageModel);
			updatePageTitle(model, contentPageModel);
			return getViewForPage(model);
		}
		else
		{
			if (selectCountry.equalsIgnoreCase("zh"))
			{
				//return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/zh-consultingsite/zh/Development/c/Development";
				return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/zh-consultingsite/zh/开发/c/Development";
			}
			else
			{
				//return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/uk-consultingsite/en/Development/c/Development";
				return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/uk-consultingsite/en/Development/c/Development";
			}
		}
	}


	@RequestMapping(value = "/changeCountry", method = RequestMethod.GET)
	public String changeCountryFromLink(final HttpServletRequest request, final Model model, final HttpServletResponse response)
			throws IOException, CMSItemNotFoundException
	{
		//The user purpose to open the choose page for another choice
		final ContentPageModel contentPageModel = cmsPageService.getPageByLabel("chooseCountryPage");
		model.addAttribute("chooseUrl", "/main/chooseCountry");
		storeCmsPageInModel(model, contentPageModel);
		setUpMetaDataForContentPage(model, contentPageModel);
		updatePageTitle(model, contentPageModel);
		return getViewForPage(model);
	}


	@RequestMapping(value = "/chooseCountry", method = RequestMethod.POST)
	public String chooseCountry(@RequestParam(value = "country", required = true) final String country, final Model model,
			final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException
	{
		final String cookiValue = country;
		cookieGenerator.addCookie(response, cookiValue);
		CookieUtils.addCookie(response, 360000, "country-selected", country);
		getSessionService().getCurrentSession().setAttribute(CountrySelectorStrategy.SESSION_SELECT_COUNTYR, country);

		if (country.equalsIgnoreCase("zh"))
		{
			return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/zh-consultingsite/zh/开发/c/Development";
			//return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/zh-consultingsite/zh/Development/c/Development";
		}
		else
		{
			return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/uk-consultingsite/en/Development/c/Development";
			//return REDIRECT_PREFIX + "https://localhost:9002/consultingstorefront/uk-consultingsite/en/Development/c/Development";
		}
	}

	protected void updatePageTitle(final Model model, final AbstractPageModel cmsPage)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveHomePageTitle(cmsPage.getTitle()));
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
}
